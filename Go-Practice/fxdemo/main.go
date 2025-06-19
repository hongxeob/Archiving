package main

import (
	"context"
	"fmt"
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
			fx.Annotate(
				NewServeMux,
				// fx.ParamTags()는 fx.Provide()에 전달된 함수의 매개변수에 주석을 추가하여 의존성 주입 컨테이너에서 해당 매개변수를 찾을 때 사용할 수 있는 메타데이터를 제공한다.
				fx.ParamTags(`group:"routes"`),
			),
			AsRoute(NewEchoHandler),
			AsRoute(NewHelloHandler),
			zap.NewExample,
		),
		// fx.Invoke()는 fx.New()에 전달된 함수를 실행하여 애플리케이션의 생명 주기 동안 특정 시점에 실행될 콜백 함수를 등록한다.
		fx.Invoke(func(s *http.Server) {}),
	).Run()
}

func AsRoute(f any) any {
	return fx.Annotate(
		f,
		fx.As(new(Route)),
		fx.ResultTags(`group:"routes"`),
	)

}

// Route 인터페이스는 http.Handler를 구현하는 구조체가 HTTP 요청을 처리할 수 있도록 하는 인터페이스이다.
type Route interface {
	http.Handler
	Pattern() string
}

func (*EchoHandler) Pattern() string {
	return "/echo"
}

type HelloHandler struct {
	log *zap.Logger
}

func NewHelloHandler(log *zap.Logger) *HelloHandler {
	return &HelloHandler{log: log}
}

func (*HelloHandler) Pattern() string {
	return "/hello"
}

func (h *HelloHandler) ServeHTTP(w http.ResponseWriter, r *http.Request) {
	body, err := io.ReadAll(r.Body)
	if err != nil {
		h.log.Error("failed to read request body", zap.Error(err))
		http.Error(w, "failed to read request body", http.StatusInternalServerError)
		return
	}

	if _, err := fmt.Fprintf(w, "Hello, %s!", body); err != nil {
		h.log.Error("failed to write response", zap.Error(err))
		http.Error(w, "failed to write response", http.StatusInternalServerError)
		return
	}
}

// NewServeMux 는 EchoHandler를 사용하여 HTTP 요청을 처리하는 ServeMux를 생성한다.
// ServeMux는 HTTP 요청을 여러 핸들러로 라우팅하는 데 사용되는 멀티플렉서이다.
func NewServeMux(routes []Route) *http.ServeMux {
	mux := http.NewServeMux()
	// HandleFunc()는 HTTP 요청을 처리할 핸들러를 등록한다.
	for _, route := range routes {
		mux.Handle(route.Pattern(), route)
	}
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
