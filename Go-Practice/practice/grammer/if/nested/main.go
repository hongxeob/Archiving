package main

import "fmt"

func HasRichFriend() bool {
	return true
}

func GetFriendCount() int {
	return 3
}

func main() {
	price := 600

	if price > 700 {
		if HasRichFriend() {
			fmt.Println("신발끈")
		} else {
			fmt.Println("나눠내자")
		}
	} else if price >= 300 && price < 700 {
		if GetFriendCount() > 3 {
			fmt.Println("신발끈")
		} else {
			fmt.Println("나눠내자")
		}
	} else {
		fmt.Println("쏜다")
	}
}
