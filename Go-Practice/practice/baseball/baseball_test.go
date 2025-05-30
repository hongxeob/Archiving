package baseball

import (
	"fmt"
	"testing"
)

type Game interface {
}

type gameNumbs struct {
	nums string
}

func CreateGame(nums string) (game Game, err error) {
	if len(nums) != 3 {
		return nil, fmt.Errorf("invalid numbs length %s", nums)

	}
	return nil, fmt.Errorf("invalid nums %s", nums)
}

func TestCreateGame(t *testing.T) {
	// 중복 숫자 X
	// 숫자만
	// 자리수 3자리만
	game, _ := CreateGame("123")
	if game == nil {
		t.Fatalf("게임 실행 불가")
	}
}
func TestInvalidNums(t *testing.T) {
	// 중복 숫자 X
	// 숫자만
	// 자리수 3자리만
	_, err := CreateGame("111")
	if err == nil {
		t.Fatalf("중복 숫자 불가 %s", "111")
	}

	_, err2 := CreateGame("11a")
	if err2 == nil {
		t.Fatalf("숫자만 가능 %s", "11a")
	}
}
