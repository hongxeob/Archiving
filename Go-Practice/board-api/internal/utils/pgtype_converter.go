package utils

import (
	"time"

	"github.com/jackc/pgx/v5/pgtype"
)

// PostgreSQL pgtype을 Go 기본 타입으로 안전하게 변환하는 유틸리티
// Spring Boot JPA의 자동 매핑을 Go에서 명시적으로 구현

// PgTextToString은 pgtype.Text를 string으로 변환
// Spring Boot: @Column(nullable = true) String content와 동일한 처리
func PgTextToString(pgText pgtype.Text) string {
	if !pgText.Valid {
		return "" // NULL인 경우 빈 문자열 반환
	}
	return pgText.String
}

// StringToPgText는 string을 pgtype.Text로 변환
func StringToPgText(s string) pgtype.Text {
	if s == "" {
		return pgtype.Text{Valid: false} // 빈 문자열은 NULL로 처리
	}
	return pgtype.Text{String: s, Valid: true}
}

// PgInt4ToInt32는 pgtype.Int4를 int32로 변환
// Spring Boot: @Column(nullable = true) Integer viewCount와 동일한 처리
func PgInt4ToInt32(pgInt pgtype.Int4) int32 {
	if !pgInt.Valid {
		return 0 // NULL인 경우 0 반환
	}
	return pgInt.Int32
}

// Int32ToPgInt4는 int32를 pgtype.Int4로 변환
func Int32ToPgInt4(i int32) pgtype.Int4 {
	return pgtype.Int4{Int32: i, Valid: true}
}

// PgInt8ToInt64는 pgtype.Int8을 int64로 변환
func PgInt8ToInt64(pgInt pgtype.Int8) int64 {
	if !pgInt.Valid {
		return 0
	}
	return pgInt.Int64
}

// Int64ToPgInt8은 int64를 pgtype.Int8로 변환
func Int64ToPgInt8(i int64) pgtype.Int8 {
	return pgtype.Int8{Int64: i, Valid: true}
}

// PgTimestamptzToTime은 pgtype.Timestamptz를 time.Time으로 변환
// Spring Boot: @Column(nullable = true) LocalDateTime createdAt와 동일한 처리
func PgTimestamptzToTime(pgTime pgtype.Timestamptz) time.Time {
	if !pgTime.Valid {
		return time.Time{} // NULL인 경우 제로값 반환
	}
	return pgTime.Time
}

// TimeToPgTimestamptz는 time.Time을 pgtype.Timestamptz로 변환
func TimeToPgTimestamptz(t time.Time) pgtype.Timestamptz {
	if t.IsZero() {
		return pgtype.Timestamptz{Valid: false}
	}
	return pgtype.Timestamptz{Time: t, Valid: true}
}

// PgBoolToBool은 pgtype.Bool을 bool로 변환
func PgBoolToBool(pgBool pgtype.Bool) bool {
	if !pgBool.Valid {
		return false
	}
	return pgBool.Bool
}

// BoolToPgBool은 bool을 pgtype.Bool로 변환
func BoolToPgBool(b bool) pgtype.Bool {
	return pgtype.Bool{Bool: b, Valid: true}
}
