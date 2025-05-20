package main

import (
	"fmt"
	"net/http"
)

type fooHandler struct{}

func (h *fooHandler) ServeHTTP(w http.ResponseWriter, r *http.Request) {
	fmt.Fprintf(w, "Hello, Foo!")
}

func main() {
	http.HandleFunc("/", func(w http.ResponseWriter, r *http.Request) {
		fmt.Fprintf(w, "Hello, World!")
	})
	http.HandleFunc("/bar", func(w http.ResponseWriter, r *http.Request) {
		fmt.Fprintf(w, "Hello, Bar!")
	})
	http.ListenAndServe(":3000", nil)

	http.Handle("/foo", &fooHandler{})
}
