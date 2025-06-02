package main

import (
	"go.uber.org/fx"
	"user-api/internal/config"
	"user-api/internal/handler"
	"user-api/internal/repository"
	"user-api/internal/server"
	"user-api/internal/service"
	"user-api/pkg/logger"
)

func main() {
	app := fx.New(
		fx.Provide(config.NewConfig),

		fx.Provide(logger.New),
		//fx.Provide(db.NewPostgres()),
		// 비즈니스 로직 모듈
		fx.Provide(
			repository.NewUserRepository(),
			service.NewUserService,
			handler.NewUserHandler,
		),

		// 서버 모듈
		fx.Provide(server.Server{}),
		fx.Invoke(server.Run),
	)

	app.Run()
}
