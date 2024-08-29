## 🤔 문제 상황
엔티티의 값을 변경할 때 필드를 변경 후, JPA의 더티 체킹을 사용하지 않고 save 메서드를 한 번 더 호출하여 엔티티의 값을 바꾸는 로직을 목격했다.
## 🕊️ 나의 생각
단순 기술 이해도의 부족일까? 라는 생각을 가졌다.
## 💫 해결 방안
Spring Data JPA를 사용하면 하이버네이트 구현체를 사용하게 되는데 그래서 더티 체킹도 사용하게 된다.
나는 단순하게 프로젝트에서 Spring Data JPA만 사용하기에 의존성에 관하여 생각해보지 못했다.
하지만 더티 체킹을 사용하는 것도 결국 Spring Data JPA에 **의존적인 코드**가 된다는 점이다.
만약 JDBC나 다른 ORM 기술을 사용하면? 결국 다 걷어내야한다. 이말은 SOLID 원칙중 `OCP`를 위반한다.
그래서 도메인 로직등을 이용하여 엔티티 update를 해준뒤, 명시적으로 `save()`를 호출하는 방법을 쓴다. 그러면 다른 repository 구현체로 교체해도 호환성에 문제가 없을 것이다!

참고로 JPA [hypersistence-utils-hibernate](https://github.com/vladmihalcea/hypersistence-utils)의 HibernateRepository를 사용한다면 `update()` 메서드를 사용할 수 있다!

---

### 참조
- https://devs0n.tistory.com/113
