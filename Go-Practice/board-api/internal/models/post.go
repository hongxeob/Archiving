package models

import (
	"time"
)

// Post는 게시글 모델 구조체
// Spring Boot의 @Entity와 유사하지만 어노테이션 없이 순수 구조체
type Post struct {
	ID        int64     `json:"id" db:"id"`
	Title     string    `json:"title" db:"title"`
	Content   string    `json:"content" db:"content"`
	Author    string    `json:"author" db:"author"`
	ViewCount int32     `json:"view_count" db:"view_count"`
	CreatedAt time.Time `json:"created_at" db:"created_at"`
	UpdatedAt time.Time `json:"updated_at" db:"updated_at"`
}

// CreatePostRequest는 게시글 생성 요청 DTO
// Spring Boot의 @RequestBody와 유사한 용도
type CreatePostRequest struct {
	Title   string `json:"title" validate:"required,min=1,max=255"`
	Content string `json:"content"`
	Author  string `json:"author" validate:"required,min=1,max=100"`
}

// UpdatePostRequest는 게시글 수정 요청 DTO
type UpdatePostRequest struct {
	Title   string `json:"title" validate:"required,min=1,max=255"`
	Content string `json:"content"`
}

// PostResponse는 게시글 응답 DTO
// 필요한 필드만 노출하여 데이터 은닉
type PostResponse struct {
	ID        int64     `json:"id"`
	Title     string    `json:"title"`
	Content   string    `json:"content"`
	Author    string    `json:"author"`
	ViewCount int32     `json:"view_count"`
	CreatedAt time.Time `json:"created_at"`
	UpdatedAt time.Time `json:"updated_at"`
}

// PostListResponse는 게시글 목록 응답 DTO
type PostListResponse struct {
	Posts      []PostResponse `json:"posts"`
	TotalCount int64          `json:"total_count"`
	Page       int            `json:"page"`
	Size       int            `json:"size"`
}

// SearchRequest는 게시글 검색 요청 DTO
type SearchRequest struct {
	Query string `json:"query"`
	Page  int    `json:"page"`
	Size  int    `json:"size"`
}

// ToPostResponse는 Post 엔티티를 PostResponse DTO로 변환
// Spring Boot의 ModelMapper나 MapStruct와 유사한 기능을 수동으로 구현
func (p *Post) ToPostResponse() PostResponse {
	return PostResponse{
		ID:        p.ID,
		Title:     p.Title,
		Content:   p.Content,
		Author:    p.Author,
		ViewCount: p.ViewCount,
		CreatedAt: p.CreatedAt,
		UpdatedAt: p.UpdatedAt,
	}
}

// Validate는 CreatePostRequest의 유효성을 검증
// Spring Boot Validation의 @Valid와 유사한 기능을 수동 구현
func (req *CreatePostRequest) Validate() error {
	if req.Title == "" {
		return NewValidationError("title", "제목은 필수입니다")
	}
	if len(req.Title) > 255 {
		return NewValidationError("title", "제목은 255자를 초과할 수 없습니다")
	}
	if req.Author == "" {
		return NewValidationError("author", "작성자는 필수입니다")
	}
	if len(req.Author) > 100 {
		return NewValidationError("author", "작성자는 100자를 초과할 수 없습니다")
	}
	return nil
}

// Validate는 UpdatePostRequest의 유효성을 검증
func (req *UpdatePostRequest) Validate() error {
	if req.Title == "" {
		return NewValidationError("title", "제목은 필수입니다")
	}
	if len(req.Title) > 255 {
		return NewValidationError("title", "제목은 255자를 초과할 수 없습니다")
	}
	return nil
}
