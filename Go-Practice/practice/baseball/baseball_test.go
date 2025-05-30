package baseball

import "testing"

type Game interface {
}

type gameNumbs struct {
	nums string
}

func CreateGame(nums string) Game {
	return &gameNumbs{nums: nums}
}

func TestCreateGame(t *testing.T) {
	var game Game = CreateGame("123")

	if game == nil {
		t.Fatalf("게임 실행 불가")
	}
}
