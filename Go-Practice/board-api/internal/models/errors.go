package models

import (
	"encoding/json"
	"fmt"
	"net/http"
)

// APIError는 API 응답에서 사용할 공통 에러 구조체
// Spring Boot의 @ControllerAdvice + @ExceptionHandler와 유사한 역할
type APIError struct {
	Code    int    `json:"code"`
	Message string `json:"message"`
	Field   string `json:"field,omitempty"`
}

// Error는 error 인터페이스를 구현
func (e *APIError) Error() string {
	return e.Message
}

// StatusCode는 HTTP 상태 코드를 반환
func (e *APIError) StatusCode() int {
	return e.Code
}

// ToJSON은 에러를 JSON으로 변환
func (e *APIError) ToJSON() []byte {
	bytes, _ := json.Marshal(e)
	return bytes
}

// 사전 정의된 에러들 - Spring Boot의 표준 예외들과 유사
var (
	ErrPostNotFound = &APIError{
		Code:    http.StatusNotFound,
		Message: "게시글을 찾을 수 없습니다",
	}

	ErrInvalidRequest = &APIError{
		Code:    http.StatusBadRequest,
		Message: "잘못된 요청입니다",
	}

	ErrInternalServer = &APIError{
		Code:    http.StatusInternalServerError,
		Message: "서버 내부 오류가 발생했습니다",
	}

	ErrDatabase = &APIError{
		Code:    http.StatusInternalServerError,
		Message: "데이터베이스 오류가 발생했습니다",
	}
)

// NewValidationError는 유효성 검사 에러를 생성
// Spring Boot Validation의 FieldError와 유사
func NewValidationError(field, message string) *APIError {
	return &APIError{
		Code:    http.StatusBadRequest,
		Message: message,
		Field:   field,
	}
}

// NewNotFoundError는 리소스를 찾을 수 없는 에러를 생성
func NewNotFoundError(resource string) *APIError {
	return &APIError{
		Code:    http.StatusNotFound,
		Message: fmt.Sprintf("%s을(를) 찾을 수 없습니다", resource),
	}
}

// NewInternalError는 내부 서버 에러를 생성
func NewInternalError(message string) *APIError {
	return &APIError{
		Code:    http.StatusInternalServerError,
		Message: message,
	}
}

// ErrorResponse는 에러 응답 구조체
// Spring Boot의 ResponseEntity<ErrorResponse>와 유사
type ErrorResponse struct {
	Error     *APIError `json:"error"`
	Timestamp string    `json:"timestamp"`
	Path      string    `json:"path"`
}

// ValidationErrors는 다중 필드 유효성 검사 에러
type ValidationErrors struct {
	Errors []APIError `json:"errors"`
}

// Error는 error 인터페이스를 구현
func (ve *ValidationErrors) Error() string {
	return "유효성 검사 실패"
}

// AddError는 유효성 검사 에러를 추가
func (ve *ValidationErrors) AddError(field, message string) {
	ve.Errors = append(ve.Errors, APIError{
		Code:    http.StatusBadRequest,
		Message: message,
		Field:   field,
	})
}

// HasErrors는 에러가 있는지 확인
func (ve *ValidationErrors) HasErrors() bool {
	return len(ve.Errors) > 0
}
