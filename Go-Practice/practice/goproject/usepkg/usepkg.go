package main

import (
	"fmt"
	"github.com/guptarohit/asciigraph"
	"github.com/tuckersGo/musthaveGo2/ch14/expkg"
	"goproject/usepkg/custompkg"
)

func main() {
	custompkg.PrintCustom()
	expkg.PrintSample()

	data := []float64{1, 2, 3}
	graph := asciigraph.Plot(data)

	fmt.Println(graph)
}
