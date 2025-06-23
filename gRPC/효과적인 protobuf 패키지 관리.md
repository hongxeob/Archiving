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