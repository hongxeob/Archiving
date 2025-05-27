package main

import "fmt"

var cnt int = 33

func IncreaseAndReturn() int {
	fmt.Println("increaseAndReturn()", cnt)
	cnt++
	return cnt
}

func main() {
	if false && IncreaseAndReturn() < 5 {
		fmt.Println("1 increase")
	}
}
