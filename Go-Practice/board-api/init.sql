-- 데이터베이스 초기화 스크립트
-- Spring Boot의 schema.sql과 유사한 역할

-- 게시글 테이블
-- Go의 타입 시스템 고려: BIGSERIAL은 int64, SERIAL은 int32로 매핑됨
CREATE TABLE IF NOT EXISTS posts
(
    id         BIGSERIAL PRIMARY KEY,
    title      VARCHAR(255) NOT NULL,
    content    TEXT,
    author     VARCHAR(100) NOT NULL,
    view_count INTEGER                  DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 댓글 테이블 (추후 확장용)
CREATE TABLE IF NOT EXISTS comments
(
    id         SERIAL PRIMARY KEY,
    post_id    INTEGER      NOT NULL REFERENCES posts (id) ON DELETE CASCADE,
    content    TEXT         NOT NULL,
    author     VARCHAR(100) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 인덱스 생성 (성능 최적화)
CREATE INDEX IF NOT EXISTS idx_posts_created_at ON posts (created_at DESC);
CREATE INDEX IF NOT EXISTS idx_comments_post_id ON comments (post_id);

-- 트리거 함수: updated_at 자동 업데이트
CREATE
OR
REPLACE
FUNCTION update_updated_at_column()
    RETURNS TRIGGER AS
$$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- 트리거 생성
CREATE TRIGGER update_posts_updated_at
    BEFORE UPDATE
    ON posts
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- 샘플 데이터 삽입
INSERT INTO posts (title, content, author)
VALUES ('Go 언어 학습하기', 'Go 언어의 기본 개념과 특징을 알아봅시다.', 'gopher'),
       ('gRPC vs REST API', 'gRPC와 REST API의 차이점과 장단점을 비교해보겠습니다.', 'developer'),
       ('Docker로 개발환경 구성', 'Docker Compose를 활용한 로컬 개발환경 셋업 가이드입니다.', 'devops');

INSERT INTO comments (post_id, content, author)
VALUES (1, '좋은 정보 감사합니다!', 'reader1'),
       (1, 'Go 언어 정말 흥미롭네요.', 'reader2'),
       (2, 'gRPC 성능이 궁금해요.', 'curious');
