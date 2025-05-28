package handler

import (
	"encoding/json"
	"net/http"
	"strconv"
	"time"

	"board-api/internal/models"
	"board-api/internal/service"

	"github.com/gorilla/mux"
	"go.uber.org/zap"
)

// PostHandler는 게시글 HTTP 요청을 처리하는 핸들러
// Spring Boot의 @RestController와 유사한 역할
type PostHandler struct {
	postService service.PostServiceInterface
	logger      *zap.Logger
}

// NewPostHandler는 PostHandler 인스턴스를 생성
func NewPostHandler(postService service.PostServiceInterface, logger *zap.Logger) *PostHandler {
	return &PostHandler{
		postService: postService,
		logger:      logger,
	}
}

// RegisterRoutes는 라우터에 엔드포인트를 등록
// Spring Boot의 @RequestMapping과 유사하지만 더 명시적
func (h *PostHandler) RegisterRoutes(router *mux.Router) {
	// /api/v1/posts 그룹
	api := router.PathPrefix("/api/v1").Subrouter()

	// 게시글 관련 라우트
	api.HandleFunc("/posts", h.GetPosts).Methods("GET")
	api.HandleFunc("/posts", h.CreatePost).Methods("POST")
	api.HandleFunc("/posts/search", h.SearchPosts).Methods("GET")
	api.HandleFunc("/posts/{id:[0-9]+}", h.GetPost).Methods("GET")
	api.HandleFunc("/posts/{id:[0-9]+}", h.UpdatePost).Methods("PUT")
	api.HandleFunc("/posts/{id:[0-9]+}", h.DeletePost).Methods("DELETE")
}

// GetPost은 개별 게시글을 조회
// Spring Boot: @GetMapping("/posts/{id}")와 유사
func (h *PostHandler) GetPost(w http.ResponseWriter, r *http.Request) {
	vars := mux.Vars(r)
	id, err := strconv.ParseInt(vars["id"], 10, 64)
	if err != nil {
		h.logger.Warn("Invalid post ID", zap.String("id", vars["id"]))
		h.writeErrorResponse(w, r.URL.Path, models.ErrInvalidRequest)
		return
	}

	post, err := h.postService.GetPost(r.Context(), id)
	if err != nil {
		if apiErr, ok := err.(*models.APIError); ok {
			h.writeErrorResponse(w, r.URL.Path, apiErr)
		} else {
			h.logger.Error("Unexpected error getting post", zap.Error(err))
			h.writeErrorResponse(w, r.URL.Path, models.ErrInternalServer)
		}
		return
	}

	h.writeSuccessResponse(w, post)
}

// GetPosts는 게시글 목록을 조회 (페이징 지원)
// Spring Boot: @GetMapping("/posts") + Pageable과 유사
func (h *PostHandler) GetPosts(w http.ResponseWriter, r *http.Request) {
	// 쿼리 파라미터 파싱
	page, _ := strconv.Atoi(r.URL.Query().Get("page"))
	size, _ := strconv.Atoi(r.URL.Query().Get("size"))

	posts, err := h.postService.GetPosts(r.Context(), page, size)
	if err != nil {
		if apiErr, ok := err.(*models.APIError); ok {
			h.writeErrorResponse(w, r.URL.Path, apiErr)
		} else {
			h.logger.Error("Unexpected error getting posts", zap.Error(err))
			h.writeErrorResponse(w, r.URL.Path, models.ErrInternalServer)
		}
		return
	}

	h.writeSuccessResponse(w, posts)
}

// CreatePost는 새 게시글을 생성
// Spring Boot: @PostMapping("/posts") + @RequestBody와 유사
func (h *PostHandler) CreatePost(w http.ResponseWriter, r *http.Request) {
	var req models.CreatePostRequest

	// JSON 디코딩
	if err := json.NewDecoder(r.Body).Decode(&req); err != nil {
		h.logger.Warn("Invalid JSON in create post request", zap.Error(err))
		h.writeErrorResponse(w, r.URL.Path, models.NewValidationError("body", "유효하지 않은 JSON 형식입니다"))
		return
	}

	post, err := h.postService.CreatePost(r.Context(), &req)
	if err != nil {
		if apiErr, ok := err.(*models.APIError); ok {
			h.writeErrorResponse(w, r.URL.Path, apiErr)
		} else {
			h.logger.Error("Unexpected error creating post", zap.Error(err))
			h.writeErrorResponse(w, r.URL.Path, models.ErrInternalServer)
		}
		return
	}

	// 201 Created 응답
	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusCreated)
	json.NewEncoder(w).Encode(post)
}

// UpdatePost는 기존 게시글을 수정
// Spring Boot: @PutMapping("/posts/{id}") + @RequestBody와 유사
func (h *PostHandler) UpdatePost(w http.ResponseWriter, r *http.Request) {
	vars := mux.Vars(r)
	id, err := strconv.ParseInt(vars["id"], 10, 64)
	if err != nil {
		h.logger.Warn("Invalid post ID", zap.String("id", vars["id"]))
		h.writeErrorResponse(w, r.URL.Path, models.ErrInvalidRequest)
		return
	}

	var req models.UpdatePostRequest
	if err := json.NewDecoder(r.Body).Decode(&req); err != nil {
		h.logger.Warn("Invalid JSON in update post request", zap.Error(err))
		h.writeErrorResponse(w, r.URL.Path, models.NewValidationError("body", "유효하지 않은 JSON 형식입니다"))
		return
	}

	post, err := h.postService.UpdatePost(r.Context(), id, &req)
	if err != nil {
		if apiErr, ok := err.(*models.APIError); ok {
			h.writeErrorResponse(w, r.URL.Path, apiErr)
		} else {
			h.logger.Error("Unexpected error updating post", zap.Error(err))
			h.writeErrorResponse(w, r.URL.Path, models.ErrInternalServer)
		}
		return
	}

	h.writeSuccessResponse(w, post)
}

// DeletePost는 게시글을 삭제
// Spring Boot: @DeleteMapping("/posts/{id}")와 유사
func (h *PostHandler) DeletePost(w http.ResponseWriter, r *http.Request) {
	vars := mux.Vars(r)
	id, err := strconv.ParseInt(vars["id"], 10, 64)
	if err != nil {
		h.logger.Warn("Invalid post ID", zap.String("id", vars["id"]))
		h.writeErrorResponse(w, r.URL.Path, models.ErrInvalidRequest)
		return
	}

	err = h.postService.DeletePost(r.Context(), id)
	if err != nil {
		if apiErr, ok := err.(*models.APIError); ok {
			h.writeErrorResponse(w, r.URL.Path, apiErr)
		} else {
			h.logger.Error("Unexpected error deleting post", zap.Error(err))
			h.writeErrorResponse(w, r.URL.Path, models.ErrInternalServer)
		}
		return
	}

	// 204 No Content 응답
	w.WriteHeader(http.StatusNoContent)
}

// SearchPosts는 게시글을 검색
// Spring Boot: @GetMapping("/posts/search") + @RequestParam과 유사
func (h *PostHandler) SearchPosts(w http.ResponseWriter, r *http.Request) {
	query := r.URL.Query().Get("q")
	if query == "" {
		h.writeErrorResponse(w, r.URL.Path, models.NewValidationError("q", "검색어가 필요합니다"))
		return
	}

	page, _ := strconv.Atoi(r.URL.Query().Get("page"))
	size, _ := strconv.Atoi(r.URL.Query().Get("size"))

	posts, err := h.postService.SearchPosts(r.Context(), query, page, size)
	if err != nil {
		if apiErr, ok := err.(*models.APIError); ok {
			h.writeErrorResponse(w, r.URL.Path, apiErr)
		} else {
			h.logger.Error("Unexpected error searching posts", zap.Error(err))
			h.writeErrorResponse(w, r.URL.Path, models.ErrInternalServer)
		}
		return
	}

	h.writeSuccessResponse(w, posts)
}

// writeSuccessResponse는 성공 응답을 작성
// Spring Boot의 ResponseEntity.ok()와 유사한 기능
func (h *PostHandler) writeSuccessResponse(w http.ResponseWriter, data interface{}) {
	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusOK)

	if err := json.NewEncoder(w).Encode(data); err != nil {
		h.logger.Error("Failed to encode JSON response", zap.Error(err))
		http.Error(w, "Internal Server Error", http.StatusInternalServerError)
	}
}

// writeErrorResponse는 에러 응답을 작성
// Spring Boot의 @ExceptionHandler와 유사한 기능을 수동 구현
func (h *PostHandler) writeErrorResponse(w http.ResponseWriter, path string, apiErr *models.APIError) {
	errorResponse := models.ErrorResponse{
		Error:     apiErr,
		Timestamp: time.Now().Format(time.RFC3339),
		Path:      path,
	}

	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(apiErr.StatusCode())

	if err := json.NewEncoder(w).Encode(errorResponse); err != nil {
		h.logger.Error("Failed to encode error response", zap.Error(err))
		http.Error(w, "Internal Server Error", http.StatusInternalServerError)
	}
}

// HealthCheckHandler는 서버 상태를 확인하는 헬스체크 엔드포인트
// Spring Boot Actuator의 /actuator/health와 유사
type HealthCheckHandler struct {
	logger *zap.Logger
}

// NewHealthCheckHandler는 HealthCheckHandler 인스턴스를 생성
func NewHealthCheckHandler(logger *zap.Logger) *HealthCheckHandler {
	return &HealthCheckHandler{logger: logger}
}

// Health는 서버 상태를 반환
func (h *HealthCheckHandler) Health(w http.ResponseWriter, r *http.Request) {
	healthStatus := map[string]interface{}{
		"status":    "UP",
		"timestamp": time.Now().Format(time.RFC3339),
		"service":   "board-api",
		"version":   "1.0.0",
	}

	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusOK)
	json.NewEncoder(w).Encode(healthStatus)
}
