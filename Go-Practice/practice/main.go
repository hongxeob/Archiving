package main

import "fmt"

//TIP <p>To run your code, right-click the code and select <b>Run</b>.</p> <p>Alternatively, click
// the <icon src="AllIcons.Actions.Execute"/> icon in the gutter and select the <b>Run</b> menu item from here.</p>

func main() {
	n, err := fmt.Print("Hello, World!")
	fmt.Println(n, err)

	foo, _ := doFoo()
	fmt.Println(foo)

	for i := 0; i < 10; i++ {
		fmt.Println("dooFoo", i+1, "times")
		_, _ = doFoo()
	}
}

func doFoo() (int, error) {
	return fmt.Println("I'm in doFoo Function. Not in touFoo")
}
