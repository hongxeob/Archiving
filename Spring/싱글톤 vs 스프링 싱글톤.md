# 싱글톤 VS 스프링 싱글톤

### [우선, 싱글톤 패턴에 관하여 궁금하다면!](https://github.com/hongxeob/TIL/blob/main/CS/%EB%94%94%EC%9E%90%EC%9D%B8%20%ED%8C%A8%ED%84%B4.md)

## Java 싱글톤 패턴

싱글톤 클래스를 구현하기 위해 사전 정의된 규칙 같은건 없지만 `*클래스 로더`마다 한 번만 인스턴스화 해야한다

- 싱글톤 클래스라고 하면 해당 클래스의 인스턴스를 하나만 만들 수 있다
- 구현 방법
    - 외부 클래스로부터 인스턴스화 되는 것을 막기 위해 생성자는 `private`으로 선언되어야 한다
    - 인스턴스화 된 클래스를 `static 변수`로 선언한다 = 어느 영역에서든 접근 가능
    - 인스턴스화 된 클래스를 리턴하는 함수를 선언한다
    - 이렇게 하면 클래스가 클래스 로더에 의해 단 한번만 인스턴스화 되는 것을 이용하여 구현한다

>***클래스 로더**란<br>
자바는 동적 로드, 즉 컴파일 타임이 아니라 런타임(바이트 코드를 실행할 때)에 클래스 로드하고 링크하는 특징이 있다<br>
이 동적 로드를 담당하는 부분이 JVM의 클래스 로더이다.<br>
정리하자면, 자바 클래스들은 한 번에 모든 클래스가 메모리에 올라가지 않는다. <br>각 클래스들은 필요할 때 애플리케이션에 올라가게 되며, 이 작업을 클래스로더가 해주게 된다.<br>
**클래스 로더는 런타임 중에 JVM의 메소드 영역에 동적으로 Java 클래스를 로드하는 역할을 한다**

### `Thread Safety`를 보장하는 구현법들
- getter 메소드의 `Synchronized` 선언
    - `synchronized`를 선언하지 않았을 때에 발생할 수 있는 문제는 2개의 thread가 동시에 `getInstance()`를 호출한다면 동시에 2개의 객체가 생성되어 버릴 수도 있을 것이다.
- Bill Pugh 구현법 : 클래스 내부에 `static class` 선언
    
    ```java
    public class JavaSingleton  
    { 
      
      private JavaSingleton()
      
      private static class BillPughSingleton 
      { 
        private static final JavaSingleton instance = new JavaSingleton(); 
      } 
      
      public static JavaSingleton getInstance()  
      { 
        return BillPughSingleton.instance; 
      } 
    }
    ```
    
    - 가장 보편적으로 사용되는 싱글톤 구현법
    - static 키워드로 선언된 객체는 메모리 할당이 단 한 번만 이루어지고 final 키워드로 선언되면 그 값을 덮어쓸 수 없음을 이용하는것

## Spring 싱글톤

- 컨테이너 내에서 특정 클래스에 대하여 `@Bean` 이 정의되면 스프링 컨테이너는 그 클래스에 대해서 **딱 한 개의 인스턴스**를 만든다
- 그 공유 인스턴스는 설정 정보에 의해서 관리되고, Bean이 호출될 때마다 **스프링은 생성된 공유 인스턴스를 리턴 시킨다**
- Bean의 관리주체인 스프링 컨테이너는 항상 **단일 공유 인스턴스**를 리턴시키므로 `Thread Safety`도 자동 보장된다

## 결론

- Java의 싱글톤은 클래스 로더에 의해 구현되고, 스프링 싱글톤은 스프링 컨테이너에 의해서 구현된다
- Java의 싱글톤의 스코프는 **코드 전체**이고, 스프링 싱글톤의 스코프는 **해당 컨테이너의 내부** 이다
- 스프링에 의해 구현된 싱글톤 패턴은 Thread Safety를 자동으로 보장한다
    - 하지만 Java 싱글톤의 경우 구현하는 개발자의 로직에 따라 보장할 수도 않을 수도 있다