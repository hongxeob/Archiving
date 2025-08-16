# gRPC 공통 메타데이터 정의

Go 언어 기반 gRPC 서비스에서 사용할 공통 메타데이터 표준을 정의합니다.<br>
이는 서비스 간 일관된 통신과 추적성을 보장하기 위한 필수 요소입니다.

## 메타데이터 구조

### 1. 인증/인가 관련 메타데이터

사용자 식별과 인증을 위한 필수 메타데이터입니다.

```go
// 필수 메타데이터
const (
    MetaUserID    = "user_id"       // 사용자 고유 식별자
    MetaUserName  = "user_name"     // 사용자명
    MetaUserEmail = "user_email"    // 사용자 이메일 (로그인시 이메일 주소지 확인)
    MetaUserType  = "user_type"     // 사용자 타입 (admin, user, guest 등)
)
```

### 2. 요청 컨텍스트 메타데이터

클라이언트 환경과 요청 추적을 위한 필수 메타데이터입니다.

```go
// 필수 메타데이터
const (
    MetaUserAgent   = "user_agent"     // 클라이언트 User-Agent (확인 필)
    MetaClientIP    = "client_ip"      // 클라이언트 IP 주소 (확인 필)
    MetaPlatform    = "platform"       // 플랫폼 (web, mobile, desktop)
    MetaDeviceID    = "device_id"      // 디바이스 고유 식별자
)

// 선택적 메타데이터
const (
    MetaAppVersion   = "app_version"    // 애플리케이션 버전 (optional)
    MetaGeoLocation  = "geo_location"   // 지리적 위치 정보 (optional)
    MetaLatitude     = "latitude"           // 위도
    MetaLongitude    = "longitude"           // 경도
)
```

## 구현 예시

### 메타데이터 설정 (클라이언트)

```go
package client

import (
    "context"
    "google.golang.org/grpc/metadata"
)

func CreateContextWithMetadata(ctx context.Context, userID, userName, userEmail string) context.Context {
    md := metadata.Pairs(
        // 인증/인가 메타데이터
        "user_id", userID,
        "user_name", userName,
        "user_email", userEmail,
        "user_type", "user",
        
        // 요청 컨텍스트 메타데이터
        "user_agent", "MyApp/1.0.0",
        "client_ip", "192.168.1.100",
        "platform", "web",
        "device_id", "device_12345",
        
        // 선택적 메타데이터
        "app_version", "1.2.3",
        "geo_location", "Seoul,KR",
        "lat", "37.5665",
        "lng", "126.9780",
    )
    
    return metadata.NewOutgoingContext(ctx, md)
}
```

### 메타데이터 추출 (서버)

```go
package server

import (
    "context"
    "google.golang.org/grpc/metadata"
)

type RequestMetadata struct {
    UserID      string
    UserName    string
    UserEmail   string
    UserType    string
    UserAgent   string
    ClientIP    string
    Platform    string
    DeviceID    string
    AppVersion  string
    GeoLocation string
    Latitude    string
    Longitude   string
}

func ExtractMetadata(ctx context.Context) (*RequestMetadata, error) {
    md, ok := metadata.FromIncomingContext(ctx)
    if !ok {
        return nil, errors.New("메타데이터가 존재하지 않습니다")
    }
    
    meta := &RequestMetadata{
        UserID:      getMetadataValue(md, "user_id"),
        UserName:    getMetadataValue(md, "user_name"),
        UserEmail:   getMetadataValue(md, "user_email"),
        UserType:    getMetadataValue(md, "user_type"),
        UserAgent:   getMetadataValue(md, "user_agent"),
        ClientIP:    getMetadataValue(md, "client_ip"),
        Platform:    getMetadataValue(md, "platform"),
        DeviceID:    getMetadataValue(md, "device_id"),
        AppVersion:  getMetadataValue(md, "app_version"),
        GeoLocation: getMetadataValue(md, "geo_location"),
        Latitude:    getMetadataValue(md, "lat"),
        Longitude:   getMetadataValue(md, "lng"),
    }
    
    return meta, nil
}

func getMetadataValue(md metadata.MD, key string) string {
    values := md.Get(key)
    if len(values) > 0 {
        return values[0]
    }
    return ""
}
```

## 인터셉터를 통한 자동화

### 클라이언트 인터셉터

```go
func MetadataUnaryClientInterceptor() grpc.UnaryClientInterceptor {
    return func(ctx context.Context, method string, req, reply interface{}, 
                cc *grpc.ClientConn, invoker grpc.UnaryInvoker, 
                opts ...grpc.CallOption) error {
        
        // 기본 메타데이터 자동 추가
        ctx = addDefaultMetadata(ctx)
        
        return invoker(ctx, method, req, reply, cc, opts...)
    }
}
```

### 서버 인터셉터

```go
func MetadataUnaryServerInterceptor() grpc.UnaryServerInterceptor {
    return func(ctx context.Context, req interface{}, info *grpc.UnaryServerInfo, 
                handler grpc.UnaryHandler) (interface{}, error) {
        
        // 메타데이터 검증 및 로깅
        meta, err := ExtractMetadata(ctx)
        if err != nil {
            return nil, status.Errorf(codes.InvalidArgument, "메타데이터 오류: %v", err)
        }
        
        // 로깅
        log.Printf("요청 메타데이터 - UserID: %s, Platform: %s, Method: %s", 
                   meta.UserID, meta.Platform, info.FullMethod)
        
        return handler(ctx, req)
    }
}
```

## 주요 설계 원칙

1. **일관성**: 모든 gRPC 서비스에서 동일한 메타데이터 키 사용
2. **확장성**: 새로운 메타데이터 추가 시 기존 서비스에 영향 최소화
3. **성능**: 메타데이터 크기 최적화로 네트워크 오버헤드 감소
4. **보안**: 민감한 정보는 암호화하여 전송
5. **추적성**: 요청 추적과 디버깅을 위한 충분한 정보 제공

## Best Practice

- 메타데이터 상수는 별도 패키지로 관리하여 공유
- 필수 메타데이터 누락 시 명확한 에러 메시지 제공
- 인터셉터를 활용한 메타데이터 자동 처리
- 로깅과 모니터링을 위한 메타데이터 활용
- 메타데이터 검증 로직 중앙화

이러한 표준화된 메타데이터 정의를 통해 마이크로서비스 간 일관된 통신과 효율적인 서비스 관리를 실현할 수 있습니다.
