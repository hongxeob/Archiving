package main

import "fmt"

func main() {
	slice := []int{1, 2, 3, 4, 5}
	slice2 := append([]int{}, slice...)

	fmt.Println("Original Slice:", slice)
	fmt.Println("Slice2:", slice2)
	fmt.Println("slice[2:5]:", slice[2:5])
}
