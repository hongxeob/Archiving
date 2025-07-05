# 컨텍스트의 활용 예 : `Http.Request`

> 웹 어플리케이션에서 사용자의 요청이 들어왔을 때, 요청한 작업을 수행한 후에 클라이언트로 response를 전달할 때까지를 하나의 맥락이라고 볼 수 있다.<br>
> 이 하나의 맥락 안에서 유지되어야 할 값들이 있다면 그것을 컨텍스트에 담아두고 필요한 곳에서 사용하면 된다.

```go
package http

type Request struct {
	Method string	
	Header Header
	Body io.ReadCloser		

	/* ... */

	ctx context.Context // <- 이 컨텍스트에 웹 요청이 완료될 때까지 유지해야 하는 값을 보관한다.
}
```
- 웹 서버에서 요청이 들어오면 `http.Request` 값을 만들어 핸들러 함수로 전달하는데 이때 컨텍스트를 생성한다.
  - `http.Request`의 Context 함수를 사용하면 이 컨텍스트를 가져올 수 있다.
  ```go
  package http

  func (r *Request) Context() context.Context
   ```
아래는 웹 요청을 처리하는 핸들러 함수이다. http.Request의 컨텍스트로 부터 "current_user" 값을 가져와서 사용한다.
```go
func handler(w http.ResponseWriter, r *http.Request) {
	var currentUser User

	// 컨텍스트에서 값을 가져옴
	if v := r.Context().Value("current_user"); v == nil {
		// "current_user"가 존재하지 않으면 401 에러 리턴
		http.Error(w, "Not Authorized", http.StatusUnauthorized)
		return
	} else {
		u, ok := v.(User)
		if !ok {
			// 타입이 User가 아니면 401 에러 리턴
			http.Error(w, "Not Authorized", http.StatusUnauthorized)
			return
		}

		currentUser = u
	}

	fmt.Fprintf(w, "Hi I am %s", currentUser.Name)
}
```
아래는 미들웨어 함수이다.

```go
func authMiddleware(next http.HandlerFunc) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		// 1. 사용자의 현재 세션 정보를 기반으로 currentUser 생성
		currentUser, err := getCurrentUser(r)
		if err != nil {
			http.Error(w, "Not Authorized", http.StatusUnauthorized)
			return
		}

		// 2. 기본 컨텍스트에 current_user를 담아 새로운 컨텍스트 생성
		ctx := context.WithValue(r.Context(), "current_user", currentUser)

		// 3. 새로 생성한 컨텍스트 할당한 새로운 `http.Request` 생성
		nextRequest := r.WithContext(ctx)

		// 4. 다음 핸들러 호출
		next(w, nextRequest)
	}
}
```
1. 사용자의 현재 세션 정보를 기반으로 currentUser를 생성하여
2. http.Request의 컨텍스트에 currentUser를 담아 새로운 컨텍스트를 생성하였다.
3. 그리고 새로 생성한 컨텍스트 할당한 새로운 http.Request를 생성하여
4. 다음 핸들러를 호출하게 하였다.