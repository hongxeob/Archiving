package domain

import "time"

type User struct {
	ID        int       `json:"id"`
	Name      string    `json:"name"`
	Email     string    `json:"email"`
	CreatedAt time.Time `json:"created_at"`
	UpdatedAt time.Time `json:"updated_at"`
}

type CreateUserRequest struct {
	Name  string `json:"name" validate:"required,min=1,max=100"`
	Email string `json:"email" validate:"required,email,max=255"`
}
type UpdateUserRequest struct {
	Name  *string `json:"name,omitempty" validate:"omitempty,min=1,max=100"`
	Email *string `json:"email,omitempty" validate:"omitempty,email,max=255"`
}

func (u *User) IsValid() error {
	if u.Name == "" {
		return ErrInvalidUserName
	}
	if u.Email == "" {
		return ErrInvalidUserEmail
	}
	return nil
}

func (u *User) UpdateFrom(req UpdateUserRequest) {

	//왜 여기서 포인터?
	if req.Name != nil && *req.Name != "" {
		u.Name = *req.Name
	}
}
