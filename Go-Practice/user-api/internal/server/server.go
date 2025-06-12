package server

import (
	"context"
	"github.com/gorilla/mux"
	"go.uber.org/fx"
	"go.uber.org/zap"
	"net/http"
	"time"
	"user-api/internal/config"
	"user-api/internal/handler"
)

type Server struct {
	config      *config.Config
	userHandler *handler.UserHandler
	logger      *zap.Logger
}

func NewServer(config *config.Config, userHandler *handler.UserHandler, logger *zap.Logger) *Server {
	return &Server{
		config:      config,
		userHandler: userHandler,
		logger:      logger,
	}
}

func (s *Server) setupRoutes() *mux.Router {
	router := mux.NewRouter()

	// API 버전 관리
	api := router.PathPrefix("/api/v1").Subrouter()

	// 사용자 관련 라우트
	api.HandleFunc("/users", s.userHandler.GetAllUsers).Methods("GET")
	//api.HandleFunc("/users/{id:[0-9]+}", s.userHandler.GetUser).Methods("GET")
	//api.HandleFunc("/users", s.userHandler.CreateUser).Methods("POST")
	//api.HandleFunc("/users/{id:[0-9]+}", s.userHandler.UpdateUser).Methods("PUT")
	//api.HandleFunc("/users/{id:[0-9]+}", s.userHandler.DeleteUser).Methods("DELETE")

	// 헬스체크
	//api.HandleFunc("/health", s.userHandler.HealthCheck).Methods("GET")

	return router
}

func Run(lc fx.Lifecycle, server *Server) {
	srv := &http.Server{
		Addr:         ":" + server.config.Server.Port,
		Handler:      server.setupRoutes(),
		ReadTimeout:  time.Duration(server.config.Server.ReadTimeout) * time.Second,
		WriteTimeout: time.Duration(server.config.Server.WriteTimeout) * time.Second,
		IdleTimeout:  60 * time.Second,
	}

	lc.Append(fx.Hook{
		OnStart: func(ctx context.Context) error {
			server.logger.Info("Starting HTTP server",
				zap.String("addr", srv.Addr),
				zap.Int("read_timeout", server.config.Server.ReadTimeout),
				zap.Int("write_timeout", server.config.Server.WriteTimeout),
			)

			go func() {
				if err := srv.ListenAndServe(); err != nil && err != http.ErrServerClosed {
					server.logger.Fatal("Failed to start server", zap.Error(err))
				}
			}()

			return nil
		},
		OnStop: func(ctx context.Context) error {
			server.logger.Info("Shutting down HTTP server")
			return srv.Shutdown(ctx)
		},
	})
}
