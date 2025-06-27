# gRPC 공통 metadata

## 1. 인증/인가 관련 메타데이터

### shared/v1/auth.proto
```protobuf
syntax = "proto3";
package root.shared.v1;
option go_package = "root/gen/go/shared/v1;sharedv1";

// 공통 인증 정보
message AuthContext {
  string user_id = 1;
  string session_id = 2;
  string access_token = 3;
  repeated string roles = 4;
  repeated string permissions = 5;
  string tenant_id = 6;        // 멀티테넌트
  AuthType auth_type = 7;
  int64 expires_at = 8;
}

enum AuthType {
  AUTH_TYPE_UNSPECIFIED = 0;
  AUTH_TYPE_USER = 1;          // 일반 사용자
  AUTH_TYPE_PARTNER = 2;       // 파트너
  AUTH_TYPE_ADMIN = 3;         // 어드민
  AUTH_TYPE_SERVICE = 4;       // 서비스간 통신
}
```

## 2. 요청 컨텍스트 메타데이터

### shared/v1/context.proto
```protobuf
syntax = "proto3";
package root.shared.v1;
option go_package = "root/gen/go/shared/v1;sharedv1";

// 요청 추적 정보
message RequestContext {
  string request_id = 1;       // 유니크 요청 ID
  string trace_id = 2;         // 분산 추적 ID
  string span_id = 3;          // 스팬 ID
  string correlation_id = 4;   // 비즈니스 상관관계 ID
  string client_ip = 5;        // 클라이언트 IP
  string user_agent = 6;       // User-Agent
  string gateway_type = 7;     // root/partner/admin
  int64 timestamp = 8;         // 요청 시간
  map<string, string> baggage = 9; // 추가 컨텍스트 데이터
}

// 클라이언트 정보
message ClientContext {
  string client_id = 1;        // 클라이언트 식별자
  string client_version = 2;   // 클라이언트 버전
  string platform = 3;        // web/mobile/api
  string device_id = 4;        // 디바이스 ID
  string app_version = 5;      // 앱 버전
  GeoLocation location = 6;    // 위치 정보
}

message GeoLocation {
  double latitude = 1;
  double longitude = 2;
  string country_code = 3;
  string region = 4;
  string city = 5;
}
```

## 3. 페이징 및 정렬 공통 메타데이터

### shared/v1/pagination.proto
```protobuf
syntax = "proto3";
package root.shared.v1;
option go_package = "root/gen/go/shared/v1;sharedv1";

// 페이징 요청
message PaginationRequest {
  int32 page = 1;              // 페이지 번호 (1부터 시작)
  int32 page_size = 2;         // 페이지 크기
  string cursor = 3;           // 커서 기반 페이징
  int64 offset = 4;            // 오프셋 기반 페이징
  int32 limit = 5;             // 최대 개수
}

// 페이징 응답
message PaginationResponse {
  int32 page = 1;
  int32 page_size = 2;
  int64 total_count = 3;
  int32 total_pages = 4;
  bool has_next = 5;
  bool has_previous = 6;
  string next_cursor = 7;
  string previous_cursor = 8;
}

// 정렬 옵션
message SortOption {
  string field = 1;
  SortDirection direction = 2;
}

enum SortDirection {
  SORT_DIRECTION_UNSPECIFIED = 0;
  SORT_DIRECTION_ASC = 1;
  SORT_DIRECTION_DESC = 2;
}
```

## 4. 필터링 공통 메타데이터

### shared/v1/filter.proto
```protobuf
syntax = "proto3";
package root.shared.v1;
option go_package = "root/gen/go/shared/v1;sharedv1";

// 공통 필터
message Filter {
  repeated FieldFilter field_filters = 1;
  repeated RangeFilter range_filters = 2;
  DateRangeFilter date_range = 3;
  repeated string search_terms = 4;
  map<string, string> custom_filters = 5;
}

message FieldFilter {
  string field = 1;
  FilterOperator operator = 2;
  repeated string values = 3;
}

message RangeFilter {
  string field = 1;
  string min_value = 2;
  string max_value = 3;
  bool include_min = 4;
  bool include_max = 5;
}

message DateRangeFilter {
  string field = 1;
  int64 start_timestamp = 2;
  int64 end_timestamp = 3;
}

enum FilterOperator {
  FILTER_OPERATOR_UNSPECIFIED = 0;
  FILTER_OPERATOR_EQUALS = 1;
  FILTER_OPERATOR_NOT_EQUALS = 2;
  FILTER_OPERATOR_IN = 3;
  FILTER_OPERATOR_NOT_IN = 4;
  FILTER_OPERATOR_CONTAINS = 5;
  FILTER_OPERATOR_STARTS_WITH = 6;
  FILTER_OPERATOR_ENDS_WITH = 7;
}
```

## 5. 에러 처리 공통 메타데이터

### shared/v1/errors.proto
```protobuf
syntax = "proto3";
package root.shared.v1;
option go_package = "root/gen/go/shared/v1;sharedv1";

// 표준 에러 응답
message ErrorResponse {
  string code = 1;             // 에러 코드
  string message = 2;          // 에러 메시지
  string detail = 3;           // 상세 설명
  repeated ErrorField field_errors = 4; // 필드별 에러
  map<string, string> metadata = 5;     // 추가 메타데이터
  string trace_id = 6;         // 추적 ID
  int64 timestamp = 7;         // 에러 발생 시간
}

message ErrorField {
  string field = 1;
  string code = 2;
  string message = 3;
}

// 표준 에러 코드
enum ErrorCode {
  ERROR_CODE_UNSPECIFIED = 0;
  ERROR_CODE_INVALID_ARGUMENT = 1;
  ERROR_CODE_NOT_FOUND = 2;
  ERROR_CODE_ALREADY_EXISTS = 3;
  ERROR_CODE_PERMISSION_DENIED = 4;
  ERROR_CODE_UNAUTHENTICATED = 5;
  ERROR_CODE_RESOURCE_EXHAUSTED = 6;
  ERROR_CODE_FAILED_PRECONDITION = 7;
  ERROR_CODE_ABORTED = 8;
  ERROR_CODE_OUT_OF_RANGE = 9;
  ERROR_CODE_UNIMPLEMENTED = 10;
  ERROR_CODE_INTERNAL = 11;
  ERROR_CODE_UNAVAILABLE = 12;
  ERROR_CODE_DATA_LOSS = 13;
}
```

## 6. 캐싱 관련 메타데이터

### shared/v1/cache.proto
```protobuf
syntax = "proto3";
package root.shared.v1;
option go_package = "root/gen/go/shared/v1;sharedv1";

// 캐시 옵션
message CacheOptions {
  int32 ttl_seconds = 1;       // TTL (초)
  bool no_cache = 2;           // 캐시 사용 안함
  bool force_refresh = 3;      // 강제 새로고침
  repeated string cache_tags = 4; // 캐시 태그
  CacheLevel level = 5;        // 캐시 레벨
}

enum CacheLevel {
  CACHE_LEVEL_UNSPECIFIED = 0;
  CACHE_LEVEL_NONE = 1;
  CACHE_LEVEL_MEMORY = 2;
  CACHE_LEVEL_REDIS = 3;
  CACHE_LEVEL_CDN = 4;
}
```

## 7. 감사 로그 메타데이터

### shared/v1/audit.proto
```protobuf
syntax = "proto3";
package root.shared.v1;
option go_package = "root/gen/go/shared/v1;sharedv1";

// 감사 로그 정보
message AuditLog {
  string action = 1;           // 수행된 액션
  string resource_type = 2;    // 리소스 타입
  string resource_id = 3;      // 리소스 ID
  string user_id = 4;          // 수행 사용자
  int64 timestamp = 5;         // 수행 시간
  string ip_address = 6;       // IP 주소
  string user_agent = 7;       // User-Agent
  map<string, string> changes = 8; // 변경 내용
  AuditLevel level = 9;        // 감사 레벨
}

enum AuditLevel {
  AUDIT_LEVEL_UNSPECIFIED = 0;
  AUDIT_LEVEL_INFO = 1;
  AUDIT_LEVEL_WARNING = 2;
  AUDIT_LEVEL_CRITICAL = 3;
}
```

## 8. 실제 사용 예시

### Gateway에서 공통 메타데이터 주입

```go
// middleware/metadata.go
func InjectCommonMetadata() connect.UnaryInterceptorFunc {
    return func(next connect.UnaryFunc) connect.UnaryFunc {
        return func(ctx context.Context, req connect.AnyRequest) (connect.AnyResponse, error) {
            // Request Context 생성
            requestCtx := &sharedv1.RequestContext{
                RequestId:     generateRequestID(),
                TraceId:       trace.SpanFromContext(ctx).SpanContext().TraceID().String(),
                SpanId:        trace.SpanFromContext(ctx).SpanContext().SpanID().String(),
                GatewayType:   "root", // 또는 "partner", "admin"
                Timestamp:     time.Now().Unix(),
                ClientIp:      getClientIP(ctx),
                UserAgent:     getUserAgent(ctx),
            }
            
            // 메타데이터로 주입
            ctx = metadata.AppendToOutgoingContext(ctx,
                "request-context", marshalProto(requestCtx),
                "auth-context", marshalProto(getAuthContext(ctx)),
            )
            
            return next(ctx, req)
        }
    }
}
```

### 내부 서비스에서 메타데이터 추출

```go
// service/order/handler.go
func (s *OrderService) CreateOrder(ctx context.Context, req *orderv1.CreateOrderRequest) (*orderv1.CreateOrderResponse, error) {
    // 공통 메타데이터 추출
    requestCtx := extractRequestContext(ctx)
    authCtx := extractAuthContext(ctx)
    
    // 감사 로그
    auditLog := &sharedv1.AuditLog{
        Action:       "CREATE_ORDER",
        ResourceType: "ORDER",
        UserId:       authCtx.UserId,
        Timestamp:    time.Now().Unix(),
        IpAddress:    requestCtx.ClientIp,
    }
    
    // 비즈니스 로직...
    order, err := s.createOrder(ctx, req, authCtx)
    if err != nil {
        return nil, err
    }
    
    // 감사 로그 기록
    s.auditLogger.Log(ctx, auditLog)
    
    return &orderv1.CreateOrderResponse{Order: order}, nil
}
```

## 9. 장점들

1. **일관성**: 모든 서비스에서 동일한 메타데이터 구조
2. **재사용성**: 공통 로직을 한 번만 구현
3. **유지보수성**: 메타데이터 변경 시 한 곳만 수정
4. **타입 안전성**: Proto로 정의되어 컴파일 타임 검증
5. **문서화**: Proto 자체가 문서 역할
6. **버전 관리**: Proto 버전 관리로 호환성 보장

이렇게 공통화하면 30개 서비스 모두에서 일관된 메타데이터 처리가 가능해져요!