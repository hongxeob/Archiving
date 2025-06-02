package domain

import "errors"

var (
	ErrUserNotFound      = errors.New("user not found")
	ErrUserAlreadyExists = errors.New("user already exists")
	ErrInvalidUserName   = errors.New("invalid user name")
	ErrInvalidUserEmail  = errors.New("invalid user email")
	ErrDuplicateEmail    = errors.New("email already exists")
)
