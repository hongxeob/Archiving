package main

import (
	"fmt"
	"strings"
)

func lenAndUpper(name string) (length int, uppercase string) {
	length = len(name)
	upper := strings.ToUpper(name)

	return length, upper
}

func main() {
	length, uppercase := lenAndUpper("hello")
	fmt.Println(length, uppercase)
}
