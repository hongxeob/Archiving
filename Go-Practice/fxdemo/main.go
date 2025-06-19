package main

import (
	"go.uber.org/fx"
	"net/http"
)

func main() {
	fx.New().Run()
}

func NewHTTPServer(lc fx.Lifecycle) *http.Server {
	return &http.Server{Addr: "8888"}
}
