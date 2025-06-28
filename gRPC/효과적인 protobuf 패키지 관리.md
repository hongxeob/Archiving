# MSA + gRPC 환경에서 효과적인 protobuf 스킴 관리

```shell
DDD
root/proto/
├── shared/                    # 공통 타입
│   └── v1/
│       ├── common.proto       # 기본 타입들
│       ├── errors.proto       # 에러 정의
│       └── events.proto       # 도메인 이벤트
│
├── internal(혹은 services)/                  # 내부 서비스간 통신
│   ├── order/
│   │   └── v1/
│   │       ├── order.proto
│   │       └── order_service.proto
│   ├── payment/
│   │   └── v1/
│   │       ├── payment.proto
│   │       └── payment_service.proto
│   ├── product/
│   │   └── v1/
│   │       ├── product.proto
│   │       └── product_service.proto
│   ├── board/
│   │   └── v1/
│   │       ├── board.proto
│   │       └── board_service.proto
│   └── ... (n개의 마이크로 서비스)
│
├── gateway/                   # BFF API 정의
│   ├── root/                 # 사용자용 Gateway
│   │   └── v1/
│   │       ├── user_api.proto
│   │       ├── order_api.proto
│   │       ├── product_api.proto
│   │       └── payment_api.proto
│   ├── partner/              # Partner Office
│   │   └── v1/
│   │       ├── partner_api.proto
│   │       ├── order_management_api.proto
│   │       └── product_management_api.proto
│   └── admin/                # Back Office
│       └── v1/
│           ├── admin_api.proto
│           ├── user_management_api.proto
│           └── system_api.proto
│
└── events/                   # 이벤트 스트리밍
    ├── order/
    │   └── v1/
    │       └── order_events.proto
    ├── payment/
    │   └── v1/
    │       └── payment_events.proto
    └── ...
```
### 장점

- 명확한 책임 분리: Internal vs Gateway API
- 권한별 인터페이스: 각 Gateway마다 적절한 권한 수준
- 독립적 버전 관리: 내부 서비스와 Gateway API 독립적 발전
- 재사용성: 내부 서비스를 여러 Gateway에서 조합 사용
- 보안: Gateway별로 다른 인증/인가 정책 적용 가능

---

# Root Proto Package Structure

## 📁 디렉토리 구조

```
root/proto/
├── status.proto                    # 공통 상태 정의
├── order/                          # 주문 도메인
│   ├── v1/
│   │   ├── service.proto          # OrderService 정의
│   │   ├── model.proto            # Order 관련 모델
│   │   └── event.proto            # Order 도메인 이벤트
├── payment/                        # 결제 도메인
│   └── v1/
│       ├── service.proto
│       ├── model.proto
│       └── event.proto
├── product/                        # 상품 도메인
│   └── v1/
│       ├── service.proto
│       ├── model.proto
│       └── event.proto
├── display/                        # 전시 도메인
│   └── v1/
│       ├── service.proto
│       ├── model.proto
│       └── event.proto
├── user/                           # 사용자 도메인
│   └── v1/
│       ├── service.proto
│       ├── model.proto
│       └── event.proto
└── ... (30개 도메인)
```

## 📋 패키지 네이밍 규칙

### gRPC Service 패키지명
- **형식**: `order-api.root.in/order.v1.OrderService/MethodName`
- **예시**:
    - `order-api.root.in/order.v1.OrderService/GetOrderList`
    - `payment-api.root.in/payment.v1.PaymentService/ProcessPayment`
    - `product-api.root.in/product.v1.ProductService/GetProductDetail`

### Proto 패키지 선언
```protobuf
syntax = "proto3";
package order.v1;
option go_package = "root/gen/go/order/v1;orderv1";
```

## 🏗️ 파일별 역할

### 1. `status.proto` - 공통 상태 정의
```protobuf
syntax = "proto3";
package root;
option go_package = "root/gen/go/shared;shared";

// 공통 상태 코드
enum Status {
  STATUS_UNSPECIFIED = 0;
  STATUS_ACTIVE = 1;
  STATUS_INACTIVE = 2;
  STATUS_PENDING = 3;
  STATUS_COMPLETED = 4;
  STATUS_CANCELLED = 5;
  STATUS_FAILED = 6;
}

// 공통 응답 타입
message Response {
  bool success = 1;
  string message = 2;
  int32 code = 3;
}
```

### 2. `{domain}/v1/service.proto` - 서비스 인터페이스
```protobuf
syntax = "proto3";
package order.v1;
option go_package = "root/gen/go/order/v1;orderv1";

import "order/v1/model.proto";
import "status.proto";

service OrderService {
  rpc GetOrderList(GetOrderListRequest) returns (GetOrderListResponse);
  rpc GetOrderDetail(GetOrderDetailRequest) returns (GetOrderDetailResponse);
  rpc CreateOrder(CreateOrderRequest) returns (CreateOrderResponse);
  rpc UpdateOrderStatus(UpdateOrderStatusRequest) returns (UpdateOrderStatusResponse);
  rpc CancelOrder(CancelOrderRequest) returns (CancelOrderResponse);
}
```

### 3. `{domain}/v1/model.proto` - 도메인 모델
```protobuf
syntax = "proto3";
package order.v1;
option go_package = "root/gen/go/order/v1;orderv1";

import "status.proto";

message Order {
  string order_id = 1;
  string user_id = 2;
  repeated OrderItem items = 3;
  OrderStatus status = 4;
  int64 created_at = 5;
  int64 updated_at = 6;
  string delivery_address = 7;
  int64 total_amount = 8;
}

message OrderItem {
  string product_id = 1;
  string product_name = 2;
  int32 quantity = 3;
  int64 price = 4;
  int64 total_price = 5;
}

enum OrderStatus {
  ORDER_STATUS_UNSPECIFIED = 0;
  ORDER_STATUS_PENDING = 1;
  ORDER_STATUS_CONFIRMED = 2;
  ORDER_STATUS_PREPARING = 3;
  ORDER_STATUS_SHIPPING = 4;
  ORDER_STATUS_DELIVERED = 5;
  ORDER_STATUS_CANCELLED = 6;
}
```

### 4. `{domain}/v1/event.proto` - 도메인 이벤트
```protobuf
syntax = "proto3";
package order.v1;
option go_package = "root/gen/go/order/v1;orderv1";

import "order/v1/model.proto";

// 주문 생성 이벤트
message OrderCreatedEvent {
  string event_id = 1;
  string order_id = 2;
  Order order = 3;
  int64 occurred_at = 4;
  string user_id = 5;
}

// 주문 상태 변경 이벤트
message OrderStatusChangedEvent {
  string event_id = 1;
  string order_id = 2;
  OrderStatus old_status = 3;
  OrderStatus new_status = 4;
  int64 occurred_at = 5;
  string changed_by = 6;
  string reason = 7;
}

// 주문 취소 이벤트
message OrderCancelledEvent {
  string event_id = 1;
  string order_id = 2;
  string reason = 3;
  int64 occurred_at = 4;
  string cancelled_by = 5;
}
```

## ⚙️ 코드 생성 설정

### `buf.yaml`
```yaml
version: v1
breaking:
  use:
    - FILE
lint:
  use:
    - DEFAULT
build:
  roots:
    - proto
```

### `buf.gen.yaml`
```yaml
version: v1
plugins:
  # Go 백엔드
  - plugin: go
    out: gen/go
    opt: paths=source_relative
  - plugin: go-grpc
    out: gen/go
    opt: paths=source_relative
  - plugin: connect-go
    out: gen/go
    opt: paths=source_relative
    
  # TypeScript 프론트엔드
  - plugin: es
    out: gen/ts
    opt: target=ts
  - plugin: connect-es
    out: gen/ts
    opt: target=ts
```

## 🚀 사용법

### 1. 코드 생성
```bash
# Proto 파일에서 코드 생성
buf generate

# 생성된 파일 확인
tree gen/
gen/
├── go/
│   ├── order/v1/
│   │   ├── model.pb.go
│   │   ├── service.pb.go
│   │   ├── service_grpc.pb.go
│   │   ├── service_connect.go
│   │   └── event.pb.go
│   └── shared/
│       └── status.pb.go
└── ts/
    ├── order/v1/
    │   ├── model_pb.ts
    │   ├── service_pb.ts
    │   ├── service_connect.ts
    │   └── event_pb.ts
    └── shared/
        └── status_pb.ts
```

### 2. 백엔드에서 사용 (Go)
```go
package main

import (
    orderv1 "root/gen/go/order/v1"
    "root/gen/go/order/v1/orderv1connect"
)

// 서비스 구현
type OrderService struct {
    orderv1connect.UnimplementedOrderServiceHandler
}

func (s *OrderService) GetOrderList(
    ctx context.Context,
    req *connect.Request[orderv1.GetOrderListRequest],
) (*connect.Response[orderv1.GetOrderListResponse], error) {
    // 비즈니스 로직 구현
    orders := []*orderv1.Order{
        {
            OrderId: "order-123",
            UserId:  req.Msg.UserId,
            Status:  orderv1.OrderStatus_ORDER_STATUS_CONFIRMED,
        },
    }
    
    return connect.NewResponse(&orderv1.GetOrderListResponse{
        Orders: orders,
    }), nil
}
```

### 3. 프론트엔드에서 사용 (TypeScript)
```typescript
import { createClient } from '@connectrpc/connect';
import { createConnectTransport } from '@connectrpc/connect-web';
import { OrderService } from './gen/ts/order/v1/service_connect';

const transport = createConnectTransport({
  baseUrl: 'https://order-api.root.in',
});

const client = createClient(OrderService, transport);

// API 호출
const response = await client.getOrderList({
  userId: 'user-123',
  page: 1,
  pageSize: 20
});

console.log(response.orders);
```

## 📚 개발 가이드라인

### 1. 새 도메인 추가 시
1. `proto/{domain}/v1/` 디렉토리 생성
2. `service.proto`, `model.proto`, `event.proto` 파일 생성
3. 패키지명을 `{domain}.v1`로 설정
4. `buf generate` 실행

### 2. API 엔드포인트 규칙
- **서비스명**: `{Domain}Service` (예: `OrderService`, `PaymentService`)
- **메서드명**: 동사 + 명사 조합 (예: `GetOrderList`, `CreateOrder`)
- **엔드포인트**: `{domain}-api.root.in/{domain}.v1.{Domain}Service/{Method}`

### 3. 버전 관리
- 하위 호환성 유지: 필드 추가는 가능, 삭제/변경 시 새 버전 생성
- 버전 업그레이드 시: `v1` → `v2` 디렉토리로 분리
- 구버전 지원 기간: 최소 6개월

### 4. 이벤트 명명 규칙
- **형식**: `{Domain}{Action}Event`
- **예시**: `OrderCreatedEvent`, `PaymentProcessedEvent`, `ProductUpdatedEvent`
- **필수 필드**: `event_id`, `occurred_at`, 도메인 관련 필드

## 🔧 개발 도구

### 필수 도구 설치
```bash
# Buf CLI 설치
curl -sSL "https://github.com/bufbuild/buf/releases/latest/download/buf-$(uname -s)-$(uname -m)" -o "/usr/local/bin/buf"
chmod +x "/usr/local/bin/buf"

# 코드 생성 플러그인 설치
go install google.golang.org/protobuf/cmd/protoc-gen-go@latest
go install google.golang.org/grpc/cmd/protoc-gen-go-grpc@latest
go install connectrpc.com/connect/cmd/protoc-gen-connect-go@latest
```

### VS Code 확장
- **vscode-proto3**: Proto 파일 구문 하이라이트
- **Buf**: Proto 파일 린팅 및 포맷팅

## 📞 문의 및 지원
- **Proto 구조 관련**: Backend Team Lead
- **코드 생성 이슈**: DevOps Team
- **API 설계 가이드**: Architecture Team

---
*이 문서는 Root Proto 패키지 구조 v1.0 기준으로 작성되었습니다.*

## 장점
- 명확한 도메인 분리: 각 도메인이 독립적으로 관리됨
- 확장성: 30개 서비스가 각각 독립적인 패키지로 확장 가능
- 버전 관리: v1, v2 등으로 하위 호환성 유지
- 이벤트 응집성: 도메인 이벤트가 해당 도메인 내에서 관리됨