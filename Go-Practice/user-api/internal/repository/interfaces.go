package repository

import (
	"context"
	"user-api/internal/domain"
)

// UserRepository
// Go의 인터페이스는 구현체가 아닌 사용하는 곳에서 정의하는 것이 관례
// Spring의 JpaRepository와 달리 필요한 메서드만 정의
type UserRepository interface {
	GetAll(ctx context.Context) ([]domain.User, error)
	GetByID(ctx context.Context, id int) (*domain.User, error)
	GetByEmail(ctx context.Context, email string) (*domain.User, error)
	Create(ctx context.Context, user domain.User) (*domain.User, error)
	Update(ctx context.Context, user domain.User) (*domain.User, error)
	Delete(ctx context.Context, id int) error
}
