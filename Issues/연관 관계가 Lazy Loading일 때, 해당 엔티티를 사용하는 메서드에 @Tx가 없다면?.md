## 🤔 문제 상황
JPA에서 엔티티 간 연관 관계를 `Lazy Loading`으로 설정했을 때, 이 엔티티를 사용하는 메서드에 `@Transactional` 어노테이션이 없다면 **LazyInitializationException**이 발생할 수 있다.
예시 코드로 보자면
```java
@Entity
public class User {
    @Id
    private Long id;
    
    @OneToMany(fetch = FetchType.LAZY)
    private List<Order> orders;
}

public class UserService {
    public void processUserOrders(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        user.getOrders().forEach(order -> {
            // 주문 처리 로직
        });
    }
}
```
이 경우, `processUserOrders` 메서드에서 `user.getOrders()`를 호출할 때 LazyInitializationException이 발생할 수 있다.
## 🕊️ 나의 생각
실제 사용하는 시점에서 영속성 컨텍스트가 이미 닫혀있기 때문이라고 생각했다. -> 그래서 지연 로딩을 수행할 수 없다!
이 문제는 트랜잭션 범위와 영속성 컨텍스트의 생명주기가 일치하지 않아 발생한다. 
`@Transactional` 어노테이션이 없으면, **데이터베이스 조회 후 즉시 영속성 컨텍스트가 닫히게 되어** 지연 로딩을 수행할 수 없게 된다!!
## 💫 해결 방안
1. `@Transactional` 추가 : 가장 간단한 해결 방법은 해당 메서드에 `@Transactional`을 추가하는 것이다. 이렇게 하면 메서드 실행 동안 영속성 컨텍스트가 유지되어 지연 로딩이 가능해진다.
```java
@Transactional
public void processUserOrders(Long userId) {
    // 기존 코드
}
```

2. `Fetch Join` 사용: JPQL이나 Criteria API를 사용하여 필요한 연관 엔티티를 미리 로딩할 수 있다.
```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u JOIN FETCH u.orders WHERE u.id = :userId")
    Optional<User> findByIdWithOrders(@Param("userId") Long userId);
}
```
3. `DTO` 사용: 필요한 데이터만 DTO로 조회하여 사용한다.(Projection) 이 방법은 불필요한 데이터 로딩을 방지할 수 있다.
4. `Open Session In View (OSIV)` 패턴 사용: 이 방법은 **영속성 컨텍스트를 뷰 렌더링까지 유지**하지만, 데이터베이스 커넥션을 오래 유지하는 단점이 있어 신중하게 사용해야 한다.
5. 프록시 초기화: 트랜잭션 내에서 연관 엔티티를 미리 초기화하는 방법도 있다.
```java
@Transactional(readOnly = true)
public void processUserOrders(Long userId) {
    User user = userRepository.findById(userId).orElseThrow();
    Hibernate.initialize(user.getOrders());
    // 이후 로직
}
```