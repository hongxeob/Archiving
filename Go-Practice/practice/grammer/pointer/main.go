package main

import "fmt"

func main() {
	v := 1024
	fmt.Println(v, &v)

	callByValue(v)
	fmt.Println(v, &v)

	callByRef(&v)
	fmt.Println(v, &v)

}
func callByValue(i int) {
	i += 100
	fmt.Println("callbyvalue", i)
	fmt.Println("callbyvalue &", &i)
}

func callByRef(i *int) {
	*i += 100
}
