## 더미 (Dummy)
```java
class DummyEmailService : EmailService {
    override fun sendEmail(email: String, message: String) {
        // 아무 동작도 하지 않음 - 단순히 파라미터를 채우기 위한 용도
    }
}

// 사용 예시
@Test
fun testUserRegistration() {
    val dummyEmailService = DummyEmailService()
    val userService = UserService(dummyEmailService)  // 이메일 서비스는 실제로 사용되지 않음
    userService.register("test@test.com")
}
```
- 가장 단순한 형태
- **실제로 사용되지 않고** 단순히 인스턴스화된 객체가 필요한 경우 사용
- 메서드가 호출되어도 아무 동작도 하지 않음
- 어떤 비즈니스 애플리케이션에 전달해야 할 인수가 여러 개 있지만 테스트는 이들 중 몇개만 수행할 때 흔히 볼 수 있다.
## 페이크 (Fake)
```java
class FakeUserRepository : UserRepository {
    private val users = mutableListOf<User>()
    
    override fun save(user: User) {
        users.add(user)
    }
    
    override fun findById(id: Long): User? {
        return users.find { it.id == id }
    }
}

// 사용 예시
@Test
fun testUserOperations() {
    val fakeRepo = FakeUserRepository()
    val userService = UserService(fakeRepo)
    userService.createUser(User("test", "test@test.com"))
}
```
- 실제 구현을 단순화한 구현체
- 실제로 동작하는 구현을 가짐
- 주로 메모리 내 저장소로 구현됨
## 스텁 (Stub)
```java
class PaymentServiceStub : PaymentService {
    override fun processPayment(amount: Double): PaymentResult {
        // 항상 성공 반환
        return PaymentResult(true, "Payment Successful")
    }
}

// 사용 예시
@Test
fun testOrderProcessing() {
    val stubPayment = PaymentServiceStub()
    val orderService = OrderService(stubPayment)
    val result = orderService.createOrder(100.0)
    assert(result.isSuccess)
}
```
- 미리 준비된 답변을 제공
- 호출에 대해 **하드코딩**된 응답을 반환
- 테스트를 위해 프로그래밍된 응답만 제공
## 모의 객체 (Mock)
```java
@Test
fun testUserNotification() {
    // Mockito 사용 예시
    val mockNotificationService = mock(NotificationService::class.java)
    
    // 동작 정의
    whenever(mockNotificationService.send(any()))
        .thenReturn(true)
    
    val userService = UserService(mockNotificationService)
    userService.notifyUser("test message")
    
    // 호출 검증
    verify(mockNotificationService).send("test message")
    verify(mockNotificationService, times(1)).send(any())
}
```
- 호출 여부와 방법을 검증
- 예상된 동작을 미리 프로그래밍 가능
- **실제로 호출되었는지 검증**하는 것이 주 목적
## 스파이 (Spy)
```java
class EmailServiceSpy : EmailService {
    var emailsSent = 0
    private val sentEmails = mutableListOf<String>()
    
    override fun sendEmail(email: String, message: String) {
        emailsSent++
        sentEmails.add(email)
    }
    
    fun getLastEmailSent(): String? = sentEmails.lastOrNull()
}

// 사용 예시
@Test
fun testEmailSending() {
    val emailSpy = EmailServiceSpy()
    val userService = UserService(emailSpy)
    
    userService.sendWelcomeEmail("test@test.com")
    
    assertEquals(1, emailSpy.emailsSent)
    assertEquals("test@test.com", emailSpy.getLastEmailSent())
}
```
- 이름에서 알 수 있듯이 의존성을 감시한다.
- 실제 객체를 감싸서 추가 정보를 기록
- 호출된 내용을 기록하면서 실제 동작도 수행 가능
- 모의 객체와 실제 객체의 중간 형태
- 테스트 대상 메서드가 의존 대상과 어떻게 상호작용하는지 단언하고자 하는 경우에 사용된다.
---
## 요약
- 더미: 아무 동작도 하지 않음
- 페이크: 단순화된 실제 구현 제공
- 스텁: 미리 준비된 응답만 제공
- 모의 객체: 예상된 동작을 검증
- 스파이: 실제 동작을 하면서 호출 정보도 기록
