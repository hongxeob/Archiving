package db

import (
	"database/sql"
	"fmt"
	"user-api/internal/config"
)

// 재사용 가능한 데이터베이스 연결 팩토리
func NewPostgres(cfg *config.Config) (*sql.DB, error) {
	dsn := fmt.Sprintf("host=%s port=%s user=%s password=%s dbname=%s sslmode=%s",
		cfg.Database.Host,
		cfg.Database.Port,
		cfg.Database.User,
		cfg.Database.Password,
		cfg.Database.Name,
		cfg.Database.SSLMode,
	)

	db, err := sql.Open("mysql", dsn)
	if err != nil {
		return nil, fmt.Errorf("failed to connect database: %w", err)
	}

	if err := db.Ping(); err != nil {
		return nil, fmt.Errorf("failed to ping database: %w", err)
	}

	// 연결 풀 설정
	db.SetMaxOpenConns(25)
	db.SetMaxIdleConns(5)

	return db, nil
}
