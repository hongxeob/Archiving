package repository

import (
	"context"
	"user-api/internal/db"
	"user-api/internal/domain"
)

type userRepositoryImpl struct {
	q *db.Queries
}

func (r *userRepositoryImpl) GetAll(ctx context.Context) ([]domain.User, error) {
	users, err := r.q.GetAllUsers(ctx)
	if err != nil {
		return nil, err
	}

	result := make([]domain.User, 0, len(users))
	for _, u := range users {
		result = append(result, domain.User{
			ID:        int(u.ID),
			Name:      u.Name,
			Email:     u.Email,
			CreatedAt: u.CreatedAt,
			UpdatedAt: u.UpdatedAt,
		})
	}
	return result, nil
}

func (r userRepositoryImpl) GetByID(ctx context.Context, id int) (*domain.User, error) {
	//TODO implement me
	panic("implement me")
}

func (r userRepositoryImpl) GetByEmail(ctx context.Context, email string) (*domain.User, error) {
	//TODO implement me
	panic("implement me")
}

func (r userRepositoryImpl) Create(ctx context.Context, user domain.User) (*domain.User, error) {
	//TODO implement me
	panic("implement me")
}

func (r userRepositoryImpl) Update(ctx context.Context, user domain.User) (*domain.User, error) {
	//TODO implement me
	panic("implement me")
}

func (r userRepositoryImpl) Delete(ctx context.Context, id int) error {
	//TODO implement me
	panic("implement me")
}

func NewUserRepository(q *db.Queries) UserRepository {
	return &userRepositoryImpl{q: q}
}
