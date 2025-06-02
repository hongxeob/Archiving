package main

import "fmt"

func main() {
	slice := []int{1, 2, 3, 4, 5}
	slice2 := append(slice, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

	fmt.Println("Original Slice:", slice)
	fmt.Println("Slice2:", slice2)
}
