# JPQL 기본 문법과 소개

- JPQL은 객체지향 쿼리 언어다. 따라서 테이블을 대상으로 쿼리하는 것이 아니라 **엔티티 객체를 대상으로 쿼리**한다
- JPQL은 SQL을 추상화해서 특정 데이터베이스 SQL에 의존하지 않는다
- JPQL은 결국 SQL로 변환되어 실행된다

## JPQL 문법

- `select m from Member as m where m.age > 18`
- 엔티티와 속성은 대소문자를 구분한다 (Member, age)
- JPQL 키워드는 대소문자를 구분하지 않는다 (SELECT, FROM, WHERE)
- 엔티티의 이름을 사용하며, 테이블 이름이 아니다 (Member)
- **별칭은 필수이다 (m)** (`as`는 생략 가능하다)

## 1. 반환 타입 유무에 대한 문법

- `TypeQuery` : 반환 타입이 명확할 때 사용

```jpaql
TypedQuery<Member> query =
		em.createQuery("SELECT m FROM Member m", Member.class);
```

- `Query` : 반환 타입이 명확하지 않을 때 사용

```jpaql
Query query =
        em.createQuery("SELECT m.username, m.age from Member m");
```

## 2. 결과 조회 API

- `query.getResultList()`: **결과가 하나 이상일  때**, 리스트로 반환한다
    - 결과가 없으면 빈 리스트 반환
- `query.getSingleResult()`: **결과가 정확히 하나**, 단일 객체 반환
    - 결과가 없으면 **:** javax.persistence.NoResultException
    - 둘 이상이면: javax.persistence.NonUniqueResultException
    - but, 스프링 데이터 JPA에서는 null or optional로 반환해준다

## 3. 파라미터 바인딩 - 이름 기준, 위치 기준

```jpaql
//이름 기준
SELECT m FROM Member m where m.username=:**username** 
query.setParameter("**username**", usernameParam);
```

```jpaql
//위치 기준 -> 잘 쓰이지 않는다 
SELECT m FROM Member m where m.username=**?1** 
query.setParameter(**1**, usernameParam);
```

- *but 위치 기준은 잘 쓰이지 않는다, 이유는 1,2,3으로 이어나간다 했을 때 중간에 하나가 삽입이 된다면 순서가 다 밀려서 장애로 이어진다!*

## 4. 프로젝션

- 프로젝션 : SELECT 절에 조회할 대상을 지정하는 것 → 즉 뽑아내는 것!
- 프로젝션 대상 : **엔티티, 임베디드 타입**, 스칼라 타입(숫자, 문자등 기본 데이터 타입)
    - **엔티티 프로젝션은 영속성 컨텍스트가 관리를 해준다**
- `SELECT m FROM Member m` → 엔티티 프로젝션
    - m은 Member m의 별칭이기 때문에 Member 엔티티를 조회!
- `SELECT m.team FROM Member m` → 엔티티 프로젝션
    - [m.team](http://m.team) → member.tem 이라는 말! 멤버와 연관된 팀을 조회, 결과가 팀이다
- `SELECT m.address FROM Member m` → 임베디드 타입 프로젝션
    - Address는 값타입. 즉 임베디드 타입이므로 임베디드 프로젝션
- `SELECT m.username, m.age FROM Member m` -> 스칼라 타입 프로젝션
- DISTINCT로 중복제거 가능

### 4.1 프로젝션의 여러 값 조회

- `SELECT m.username, m.age FROM Member m`
1. Query 타입으로 조회
2. Object[] 타입으로 조회
3. new 명령어로 조회
    1. 단순 값을 DTO로 바로 조회
    2. 패키지 명을 포함한 전체 클래스 명 입력
    3. **순서와 타입이 일치**하는 생성자 필요
    
    ```jpaql
    SELECT **new** jpabook.jpql.UserDTO(m.username, m.age) 
    FROM Member m
    ```
    

## 5. 페이징 API

- JPA는 페이징을 다음 두 API로 추상화 한다
- `**setFirstResult**(int startPosition)` : 조회 시작 위치 (0부터 시작)
- `**setMaxResults**(int maxResult)` : 조회할 데이터 수
    
    ```jpaql
    // 페이징 쿼리
    String jpql = "select m from Member m order by m.name desc"; 
    List<Member> resultList = em.createQuery(jpql, Member.class)
    			.setFirstResult(10) 
    			.setMaxResults(20) 
    			.getResultList();
    ```
    

## 6. 조인 (Join)

### 1) 내부 조인

- 내부 조인은 외부 조인과 달리 연관관계가 있어야 조인이 가능하다
- JPQL에서는 `JOIN` 키워드를 사용하여 내부 조인을 표현할 수 있다

```jpaql
// 내부 조인
SELECT m FROM Member m (INNER)JOIN m.team t
```

- 위의 예제는 `Member` 엔티티와 `Team` 엔티티의 연관관계를 이용하여 내부 조인을 수행하는 JPQL 쿼리이다.
- `JOIN` 키워드 다음에는 조인 대상이 되는 엔티티의 별칭을 사용하여 해당 엔티티와 조인한다
    - 위의 예제에서는 `Member` 엔티티의 별칭인 `m`과 `Team` 엔티티의 별칭인 `t`를 사용하여 조인하였다
- `JOIN` 키워드 뒤에는 조인 대상이 되는 엔티티의 필드를 참조하는 코드가 따라온다
    - 위의 예제에서는 `m.team`을 사용하여 `Member` 엔티티와 `Team` 엔티티를 조인하였다.

### 2) 외부 조인

- 외부 조인은 JPQL에서 `LEFT JOIN`, `RIGHT JOIN` 키워드를 사용하여 표현할 수 있다
    - `LEFT JOIN` : 왼쪽에 위치한 엔티티를 기준으로 우측의 엔티티와 조인한다
    - `RIGHT JOIN` : 오른쪽에 위치한 엔티티를 기준으로 왼쪽의 엔티티와 조인한다

```jpaql
// 외부 조인
SELECT m FROM Member m LEFT(OUTER) JOIN m.team t
```

- 위의 예제는 `Member` 엔티티와 `Team` 엔티티의 연관관계를 이용하여 외부 조인을 수행하는 JPQL 쿼리이다
- `LEFT JOIN` 키워드 다음에는 조인 대상이 되는 엔티티의 별칭을 사용하여 해당 엔티티와 조인한다
    - 위의 예제에서는 `Member` 엔티티의 별칭인 `m`과 `Team` 엔티티의 별칭인 `t`를 사용하여 조인하였다
- `LEFT JOIN` 키워드 뒤에는 조인 대상이 되는 엔티티의 필드를 참조하는 코드가 따라온다
    - 위의 예제에서는 `m.team`을 사용하여 `Member` 엔티티와 `Team` 엔티티를 조인하였다
- 외부 조인을 사용할 때는 `ON` 절을 사용하여 조인 조건을 명시할 수도 있다

### 3) 세타 조인

- 세타 조인은 연관 관계가 없는 엔티티를 조인할 때 사용하는 방식이다
- `FROM` 절에 여러 엔티티를 나열하고, `WHERE` 절에서 연관 관계가 없는 필드를 이용하여 조인한다

```jpaql
SELECT m, t FROM Member m, Team t WHERE m.username = t.name
```

- 위의 예제는 `Member` 엔티티와 `Team` 엔티티의 연관관계가 없을 때, `Member` 엔티티의 `username` 필드와 `Team` 엔티티의 `name` 필드를 이용하여 세타 조인을 수행하는 JPQL 쿼리이다

### 4) ON 절

- ON절을 활용한 조인
    - 조인 대상 필터링
    - 연관관계 없는 엔티티 외부 조인
1. **조인 대상 필터링**
- ex) 회원과 팀을 조인 하면서, 팀 이름이 A인 팀만 조회

```jpaql
//JPQL
SELECT m,t FROM Member m LEFT JOIN m.team t ON t.name = 'A'
```

```jpaql
//실제 나가는 SQL
SELECT m.*, t.*
FROM Member m 
LEFT JOIN Team t ON m.TEAM_ID=t.id AND t.name='A'
```

1. **연관관계 없는 엔티티 외부 조인**
- ex) 회원의 이름과 팀의 이름이 같은 대상 외부 조인

```jpaql
//JPQL
SELECT m,t
FROM Member m LEFT JOIN Team t ON m.username = t.name
```

```jpaql
//실제 나가는 SQL
SELECT m.*, t.*
FROM Member m LEFT JOIN Team t ON m.username = t.name
```

## 7. 서브 쿼리

- 나이가 평균보다 많은 회원

```jpaql
select m from Member m
where m.age > **(select avg(m2.age) from Member m2)**
```

- 한 건이라도 주문한 고객

```sql
select m from Member m
where **(select count(o) from Order o where m = o.member)** > 0
```

### 서브 쿼리 지원 함수

- `[NOT] EXISTS` (subquery): 서브 쿼리에 결과가 존재하면 **참**
    - {ALL | ANY | SOME} (subquery)
        - ALL : 모두 만족하면 **참**
        - ANY,SOME : 같은 의미 조건을 하나라도 만족하면 **참**
- `[NOT] IN` (subquery) : 서브 쿼리의 결과 중 하나라도 같은 것이 있으면 참

```jpaql
//팀A 소속인 회원
select m from Member m
where **exists** (select t from m.team t where t.name = ‘팀A')

//전체 상품 각각의 재고보다 주문량이 많은 주문들
select o from Order o 
where o.orderAmount > **ALL** (select p.stockAmount from Product p)

//어떤 팀이든 팀에 소속된 회원 select m from Member m 
where m.team = **ANY** (select t from Team t)
```

### 서브 쿼리의 한계

- JPA는 WHERE,HAVING 절에서만 서브 쿼리 사용 가능
- SELECT 절도 가능(하이버네이트에서 지원)
- **FROM 절의 서브 쿼리는 현재 JPQL에서 불가능**
    - **조인으로 풀 수 있으면 풀어서 해결**

## JPQL 타입 표현

- 문자 : ‘HELLO’, ‘She”s’
- 숫자 : 10L(Long), 10D(Duble), 10F(Float)
- Boolean : TRUE,FALSE
- ENUM : ex) jpabook.MemberType.Admin (패키지명 포함)
- 엔티티 타입 : TYPE(m) = Member (상속 관계에서 사용)

## JPQL 기타

- SQL과 문법이 같은 식
- EXISTS, IN
- AND,OR,NOT
- =, >, >=, <, <=, <>
- BETWEEN, LIKE, **IS NULL**

## 조건식 - CASE 식

- 기본 CASE 식
    
    ```jpaql
    select
        case when m.age <= 10 then '학생요금' 
 			 when m.age >= 60 then '경로요금'
    	 else '일반요금'
        end
    from Member m
    ```
    
- 단순 CASE 식
    
    ```jpaql
    select
        case t.name 
    	when '팀A' then '인센티브110%' 
    	when '팀B' then '인센티브120%'
    	else '인센티브105%'
        end
    from Team t
    ```
    
- COALESCE : 하나씩 조회해서 null이 아니면 반환
    - 사용자 이름이 없으면 이름 없는 회원을 반환
    - `select coalesce(m.username,'이름 없는 회원') from Member m`
- NULLIF : 두 값이 같으면 null 반환, 다르면 첫번째 값 반환
    - 사용자 이름이 ‘관리자’면 null을 반환하고 나머지는 본인의 이름을 반환
    - `select NULLIF(m.username, '관리자') from Member m`