# Go Connect (Connect-Go)

## 🚀 Connect-Go란

Connect-Go는 Buf에서 개발한 혁신적인 RPC 프레임워크로, **Protocol Buffers와 Go 표준 라이브러리 `net/http`를 기반**으로 합니다. gRPC의 복잡성을 대폭 줄이면서도 완벽한
호환성을 유지하는 것이 핵심 철학입니다.

### 🎯 핵심 학습 포인트

- **단순성**: 표준 라이브러리 기반의 명확한 설계
- **호환성**: gRPC, gRPC-Web과 완벽 호환
- **효율성**: 최소한의 코드로 최대의 성능
- **Go 철학**: 간결하고 명확한 코드 작성 원칙 준수

## 📚 Connect-Go의 혁신적 특징

### 1. 표준 라이브러리 기반 설계

```go
// Connect-Go는 표준 http.Handler 인터페이스 활용
func (s *Server) ServeHTTP(w http.ResponseWriter, r *http.Request) {
// 표준 라이브러리와 완벽 호환
}
```

### 2. 3가지 프로토콜 지원

- **gRPC**: 기존 gRPC 클라이언트와 완벽 호환
- **gRPC-Web**: 브라우저 환경 지원
- **Connect Protocol**: 새로운 HTTP/1.1, HTTP/2 호환 프로토콜

### 3. 간단한 HTTP 호출 가능

```bash
# Connect Protocol을 사용한 간단한 curl 호출
curl --header "Content-Type: application/json" \
     --data '{"name": "World"}' \
     https://api.example.com/greet.v1.GreetService/Greet
```

## 🛠️ 실습 예제: 간단한 인사 서비스

### 1. 프로젝트 초기 설정

```bash
# 새 모듈 생성
mkdir connect-example && cd connect-example
go mod init connect-example

# 필요한 도구 설치
go install github.com/bufbuild/buf/cmd/buf@latest
go install github.com/bufbuild/buf/cmd/protoc-gen-buf-lint@latest
go install connectrpc.com/connect/cmd/protoc-gen-connect-go@latest
```

### 2. Protocol Buffer 스키마 정의

```protobuf
// greet/v1/greet.proto
syntax = "proto3";

package greet.v1;

option go_package = "connect-example/gen/greet/v1;greetv1";

message GreetRequest {
  string name = 1;
}

message GreetResponse {
  string greeting = 1;
}

service GreetService {
  rpc Greet(GreetRequest) returns (GreetResponse) {}
}
```

### 3. 서버 구현

```go
package main

import (
	"context"
	"log"
	"net/http"

	greetv1 "connect-example/gen/greet/v1"
	"connect-example/gen/greet/v1/greetv1connect"

	"connectrpc.com/connect"
)

// 서비스 구현
type GreetServer struct{}

func (s *GreetServer) Greet(
	ctx context.Context,
	req *connect.Request[greetv1.GreetRequest],
) (*connect.Response[greetv1.GreetResponse], error) {
	log.Printf("Request headers: %v", req.Header())

	res := connect.NewResponse(&greetv1.GreetResponse{
		Greeting: "Hello, " + req.Msg.Name + "!",
	})
	res.Header().Set("Greet-Version", "v1")

	return res, nil
}

func main() {
	greeter := &GreetServer{}
	mux := http.NewServeMux()

	// Connect 핸들러 등록
	path, handler := greetv1connect.NewGreetServiceHandler(greeter)
	mux.Handle(path, handler)

	log.Println("Server listening on :8080")
	http.ListenAndServe(":8080", mux)
}
```

### 4. 클라이언트 구현

```go
package main

import (
	"context"
	"log"
	"net/http"

	greetv1 "connect-example/gen/greet/v1"
	"connect-example/gen/greet/v1/greetv1connect"

	"connectrpc.com/connect"
)

func main() {
	client := greetv1connect.NewGreetServiceClient(
		http.DefaultClient,
		"http://localhost:8080",
	)

	req := connect.NewRequest(&greetv1.GreetRequest{
		Name: "Connect-Go",
	})
	req.Header().Set("User-Agent", "connect-go-client")

	res, err := client.Greet(context.Background(), req)
	if err != nil {
		log.Fatalf("request failed: %v", err)
	}

	log.Printf("Response: %s", res.Msg.Greeting)
	log.Printf("Response headers: %v", res.Header())
}
```

## 🔧 고급 기능 활용

### 1. 인터셉터 (Interceptor) 활용

```go
// 로깅 인터셉터
func loggingInterceptor() connect.UnaryInterceptorFunc {
return func (next connect.UnaryFunc) connect.UnaryFunc {
return func (ctx context.Context, req connect.AnyRequest) (connect.AnyResponse, error) {
log.Printf("Request: %s", req.Spec().Procedure)
return next(ctx, req)
}
}
}

// 서버에 인터셉터 적용
path, handler := greetv1connect.NewGreetServiceHandler(
greeter,
connect.WithInterceptors(loggingInterceptor()),
)
```

### 2. 스트리밍 지원

```go
func (s *GreetServer) StreamGreet(
ctx context.Context,
stream *connect.BidiStream[greetv1.GreetRequest, greetv1.GreetResponse],
) error {
for {
req, err := stream.Receive()
if err != nil {
return err
}

res := &greetv1.GreetResponse{
Greeting: "Hello, " + req.Name + "!",
}

if err := stream.Send(res); err != nil {
return err
}
}
}
```

### 3. 프로토콜 선택

```go
// gRPC 프로토콜 사용
client := greetv1connect.NewGreetServiceClient(
http.DefaultClient,
"http://localhost:8080",
connect.WithGRPC(), // gRPC 프로토콜 사용
)

// gRPC-Web 프로토콜 사용
client := greetv1connect.NewGreetServiceClient(
http.DefaultClient,
"http://localhost:8080",
connect.WithGRPCWeb(), // gRPC-Web 프로토콜 사용
)
```

## 🎯 실전 활용 사례

### 1. 마이크로서비스 아키텍처

- **API Gateway**: 표준 HTTP 미들웨어와 완벽 호환
- **서비스 간 통신**: gRPC 호환성으로 기존 서비스와 연동
- **브라우저 클라이언트**: gRPC-Web 지원으로 웹 앱 개발

### 2. 개발 생산성 향상

```go
// 기존 gRPC 대비 단순한 설정
func main() {
mux := http.NewServeMux()

// 여러 서비스를 쉽게 등록
registerGreetService(mux)
registerUserService(mux)
registerNotificationService(mux)

// 표준 HTTP 서버 사용
server := &http.Server{
Addr:    ":8080",
Handler: mux,
}

log.Fatal(server.ListenAndServe())
}
```

### 3. 테스트 친화적 설계

```go
func TestGreetService(t *testing.T) {
// 메모리 내 서버 생성
mux := http.NewServeMux()
path, handler := greetv1connect.NewGreetServiceHandler(&GreetServer{})
mux.Handle(path, handler)

server := httptest.NewServer(mux)
defer server.Close()

// 테스트 클라이언트 생성
client := greetv1connect.NewGreetServiceClient(
server.Client(),
server.URL,
)

// 테스트 실행
req := connect.NewRequest(&greetv1.GreetRequest{Name: "Test"})
res, err := client.Greet(context.Background(), req)

assert.NoError(t, err)
assert.Equal(t, "Hello, Test!", res.Msg.Greeting)
}
```

## 🚀 gRPC 대비 Connect-Go의 장점

### 1. 코드 복잡성 대폭 감소

| 항목      | gRPC     | Connect-Go |
|---------|----------|------------|
| 코드 라인 수 | 130,000+ | 훨씬 적음      |
| 설정 옵션   | 거의 100개  | 핵심 옵션만     |
| 의존성     | 복잡한 의존성  | 표준 라이브러리   |

### 2. 디버깅 및 모니터링

```bash
# Connect Protocol은 표준 HTTP 도구 사용 가능
curl -X POST http://localhost:8080/greet.v1.GreetService/Greet \
  -H "Content-Type: application/json" \
  -d '{"name": "Debug"}' | jq
```
