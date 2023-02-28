## 페치 조인(fetch join)

- SQL 조인 종류가 아니다
- JPQL에서 **성능 최적화**를 위해 제공하는 기능
- 연관된 엔티티나 컬렉션을 **SQL 한 번에 함께 조회**하는 기능
- `join fetch` 명령어 사용

## 엔티티 페치 조인

- 회원을 조회하면서 연관된 팀도 함께 조회하고 싶다!
- SQL을 보면 회원 뿐만 아니라 팀(T.*)도 함께 SELECT
    
    ```sql
    //JPQL
    select m from Member m join fetch m.team
    //SQL
    SELECT M.*, T.* FROM MEMBER M
    INNER JOIN TEAM T ON M.TEAM_ID=T.ID
    ```
    

## 컬렉션 페치 조인

- 일대다 관계, 컬렉션 페치 조인
    
    ```sql
    //JPQL
    select distinct t
    from Team t join fetch t.members 
    where t.name = ‘팀A'
    
    //SQL
    SELECT T.*, M.*
    FROM TEAM T
    INNER JOIN MEMBER M ON T.ID=M.TEAM_ID 
    WHERE T.NAME = '팀A'
    ```
    

### 페치조인과 DISTINCT

> 1:N 페치 조인에서는 데이터 뻥튀기가 생긴다
> 
> - 반대로 N:1 에서는 생기지 않는다
> - 그래서 중복을 제거해주는 DISTINCT를 사용한다
- SQL의 DISTINCT는 중복된 결과를 제거하는 명령
- JPQL의 DISTINCT 2가지 기능 제공한다
    1. SQL에 DISTINCT를 추가
    2. 애플리케이션에서 엔티티 중복 제거
- DISTINCT가 추가로 애플레킹션에서 중복 제거 시도
- 같은 식별자를 가진 **Team 엔티티 제거**

## 페치 조인과 일반 조인의 차이

### **일반 조인**

```sql
//일반 조인 실행 예시
//JPQL
select t
from Team t join t.members m where t.name = ‘팀A'

//SQL
SELECT T.*
FROM TEAM T
INNER JOIN MEMBER M ON T.ID=M.TEAM_ID  WHERE T.NAME = '팀A'
```

- **실행시 연관된 엔티티를 함께 조회하지 않는다**
- JPQL은 결과를 반환할 때 연관관계를 고려하지 않는다
- 단지 SELECT절에 지정한 엔티티만 조회할 뿐이다
- 그래서 그냥 딱 SELECT 절에 있는것만 가져온다
- **여기서는 팀 엔티티만 조회하고, 회원 엔티티는 조회하지 않는다**

### **페치 조인**

```sql
//페치 조인 실행 예시
//JPQL
select t 
from Team t join fetch t.members where t.name = ‘팀A'

//SQL
SELECT T.*, M.*
FROM TEAM T
INNER JOIN MEMBER M ON T.ID=M.TEAM_ID  WHERE T.NAME = '팀A'
```

- 페치 조인을 사용할 때만 연관된 엔티티도 함께 조회(즉시 로딩 같은 개념)
- 페치 조인은 객체 그래프를 SQL 한번에 조회하는 개념이다 → 한방 쿼리!!

## 페치 조인의 한계

- **페치 조인 대상에는 별칭을 줄 수 없다**
    - 하이버네이트는 가능, 가급적 사용X!!
    - ex) 팀을 조회하는데 멤버 5명중 3명만 하려면..? 위험한 행위이다
- 둘 이상의 컬렉션은 페치 조인 할 수 없다
- 컬렉션을 페치 조인하면 페이징 API(`setFirstREsult`,`setMaxResults`)를 사용할 수 없다
    - 1:1, N:1 같은 단일 값 연관 필드들은 페치 조인해도 페이징 가능
    - 하이버네이트는 경고 로그를 남기고 메모리에서 페이징(매우 위험)

## 페치 조인의 실무적인 사용법

- 연관된 엔티티들을 SQL 한 번으로 조회한다 → 성능 최적화
- 엔티티에직접 적용하는 글로벌 로딩 전략보다 우선한다
    - @OneToMany(fetch = FetchType.Lazy) = 글로벌 로딩 전략
- 실무에서 글로벌 로딩 전략은 모두 지연 로딩
- 최적화가 필요한 곳은 페치 조인 적용

## 페치 조인.fin

- 모든 것을 페치 조인으로 해결할 수는 없다
- 페치 조인은 객체 그래프를 유지할 때 사용하면 효과적이다
- 여러 테이블을 조인해서 엔티티가 가진 모양이 아닌 전혀 다른 결과를 내야하면, 페치 조인 보다는 일반 조인을 사용하고 필요한 데이터들만 조회해서 DTO로 반환하는 것이 효과적이다

## 벌크 연산

- 쿼리 한 번으로 여러 테이블 로우 변경(엔티티)
- `executeUpdate()`의 결과는 영향받은 엔티티의 수를 반환한다
- UPDATE,DELECT를 지원한다
- ex) 재고가 10개 미만인 모든 상품의 가격을 10% 상승하려면?
    - JPA 더티 체킹 기능으로 실행하려면 너무 많은 SQL 실행
    1. 재고가 10개 미만인 상품을 리스트로 조회한다
    2. 상품 엔티티의 가격을 10% 증가한다
    3. 트랜잭션 커밋 시점에 더티체킹이 동작한다
    - 변경된 데이터가 100건이라면 100번의 UPDATE SQL이 실행된다

### 벌크 연산 주의점

- 벌크 연산은 영속성 컨텍스트를 무시하고 DB에 직접 쿼리한다
- 해결 방법
    1. (영속성 컨텍스트가 채워지기 전) 벌크 연산을 먼저 실행한다
    2. 만약 영속성 컨텍스트에 데이터가 있다면, 벌크 연산 수행 후 영속성 컨텍스트 초기화를 해준다