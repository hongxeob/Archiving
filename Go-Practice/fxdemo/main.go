package main

import (
	"context"
	"go.uber.org/fx"
	"go.uber.org/fx/fxevent"
	"go.uber.org/zap"
	"io"
	"net"
	"net/http"
)

func main() {
	fx.New(
		fx.WithLogger(func(log *zap.Logger) fxevent.Logger {
			return &fxevent.ZapLogger{Logger: log}
		}),
		// fx.Provide()는 fx.New()에 전달된 함수가 fx 애플리케이션의 의존성 주입 컨테이너에 제공될 수 있도록 한다.
		fx.Provide(
			NewHTTPServer,
			NewEchoHandler,
			NewServeMux,
			zap.NewExample,
		),
		// fx.Invoke()는 fx.New()에 전달된 함수를 실행하여 애플리케이션의 생명 주기 동안 특정 시점에 실행될 콜백 함수를 등록한다.
		fx.Invoke(func(s *http.Server) {}),
	).Run()
}

// NewServeMux 는 EchoHandler를 사용하여 HTTP 요청을 처리하는 ServeMux를 생성한다.
// ServeMux는 HTTP 요청을 여러 핸들러로 라우팅하는 데 사용되는 멀티플렉서이다.
func NewServeMux(echo *EchoHandler) *http.ServeMux {
	mux := http.NewServeMux()
	// HandleFunc()는 HTTP 요청을 처리할 핸들러를 등록한다.
	mux.HandleFunc("/echo", echo.ServeHTTP)
	return mux
}

// EchoHandler EchoHandler는 HTTP 요청을 처리하는 핸들러 구조체
type EchoHandler struct {
	log *zap.Logger
}

func NewEchoHandler(log *zap.Logger) *EchoHandler {
	return &EchoHandler{log: log}
}

func (e *EchoHandler) ServeHTTP(w http.ResponseWriter, r *http.Request) {
	if _, err := io.Copy(w, r.Body); err != nil {
		e.log.Warn("failed to handle request", zap.Error(err))
	}
}

// NewHTTPServer Lifecycle interface: 앱의 생명 주기 동안 특정 시점에 실행될 콜백 함수를 등록하도록 하는 인터페이스 (서버 시작과 종료, 리소스 초기화, 해제)
func NewHTTPServer(
	lc fx.Lifecycle,
	mux *http.ServeMux,
	log *zap.Logger,
) *http.Server {
	srv := &http.Server{Addr: ":8888", Handler: mux}
	// Append() Hook 타입 객체를 받아 여러 개의 Hook을 등록한다.
	lc.Append(fx.Hook{
		OnStart: func(ctc context.Context) error {
			ln, err := net.Listen("tcp", srv.Addr)
			if err != nil {
				return err
			}
			log.Info("http server started", zap.String("addr", srv.Addr))
			go srv.Serve(ln)
			return nil
		},
		OnStop: func(ctx context.Context) error {
			return srv.Shutdown(ctx)
		},
	})
	return srv
}
