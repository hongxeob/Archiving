package main

import (
	"fmt"
	"strings"
)

func lenAndUpper(name string) (length int, uppercase string) {
	defer fmt.Println("I`m done") // function이 끝나고 이 코드를 실행시킨다. (순서 바꿔도 될듯?)
	length = len(name)
	uppercase = strings.ToUpper(name)

	return
}
func main() {
	length, uppercase := lenAndUpper("hello")
	fmt.Println(length, uppercase)
}
