package main

import (
	"context"
	"fmt"
	"go.uber.org/fx"
	"net"
	"net/http"
)

func main() {
	fx.New().Run()
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
