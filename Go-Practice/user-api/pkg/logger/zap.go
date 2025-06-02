package logger

import (
	"go.uber.org/zap"
	"user-api/internal/config"
)

// 재사용 가능한 로거 팩토리
func New(cfg *config.Config) (*zap.Logger, error) {
	var logger *zap.Logger
	var err error

	if cfg.Logger.Env == "production" {
		logger, err = zap.NewProduction()
	} else {
		logger, err = zap.NewDevelopment()
	}

	if err != nil {
		return nil, err
	}

	return logger, nil
}
