# 스프링 빈(Spring bean) 이란?

> [Spring IoC](https://github.com/hongxeob/TIL/blob/main/Spring/IoC%EC%99%80%20%EC%BB%A8%ED%85%8C%EC%9D%B4%EB%84%88.md)
> 컨테이너가 관리하는 자바 객체를 빈(Bean)이라고 부른다

- 자바 프로그래밍에서는 Class를 생성하고 new를 입력하여 원하는 객체를 직접 생성 후 사용했다
- 하지만 Spring에서는 직접 new를 이용하여 생성한 객체가 아니라, Spring에 의하여 관리당하는 자바 객체를 사용한다
- 이렇게 Spring에 의해 생성되고 관리되는 자바 객체를 Bean 이라고 한다

## 스프링에서 빈(Bean)을 등록하는 방법

### 1. @Component (자동)

- 개발자가 직접 작성한 클래스를 Bean으로 등록하고 할 경우 사용
- `@Component` 어노테이션이 붙여져 있는 경우에는 스프링이 확인하여 빈으로 등록시킨다
    - `@Controller`, `@Service`, `@Repository`는 모두 `@Component`를 포함하고 있다.

### 2. @Configuration (수동)

- 외부 라이브러리 또는 내장 클래스를 Bean으로 등록하고자 할 경우 사용(**개발자가 직접 제어가 불가능한 클래스**)
- 빈(Bean) 설정 파일에 직접 등록하는 방법
- 설정 클래스를 따로 만들어 @Configuration 어노테이션을 붙인다
    - 해당 클래스 안에서 빈으로 등록할 메소드를 만들어 @Bean 어노테이션을 붙여주면 자동으로 해당 타입의 빈 객체가 생성된다

## 빈(Bean)의 생명주기

- **스프링 IoC 컨테이너 생성**
    - 스프링 부트에서 Component-Scan 등으로 Bean으로 등록할 객체를 찾는다
- **스프링 빈 생성**
    - @Configuration 방법을 통해 Bean으로 등록할 수 있는 어노테이션들과 설정파일들을 읽어 IoC 컨테이너 안에 Bean으로 등록 시킨다
- **의존 관계 주입 전 준비 단계 (객체의 생성이 일어난다)**

> 알아야 할 점!<br>
>- **생성자 주입 : 객체의 생성과 의존관계 주입이 동시에 일어남**
>   - 자바에서 new 연산을 호출하면 생성자가 호출이 된다. **Controller 클래스에 존재하는 Service 클래스와의 의존관계가 존재하지 않는다면**, Controller 클래스는 객체 생성이 불가능할
     것이다.
>   - 이를 통해 얻는 이점들은
><br>1. Null을 주입하지 않는 한 NPE은 발생하지 않는다.
><br>2. 의존관계를 주입하지 않은 경우 객체를 생성할 수 없다. 즉, 의존관계에 대한 내용을 외부로 노출시킴으로써 컴파일 타임에 오류를 잡을 수 있다!
>- **Setter, Field 주입 : 객체의 생성 → 의존관계 주입으로 라이프 사이클이 나누어져 있음**
>   - setter 주입의 경우 Controller 객체를 만들 때 의존 관계는 필요하지 않다
>   - **즉, 생성자 주입과는 다르게 Controller 객체를 만들때 Service 객체와 의존 관계가 없어도 Controller 객체를 만들 수 있다**
>   - 따라서 객체 생성 → 의존 관계 주입의 단계로 나누어서 Bean 생명주기가 진행된다
- **의존 관계 주입**
- **초기화 콜백 메소드 호출**
- **사용**
- **소멸 전 콜백 메소드 호출**
- **스프링 종료**

## Bean 생명주기 콜백의 종류

> 콜백이란?<br>
주로 콜백 함수를 부를 때 사용되는 용어이며, 콜백 함수를 등록하면 특정 이벤트가 발생했을 때 해당 메소드가 호출 된다
<br>즉 조건에 따라 실행될 수도 실행되지 않을 수도 있는 개념이다

### 1. 인터페이스(InitializingBean, DisposableBean)

```java
public class ExampleBean implements InitializingBean, DisposableBean {
    
    @Override
    public void afterPropertiesSet() throws Exception {
    // 초기화 콜백 (의존관계 주입이 끝나면 호출)
    }
    
    @Override
    public void destroy() throws Exception {
    // 소멸 전 콜백 (메모리 반납, 연결 종료와 같은 과정)
    }
}
```

- 단점 :
    - 스프링 전용 인터페이스들을 상속하므로, 코드가 스프링 인터페이스에 의존적
    - 메소드 이름 변경 불가
    - 코드가 공개되지 않은 외부 라이브러리에 적용 불가

### 2. 설정 정보에 초기화 메소드, 종료 메소드 지정

```java
public class ExampleBean {

    public void init() throws Exception {
        // 초기화 콜백 (의존관계 주입이 끝나면 호출)
    }

    public void close() throws Exception {
        // 소멸 전 콜백 (메모리 반납, 연결 종료와 같은 과정)
    }
}

@Configuration
class LifeCycleConfig {
    
    @Bean(initMethod = "init", destroyMethod = "close")
    public ExampleBean exampleBean() {
        // 생략
    }
}
```

- 클래스 내부에 초기화/종료 메소드를 구현해놓고 `@Bean(initMethod = "init", destroyMethod = "close")` 처럼 Bean Annotation에 추가 아규먼트들을 설정해주는 방식
- 장점 :
    - 메소드 이름이 자유롭다
    - 스프링 빈이 스프링 코드에 의존적이지 않다
    - 코드를 고칠 수 없는 외부 라이브러리에도 초기화,종료 메소드를 적용 가능

### 3. `@PostConstruct`, `@PreDestroy`

```java
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

public class ExampleBean {
 
    @PostConstruct
    public void init() throws Exception {
        // 초기화 콜백 (의존관계 주입이 끝나면 호출)
    }
 
    @PreDestroy
    public void close() throws Exception {
        // 소멸 전 콜백 (메모리 반납, 연결 종료와 같은 과정)
    }
}
```

- 단순 어노테이션을 통해 콜백 함수들을 커스터마이징해준다
- 장점 :
    - 패키지가 `javax.annotation.xxx` 이다. 스프링에 종속적인 기술이 아닌 JSR-250이라는 자바 표준이다. 따라서 스프링이 아닌 다른 컨테이너에서도 동작한다.
    - 스프링 인터페이스에 의존적이지 않고, 최신 스프링에서 권장하는 방식
    - 컴포넌트 스캔과 활용도가 높다
- 단점 :
    - 코드 수정이 불가능한 외부 라이브러리에는 적용할 수 없다

## 빈 스코프(Bean Scope)란 ?

말 그대로 빈이 존재할 수 있는 범위를 뜻한다. 스프링은 다음과 같은 다양한 스코프를 지원한다

### 1. 싱글톤 (Singletone)

- 기본 스코프, 스프링 컨테이너의 시작과 종료까지 유지되는 가장 넓은 범위의 스코프
- 싱글톤 스코프의 빈을 조회하면 스프링 컨테이너는 **항상 같은 인스턴스**의 빈을 반환한다
- `@Component`, `@Configuration` + `@bean`의 조합으로 등록된 bean들의 기본 scope입니다

### 2. 프로토타입 (Prototype)

- 스프링 컨테이너는 프로토타입의 **빈의 생성과 의존관계 주입,초기화**까지만 관여하고, 더는 관리하지 않는 매우 잛은 범위의 스코프
    - 프로토타입 빈을 관리할 책임은 클라이언트에게 있다. 따라서 `@PreDestroy` 같은 종료 콜백 메소드가 호출되지 않는다
- 빈을 조회하면 스프링 컨테이너는 **항상 새로운 인스턴스**를 생성해서 반환한다
    - 싱글톤 컨테이너 생성 시점에 같이 생성되고 초기화 되지만, 프로토타입 빈은 스프링 컨테이너에서 **빈을 조회할 때 생성**되고 초기화 메소드도 실행된다
    - 스프링 컨테이너는 프로토타입 빈을 생성하고, 의존관계 주입, 초기화까지만 처리한다
    - 클라이언트에게 빈을 반환한 이후에는 생성된 프로토타입 빈을 관리하지 않는다
- 프로토타입 scope로 설정하려면 `@Scope(”prototype”)`와 같이 문자열로 지정해준다
- 매번 사용할 때 마다 의존관계 주입이 완료된 새로운 객체가 필요하면 프로토타입을 사용하면 된다
### 문제점 
- 스프링은 일반적으로 싱글톤 빈을 사용하므로, **싱글톤 빈이 프로토타입 빈을 사용**하게 된다
- 따라서 스프링 싱글톤 빈이 생성되는 시점에 프로토 타입 빈도 생성되어서 주입 되긴 하지만, 싱글톤 빈과 함께 계속 유지되는 것이 문제다
- **따라서 프로토타입 빈을 주입 시점에만 새로 생성하는 것이 아니라, 사용할 때마다 새로 생성해서 사용해야 한다**
- ObjectProvider , JSR330 Provider, 스프링 컨테이너 DL등으로 해결할 수 도 있다


### 3. 웹 스코프 (Web Scope)

- 웹 환경에서만 동작하는 스코프
- 프로토타입과는 다르게 스프링이 해당 스코프의 종료 시점까지 관리한다
    - 따라서 종료 메소드가 호출 된다
- `request` : HTTP 요청 하나가 들어오고 나갈 때까지 유지되는 스코프. 각각의 HTTP 요청마다 별도의 빈 인스턴스가 생성되고 관리된다
- `session` : HTTP Session과 동일하게 생명주기를 가지는 스코프
- `aplication` : 서블릿 컨텍스트와 동일한 생명주기를 가지는 스코프
- `websocket` : 웹 소켓과 동일한 생명주기를 가지는 스코프

## 스코프의 선택
>따로 설정을 하지 않는 것이 싱글톤 스코프 이다. 특수한 상황이 아니라면 디폴트 설정을 사용하는 것이 좋다
