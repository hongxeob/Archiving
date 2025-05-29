package baseball

import "testing"

type Game interface {
}

func TestCreateGame(t *testing.T) {
	var game Game = CreateGame("123")

	if game == nil {
		t.Fatalf("게임 실행 불가")
	}
}

func CreateGame(nums string) Game {
	return nil
}
