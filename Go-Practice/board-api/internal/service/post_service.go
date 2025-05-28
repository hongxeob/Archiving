package service

import (
	"context"
	"database/sql"
	"encoding/json"
	"fmt"
	"time"

	"board-api/internal/models"
	"board-api/internal/repository"
	"board-api/internal/utils"

	"github.com/redis/go-redis/v9"
	"go.uber.org/zap"
)

// PostService는 게시글 비즈니스 로직을 담당하는 서비스
// Spring Boot의 @Service와 유사한 역할
type PostService struct {
	queries *repository.Queries
	logger  *zap.Logger
	redis   *redis.Client
}

// PostServiceInterface는 PostService의 인터페이스
// Go의 관례: 구현체가 아닌 사용하는 쪽에서 인터페이스 정의
type PostServiceInterface interface {
	GetPost(ctx context.Context, id int64) (*models.PostResponse, error)
	GetPosts(ctx context.Context, page, size int) (*models.PostListResponse, error)
	CreatePost(ctx context.Context, req *models.CreatePostRequest) (*models.PostResponse, error)
	UpdatePost(ctx context.Context, id int64, req *models.UpdatePostRequest) (*models.PostResponse, error)
	DeletePost(ctx context.Context, id int64) error
	SearchPosts(ctx context.Context, query string, page, size int) (*models.PostListResponse, error)
}

// NewPostService는 PostService 인스턴스를 생성
// Spring Boot의 생성자 주입과 유사하지만 더 명시적
// sqlc가 생성한 *repository.Queries를 주입받습니다
func NewPostService(queries *repository.Queries, logger *zap.Logger, redis *redis.Client) PostServiceInterface {
	return &PostService{
		queries: queries,
		logger:  logger,
		redis:   redis,
	}
}

// GetPost는 ID로 게시글을 조회하고 조회수를 증가시킴
// Spring Boot의 @Transactional과 유사한 기능을 수동 구현 필요
func (s *PostService) GetPost(ctx context.Context, id int64) (*models.PostResponse, error) {
	// 캐시에서 먼저 확인 (Redis 활용)
	cacheKey := fmt.Sprintf("post:%d", id)

	// Redis에서 캐시된 데이터 조회
	cached, err := s.redis.Get(ctx, cacheKey).Result()
	if err == nil {
		var post models.PostResponse
		if json.Unmarshal([]byte(cached), &post) == nil {
			s.logger.Info("Cache hit for post", zap.Int64("post_id", id))

			// 조회수 증가 (비동기로 처리)
			go func() {
				ctx := context.Background()
				_, err := s.queries.IncrementViewCount(ctx, id)
				if err != nil {
					s.logger.Error("Failed to increment view count",
						zap.Int64("post_id", id),
						zap.Error(err))
				}
			}()

			return &post, nil
		}
	}

	// Go의 엄격한 타입 시스템: int64 → int32 명시적 변환 필요
	// Java와 달리 자동 타입 변환이 불가능하므로 안전성 확보
	dbPost, err := s.queries.GetPost(ctx, id)
	if err != nil {
		if err == sql.ErrNoRows {
			s.logger.Warn("Post not found", zap.Int64("post_id", id))
			return nil, models.ErrPostNotFound
		}
		s.logger.Error("Failed to get post from database",
			zap.Int64("post_id", id),
			zap.Error(err))
		return nil, models.ErrDatabase
	}

	// 조회수 증가
	viewCount, err := s.queries.IncrementViewCount(ctx, id)
	if err != nil {
		s.logger.Error("Failed to increment view count",
			zap.Int64("post_id", id),
			zap.Error(err))
		// 조회수 증가 실패해도 게시글 조회는 성공으로 처리
	} else {
		dbPost.ViewCount = viewCount
	}

	// 모델 변환
	post := models.Post{
		ID:        dbPost.ID,
		Title:     dbPost.Title,
		Content:   dbPost.Content,
		Author:    dbPost.Author,
		ViewCount: dbPost.ViewCount,
		CreatedAt: dbPost.CreatedAt,
		UpdatedAt: dbPost.UpdatedAt,
	}

	response := post.ToPostResponse()

	// Redis에 캐시 저장 (5분)
	postJSON, _ := json.Marshal(response)
	s.redis.Set(ctx, cacheKey, postJSON, 5*time.Minute)

	s.logger.Info("Successfully retrieved post",
		zap.Int64("post_id", id),
		zap.String("title", response.Title))

	return &response, nil
}

// GetPosts는 페이지네이션된 게시글 목록을 조회
// Spring Data JPA의 Pageable과 유사한 페이징 처리
func (s *PostService) GetPosts(ctx context.Context, page, size int) (*models.PostListResponse, error) {
	// 기본값 설정
	if page < 1 {
		page = 1
	}
	if size < 1 || size > 100 {
		size = 10
	}

	offset := (page - 1) * size

	// 게시글 목록 조회
	posts, err := s.queries.GetPosts(ctx, repository.GetPostsParams{
		Limit:  size,
		Offset: offset,
	})
	if err != nil {
		s.logger.Error("Failed to get posts", zap.Error(err))
		return nil, models.ErrDatabase
	}

	// 전체 개수 조회
	totalCount, err := s.queries.GetPostsCount(ctx)
	if err != nil {
		s.logger.Error("Failed to get posts count", zap.Error(err))
		return nil, models.ErrDatabase
	}

	// DTO 변환
	postResponses := make([]models.PostResponse, len(posts))
	for i, post := range posts {
		modelPost := models.Post{
			ID:        post.ID,
			Title:     post.Title,
			Content:   post.Content,
			Author:    post.Author,
			ViewCount: post.ViewCount,
			CreatedAt: post.CreatedAt,
			UpdatedAt: post.UpdatedAt,
		}
		postResponses[i] = modelPost.ToPostResponse()
	}

	response := &models.PostListResponse{
		Posts:      postResponses,
		TotalCount: totalCount,
		Page:       page,
		Size:       size,
	}

	s.logger.Info("Successfully retrieved posts",
		zap.Int("page", page),
		zap.Int("size", size),
		zap.Int64("total_count", totalCount))

	return response, nil
}

// CreatePost는 새 게시글을 생성
// Spring Boot의 @Valid와 유사한 유효성 검사를 수동 구현
func (s *PostService) CreatePost(ctx context.Context, req *models.CreatePostRequest) (*models.PostResponse, error) {
	// 유효성 검사
	if err := req.Validate(); err != nil {
		s.logger.Warn("Invalid create post request", zap.Error(err))
		return nil, err
	}

	// 데이터베이스에 저장
	dbPost, err := s.queries.CreatePost(ctx, repository.CreatePostParams{
		Title:   req.Title,
		Content: sql.NullString{String: req.Content, Valid: req.Content != ""},
		Author:  req.Author,
	})
	if err != nil {
		s.logger.Error("Failed to create post",
			zap.String("title", req.Title),
			zap.Error(err))
		return nil, models.ErrDatabase
	}

	// 모델 변환
	post := models.Post{
		ID:        dbPost.ID,
		Title:     dbPost.Title,
		Content:   dbPost.Content.String,
		Author:    dbPost.Author,
		ViewCount: dbPost.ViewCount,
		CreatedAt: dbPost.CreatedAt,
		UpdatedAt: dbPost.UpdatedAt,
	}

	response := post.ToPostResponse()

	s.logger.Info("Successfully created post",
		zap.Int64("post_id", response.ID),
		zap.String("title", response.Title),
		zap.String("author", response.Author))

	return &response, nil
}

// UpdatePost는 기존 게시글을 수정
func (s *PostService) UpdatePost(ctx context.Context, id int64, req *models.UpdatePostRequest) (*models.PostResponse, error) {
	// 유효성 검사
	if err := req.Validate(); err != nil {
		s.logger.Warn("Invalid update post request", zap.Error(err))
		return nil, err
	}

	// 게시글 존재 여부 확인 (타입 변환 적용)
	_, err := s.queries.GetPost(ctx, id)
	if err != nil {
		if err == sql.ErrNoRows {
			return nil, models.ErrPostNotFound
		}
		return nil, models.ErrDatabase
	}

	// 게시글 수정 (타입 변환 적용)
	dbPost, err := s.queries.UpdatePost(ctx, repository.UpdatePostParams{
		ID:      id,
		Title:   req.Title,
		Content: sql.NullString{String: req.Content, Valid: req.Content != ""},
	})
	if err != nil {
		s.logger.Error("Failed to update post",
			zap.Int64("post_id", id),
			zap.Error(err))
		return nil, models.ErrDatabase
	}

	// 캐시 무효화
	cacheKey := fmt.Sprintf("post:%d", id)
	s.redis.Del(ctx, cacheKey)

	// 모델 변환
	post := models.Post{
		ID:        int64(dbPost.ID), // int32 → int64 변환
		Title:     dbPost.Title,
		Content:   dbPost.Content.String,
		Author:    dbPost.Author,
		ViewCount: dbPost.ViewCount,
		CreatedAt: dbPost.CreatedAt,
		UpdatedAt: dbPost.UpdatedAt,
	}

	response := post.ToPostResponse()

	s.logger.Info("Successfully updated post",
		zap.Int64("post_id", id),
		zap.String("title", response.Title))

	return &response, nil
}

// DeletePost는 게시글을 삭제
func (s *PostService) DeletePost(ctx context.Context, id int64) error {
	// 게시글 존재 여부 확인 (타입 변환 적용)
	_, err := s.queries.GetPost(ctx, id)
	if err != nil {
		if err == sql.ErrNoRows {
			return models.ErrPostNotFound
		}
		return models.ErrDatabase
	}

	// 게시글 삭제 (타입 변환 적용)
	err = s.queries.DeletePost(ctx, id)
	if err != nil {
		s.logger.Error("Failed to delete post",
			zap.Int64("post_id", id),
			zap.Error(err))
		return models.ErrDatabase
	}

	// 캐시 무효화
	cacheKey := fmt.Sprintf("post:%d", id)
	s.redis.Del(ctx, cacheKey)

	s.logger.Info("Successfully deleted post", zap.Int64("post_id", id))

	return nil
}

// SearchPosts는 제목과 내용에서 키워드를 검색
func (s *PostService) SearchPosts(ctx context.Context, query string, page, size int) (*models.PostListResponse, error) {
	// 기본값 설정
	if page < 1 {
		page = 1
	}
	if size < 1 || size > 100 {
		size = 10
	}

	offset := (page - 1) * size

	// 검색 실행
	posts, err := s.queries.SearchPosts(ctx, repository.SearchPostsParams{
		Column1: query,
		Limit:   size,
		Offset:  offset,
	})
	if err != nil {
		s.logger.Error("Failed to search posts",
			zap.String("query", query),
			zap.Error(err))
		return nil, models.ErrDatabase
	}

	// DTO 변환
	postResponses := make([]models.PostResponse, len(posts))
	for i, post := range posts {
		modelPost := models.Post{
			ID:        post.ID,
			Title:     post.Title,
			Content:   post.Content,
			Author:    post.Author,
			ViewCount: post.ViewCount,
			CreatedAt: post.CreatedAt,
			UpdatedAt: post.UpdatedAt,
		}
		postResponses[i] = modelPost.ToPostResponse()
	}

	response := &models.PostListResponse{
		Posts:      postResponses,
		TotalCount: int64(len(posts)), // 실제로는 별도 카운트 쿼리 필요
		Page:       page,
		Size:       size,
	}

	s.logger.Info("Successfully searched posts",
		zap.String("query", query),
		zap.Int("results", len(posts)))

	return response, nil
}
