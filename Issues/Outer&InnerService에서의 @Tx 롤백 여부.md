## 🤔 문제 상황
스케줄러를 사용하고 있는 프로젝트가 있다. 해당 스케줄러가 주기적으로 동작하던 와중 아래와 같은 롤백 예외 로그가 찍혔다.
```
... ERROR ... [askScheduler-10] c.w.b.b.l.CreateDeliveryMessageListener : 
    org.springframework.transaction.TransactionSystemException: 
    Could not commit JPA transaction; nested exception is 
    javax.persistence.RollbackException: Transaction marked as rollbackOnly
at o.s.orm.jpa.JpaTransactionManager.doCommit(JpaTransactionManager.java:526)
...
...
Caused by: javax.persistence.RollbackException: Transaction marked as rollbackOnly
```
해당 문제가 나오는 스케줄러쪽부터 차례로 코드를 보자.
```kotlin
@Scheduled(cron = "0 */15 * * * *")
    @SchedulerLock(name = "scheduledTaskName", lockAtLeastFor = "14m")
    @Transactional
    fun performTask() {
        try {
            log.info("외부 서버와 상태 동기화 스케줄러 시작")
            dataSync()
        } catch (e: Exception) {
            log.error("외부 서버와 상태 동기화 스케줄러 실패 : {}", e.message)
        }
    }
```
해당 스케줄러 메서드의 @Tx를 시작으로 내부의 `dataSync()` 메서드 내에도 @Tx가 걸려있다.
```kotlin
@Transactional
    fun dataSync() {
        // 중략...

        if (데이터 변경 가능) {
            data.updateBy(a,b)

            dataSync.toSync(a,b,c)
        }
    }
```
```kotlin
fun updateBy(a,b) {
    // 중략...
    throw RuntimeException("예외 던지기")
}
```
또한, `dataSync()` 내에서 호출하는 메서드에도 @Tx가 걸려있었는데, 그 안에서 `RuntimeException`이 발생했다.
## 🕊️ 나의 생각
무분별한 Try-Catch 절이 몇 있었는데, 이 부분에서 예외를 잡을 때 예외 처리가 잘못된 건 아닐까?
## 🪄 실제 원인
비록 try-catch에서 예외 처리를 했다 하더라도 `RuntimeException`이 트랜잭션 안에서 발생하면 무조건 Rollback으로 표시(mark)가 된다!!
## 💫 해결 방법
1. `Checked Exception`
- RuntimeException이 아닌 Exception을 상속받아 CustomException을 만들고 예외를 던져야 할 때 이 CustomException을 던지면 **롤백**이 되지 않는다.
```kotlin
class CustomException(msg: String?) : Exception(msg)

// 위의 data.update(a,b) 수정
@Throws(CustomException::class)
fun updateBy(a,b) {
    // 중략...
    throw RuntimeException("예외 던지기")
}
```
- 롤백 조건중 excpetion instanceof RuntimeException 조건이 있다. 이걸 반대로 말하면 ‘Checked Exception은 발생하더라도 롤백되지 않는다.’ 가 된다.
2. noRollbackFor 속성 사용
- @Tx 메소드에는 `noRollbackFor` 속성이 있다. 이 속성을 사용하면 예외가 발생해도 롤백하지 않을 예외 클래스를 지정할 수 있다. 단, 이 속성에 들어갈 클래스는 Throwable 의 서브클래스여야 한다.
`@Transactional(noRollbackFor = RuntimeException::class)`

---

### 참조
- https://keencho.github.io/posts/transaction-rollback/
- https://brunch.co.kr/@purpledev/8
- https://techblog.woowahan.com/2606/
- https://leezzangmin.tistory.com/25
