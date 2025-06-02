-- SQLC가 자동으로 Go 코드를 생성할 SQL 쿼리들

-- name: GetAllUsers :many
-- 모든 사용자 조회 (Java의 @Select와 유사하지만 타입 안전성 보장)
SELECT id, name, email, created_at, updated_at
FROM users
ORDER BY created_at DESC;

-- name: GetUserByID :one
-- ID로 사용자 조회 (존재하지 않으면 sql.ErrNoRows 반환)
SELECT id, name, email, created_at, updated_at
FROM users
WHERE id = ?;

-- name: GetUserByEmail :one
-- 이메일로 사용자 조회
SELECT id, name, email, created_at, updated_at
FROM users
WHERE email = ?;

-- name: CreateUser :execresult
-- 사용자 생성 (MySQL의 LAST_INSERT_ID() 활용)
INSERT INTO users (name, email)
VALUES (?, ?);

-- name: UpdateUser :exec
-- 사용자 정보 업데이트
UPDATE users
SET name       = ?,
    email      = ?,
    updated_at = CURRENT_TIMESTAMP
WHERE id = ?;

-- name: UpdateUserPartial :exec
-- 부분 업데이트 (NULL 체크 포함)
UPDATE users
SET name       = CASE WHEN ? != '' THEN ? ELSE name END,
    email      = CASE WHEN ? != '' THEN ? ELSE email END,
    updated_at = CURRENT_TIMESTAMP
WHERE id = ?;

-- name: DeleteUser :exec
-- 사용자 삭제
DELETE
FROM users
WHERE id = ?;

-- name: CountUsers :one
-- 사용자 수 조회
SELECT COUNT(*)
FROM users;

-- name: GetUsersPaginated :many
-- 페이지네이션 지원 사용자 목록
SELECT id, name, email, created_at, updated_at
FROM users
ORDER BY created_at DESC
LIMIT ? OFFSET ?;
