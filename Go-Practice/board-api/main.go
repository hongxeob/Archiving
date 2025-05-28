package main

import (
	"context"
	"github.com/redis/go-redis/v9"
	"go.uber.org/zap/zapcore"
	"net/http"
	"time"

	"board-api/internal/config"
	"board-api/internal/handler"
	"board-api/internal/repository"
	"board-api/internal/service"

	"github.com/gorilla/mux"
	"github.com/jackc/pgx/v5/pgxpool"
	"go.uber.org/fx"
	"go.uber.org/zap"
)

// main 함수는 애플리케이션의 진입점
// Spring Boot의 @SpringBootApplication과 유사한 역할을 fx를 통해 구현
func main() {
	fx.New(
		// 의존성 공급자들 등록 - Spring Boot의 @Bean과 유사
		fx.Provide(
			// 설정 관련
			config.NewConfig,

			// 로거 관련
			newLogger,

			// 데이터베이스 관련
			newDatabase,
			newQueries,

			// Redis 관련
			newRedis,

			// 서비스 계층
			service.NewPostService,

			// 컨트롤러 계층
			handler.NewPostHandler,
			handler.NewHealthCheckHandler,

			// HTTP 서버
			newRouter,
			newServer,
		),

		// 애플리케이션 훅 등록 - Spring Boot의 lifecycle callback과 유사
		fx.Invoke(registerHooks),
	).Run()
}

// newLogger는 구조화된 로거를 생성
// Spring Boot의 logback-spring.xml 설정을 코드로 대체
func newLogger(cfg *config.Config) (*zap.Logger, error) {
	var logger *zap.Logger
	var err error

	if cfg.Logger.Mode == "production" {
		// 프로덕션 모드: JSON 형태의 구조화된 로그
		config := zap.NewProductionConfig()
		config.Level = zap.NewAtomicLevelAt(getLogLevel(cfg.Logger.Level))
		logger, err = config.Build()
	} else {
		// 개발 모드: 사람이 읽기 쉬운 형태의 로그
		config := zap.NewDevelopmentConfig()
		config.Level = zap.NewAtomicLevelAt(getLogLevel(cfg.Logger.Level))
		logger, err = config.Build()
	}

	if err != nil {
		return nil, err
	}

	// 전역 로거 설정 (선택사항)
	zap.ReplaceGlobals(logger)

	return logger, nil
}

// getLogLevel은 문자열 레벨을 zap.Level로 변환
func getLogLevel(level string) zapcore.Level {
	switch level {
	case "debug":
		return zap.DebugLevel
	case "info":
		return zap.InfoLevel
	case "warn":
		return zap.WarnLevel
	case "error":
		return zap.ErrorLevel
	default:
		return zap.InfoLevel
	}
}

// newDatabase는 PostgreSQL 연결 풀을 생성
// Spring Boot의 DataSource 설정과 유사하지만 더 직접적
func newDatabase(cfg *config.Config, logger *zap.Logger) (*pgxpool.Pool, error) {
	// 연결 풀 설정
	poolConfig, err := pgxpool.ParseConfig(cfg.GetDatabaseURL())
	if err != nil {
		logger.Error("Failed to parse database URL", zap.Error(err))
		return nil, err
	}

	// 연결 풀 파라미터 튜닝
	poolConfig.MaxConns = 25                      // 최대 연결 수
	poolConfig.MinConns = 5                       // 최소 연결 수
	poolConfig.MaxConnLifetime = time.Hour        // 연결 최대 수명
	poolConfig.MaxConnIdleTime = time.Minute * 30 // 유휴 연결 최대 시간

	// 연결 풀 생성
	pool, err := pgxpool.NewWithConfig(context.Background(), poolConfig)
	if err != nil {
		logger.Error("Failed to create database pool", zap.Error(err))
		return nil, err
	}

	// 연결 테스트
	if err := pool.Ping(context.Background()); err != nil {
		logger.Error("Failed to ping database", zap.Error(err))
		return nil, err
	}

	logger.Info("Successfully connected to database",
		zap.String("host", cfg.Database.Host),
		zap.Int("port", cfg.Database.Port),
		zap.String("database", cfg.Database.DBName))

	return pool, nil
}

// newQueries는 sqlc가 생성한 쿼리 인스턴스를 생성
// Spring Data JPA Repository와 유사한 역할
func newQueries(pool *pgxpool.Pool) *repository.Queries {
	return repository.New(pool)
}

// newRedis는 Redis 클라이언트를 생성
// Spring Boot의 RedisTemplate과 유사한 역할
func newRedis(cfg *config.Config, logger *zap.Logger) (*redis.Client, error) {
	rdb := redis.NewClient(&redis.Options{
		Addr:     cfg.GetRedisAddr(),
		Password: cfg.Redis.Password,
		DB:       cfg.Redis.DB,

		// 연결 풀 설정
		PoolSize:     10,
		MinIdleConns: 2,
		PoolTimeout:  time.Second * 5,

		// 재시도 설정
		MaxRetries:      3,
		MinRetryBackoff: time.Millisecond * 100,
		MaxRetryBackoff: time.Second * 3,
	})

	// 연결 테스트
	ctx, cancel := context.WithTimeout(context.Background(), time.Second*5)
	defer cancel()

	if err := rdb.Ping(ctx).Err(); err != nil {
		logger.Error("Failed to connect to Redis", zap.Error(err))
		return nil, err
	}

	logger.Info("Successfully connected to Redis",
		zap.String("addr", cfg.GetRedisAddr()),
		zap.Int("db", cfg.Redis.DB))

	return rdb, nil
}

// newRouter는 HTTP 라우터를 설정
// Spring Boot의 @RequestMapping 자동 스캔 대신 명시적 등록
func newRouter(
	postHandler *handler.PostHandler,
	healthHandler *handler.HealthCheckHandler,
	logger *zap.Logger,
) *mux.Router {
	router := mux.NewRouter()

	// 미들웨어 등록
	//router.Use(loggingMiddleware(logger))
	router.Use(corsMiddleware)
	router.Use(recoveryMiddleware(logger))

	// 라우트 등록
	postHandler.RegisterRoutes(router)

	// 헬스체크 엔드포인트
	router.HandleFunc("/health", healthHandler.Health).Methods("GET")

	// 정적 파일 서빙 (선택사항)
	router.PathPrefix("/static/").Handler(
		http.StripPrefix("/static/", http.FileServer(http.Dir("./static/"))),
	)

	return router
}

// newServer는 HTTP 서버를 생성
// Spring Boot의 내장 톰캣과 유사한 역할
func newServer(cfg *config.Config, router *mux.Router, logger *zap.Logger) *http.Server {
	server := &http.Server{
		Addr:    cfg.GetServerAddr(),
		Handler: router,

		// 타임아웃 설정 - 보안과 성능을 위해 중요
		ReadTimeout:       time.Second * 15,
		WriteTimeout:      time.Second * 15,
		IdleTimeout:       time.Second * 60,
		ReadHeaderTimeout: time.Second * 5,
	}

	logger.Info("HTTP server configured",
		zap.String("addr", server.Addr))

	return server
}

// registerHooks는 애플리케이션 생명주기 훅을 등록
// Spring Boot의 @PreDestroy, ApplicationListener와 유사
func registerHooks(
	lc fx.Lifecycle,
	server *http.Server,
	pool *pgxpool.Pool,
	rdb *redis.Client,
	logger *zap.Logger,
) {
	lc.Append(fx.Hook{
		OnStart: func(ctx context.Context) error {
			// 서버 시작
			go func() {
				logger.Info("Starting HTTP server", zap.String("addr", server.Addr))
				if err := server.ListenAndServe(); err != nil && err != http.ErrServerClosed {
					logger.Fatal("Failed to start server", zap.Error(err))
				}
			}()
			return nil
		},
		OnStop: func(ctx context.Context) error {
			logger.Info("Shutting down HTTP server")

			// Graceful shutdown
			if err := server.Shutdown(ctx); err != nil {
				logger.Error("Failed to shutdown server gracefully", zap.Error(err))
			}

			// 데이터베이스 연결 정리
			pool.Close()
			logger.Info("Database connections closed")

			// Redis 연결 정리
			if err := rdb.Close(); err != nil {
				logger.Error("Failed to close Redis connection", zap.Error(err))
			} else {
				logger.Info("Redis connection closed")
			}

			return nil
		},
	})
}

// 미들웨어 함수들

// loggingMiddleware는 HTTP 요청을 로깅
// Spring Boot의 logging.level.web과 유사한 기능
//func loggingMiddleware(logger *zap.Logger) mux.MiddlewareFunc {
//	return func(next http.Handler) http.Handler {
//		return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
//			start := time.Now()
//
//			// 응답 래퍼로 상태 코드 캡처
//			wrapped := &http.ResponseWriter{ResponseWriter: w, statusCode: http.StatusOK}
//
//			next.ServeHTTP(wrapped, r)
//
//			duration := time.Since(start)
//
//			logger.Info("HTTP request",
//				zap.String("method", r.Method),
//				zap.String("path", r.URL.Path),
//				zap.String("query", r.URL.RawQuery),
//				zap.Int("status", wrapped.statusCode),
//				zap.Duration("duration", duration),
//				zap.String("user_agent", r.UserAgent()),
//				zap.String("remote_addr", r.RemoteAddr),
//			)
//		})
//	}
//}

// corsMiddleware는 CORS 헤더를 설정
// Spring Boot의 @CrossOrigin과 유사한 기능
func corsMiddleware(next http.Handler) http.Handler {
	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		w.Header().Set("Access-Control-Allow-Origin", "*")
		w.Header().Set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
		w.Header().Set("Access-Control-Allow-Headers", "Content-Type, Authorization")

		// Preflight 요청 처리
		if r.Method == "OPTIONS" {
			w.WriteHeader(http.StatusOK)
			return
		}

		next.ServeHTTP(w, r)
	})
}

// recoveryMiddleware는 패닉을 복구하고 500 에러를 반환
// Spring Boot의 기본 exception handling과 유사
func recoveryMiddleware(logger *zap.Logger) mux.MiddlewareFunc {
	return func(next http.Handler) http.Handler {
		return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {})
	}
}
