package main

import (
	"container/list"
	"fmt"
)

func main() {
	list := list.New()
	e4 := list.PushBack(4)
	e1 := list.PushFront(5)
	fmt.Println(e1, e4)

}
