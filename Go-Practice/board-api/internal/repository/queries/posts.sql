-- 게시글 관련 쿼리 정의
-- Spring Data JPA의 @Query와 유사하지만 더 명시적

-- name: GetPost :one
SELECT id, title, content, author, view_count, created_at, updated_at
FROM posts
WHERE id = $1;

-- name: GetPosts :many
SELECT id, title, content, author, view_count, created_at, updated_at
FROM posts
ORDER BY created_at DESC
LIMIT $1 OFFSET $2;

-- name: GetPostsCount :one
SELECT COUNT(*)
FROM posts;

-- name: CreatePost :one
INSERT INTO posts (title, content, author)
VALUES ($1, $2, $3)
RETURNING id, title, content, author, view_count, created_at, updated_at;

-- name: UpdatePost :one
UPDATE posts
SET title = $2, content = $3, updated_at = CURRENT_TIMESTAMP
WHERE id = $1
RETURNING id, title, content, author, view_count, created_at, updated_at;

-- name: DeletePost :exec
DELETE FROM posts
WHERE id = $1;

-- name: IncrementViewCount :one
UPDATE posts
SET view_count = view_count + 1
WHERE id = $1
RETURNING view_count;

-- name: SearchPosts :many
SELECT id, title, content, author, view_count, created_at, updated_at
FROM posts
WHERE title ILIKE '%' || $1 || '%' OR content ILIKE '%' || $1 || '%'
ORDER BY created_at DESC
LIMIT $2 OFFSET $3;
