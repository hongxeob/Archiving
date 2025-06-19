package main

import (
	"context"
	"fmt"
	"go.uber.org/fx"
	"net"
	"net/http"
)

func main() {
	fx.New(
		// fx.Provide()는 fx.New()에 전달된 함수가 fx 애플리케이션의 의존성 주입 컨테이너에 제공될 수 있도록 한다.
		fx.Provide(NewHTTPServer),
		// fx.Invoke()는 fx.New()에 전달된 함수를 실행하여 애플리케이션의 생명 주기 동안 특정 시점에 실행될 콜백 함수를 등록한다.
		fx.Invoke(func(s *http.Server) {}),
	).Run()
}

// NewHTTPServer Lifecycle interface: 앱의 생명 주기 동안 특정 시점에 실행될 콜백 함수를 등록하도록 하는 인터페이스 (서버 시작과 종료, 리소스 초기화, 해제)
func NewHTTPServer(lc fx.Lifecycle) *http.Server {
	srv := &http.Server{Addr: ":8888"}
	// Append() Hook 타입 객체를 받아 여러 개의 Hook을 등록한다.
	lc.Append(fx.Hook{
		OnStart: func(ctc context.Context) error {
			ln, err := net.Listen("tcp", srv.Addr)
			if err != nil {
				return err
			}
			fmt.Println("http server started on ", srv.Addr)
			go srv.Serve(ln)
			return nil
		},
		OnStop: func(ctx context.Context) error {
			return srv.Shutdown(ctx)
		},
	})
	return srv
}
