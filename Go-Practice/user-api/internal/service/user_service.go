package service

import (
	"context"
	"errors"
	"fmt"
	"go.uber.org/zap"
	"user-api/internal/domain"
	"user-api/internal/repository"
)

type userService struct {
	userRepo repository.UserRepository
	logger   *zap.Logger
}

func NewUserService(userRepo repository.UserRepository, logger *zap.Logger) *userService {
	return &userService{
		userRepo: userRepo,
		logger:   logger,
	}
}

func (s *userService) GetAllUsers(ctx context.Context) ([]domain.User, error) {
	s.logger.Info("GetAllUsers")
	return s.userRepo.GetAll(ctx)
}

func (s *userService) GetUserById(ctx context.Context, id int) (*domain.User, error) {
	s.logger.Info("GetUserById", zap.Int("id", id))
	return s.userRepo.GetByID(ctx, id)

}
func (s *userService) CreateUser(ctx context.Context, req domain.CreateUserRequest) (*domain.User, error) {
	s.logger.Info("service: creating user", zap.String("email", req.Email))

	// 비즈니스 로직: 이메일 중복 확인
	existingUser, err := s.userRepo.GetByEmail(ctx, req.Email)
	if err != nil && !errors.Is(err, domain.ErrUserNotFound) {
		return nil, fmt.Errorf("failed to check existing user: %w", err)
	}
	if existingUser != nil {
		return nil, domain.ErrDuplicateEmail
	}

	// 도메인 객체 생성
	user := domain.User{
		Name:  req.Name,
		Email: req.Email,
	}

	// 도메인 유효성 검증
	if err := user.IsValid(); err != nil {
		return nil, err
	}

	return s.userRepo.Create(ctx, user)
}

func (s *userService) UpdateUser(ctx context.Context, id int, req domain.UpdateUserRequest) (*domain.User, error) {
	s.logger.Info("service: updating user", zap.Int("id", id))

	// 기존 사용자 조회
	user, err := s.userRepo.GetByID(ctx, id)
	if err != nil {
		return nil, err
	}

	// 이메일 변경 시 중복 확인
	if s.validateEmail(req.Email, user.Email) {
		existingUser, err := s.userRepo.GetByEmail(ctx, *req.Email)
		if err != nil && !errors.Is(err, domain.ErrUserNotFound) {
			return nil, fmt.Errorf("failed to check existing email: %w", err)
		}
		if existingUser != nil {
			return nil, domain.ErrDuplicateEmail
		}
	}

	// 도메인 객체 업데이트
	user.UpdateFrom(req)

	// 도메인 유효성 검증
	if err := user.IsValid(); err != nil {
		return nil, err
	}

	return s.userRepo.Update(ctx, *user)
}

func (s *userService) DeleteUser(ctx context.Context, id int) error {
	s.logger.Info("service: deleting user", zap.Int("id", id))
	return s.userRepo.Delete(ctx, id)
}

func (s *userService) validateEmail(reqEmail *string, userEmail string) bool {
	return reqEmail != nil && *reqEmail != userEmail
}
