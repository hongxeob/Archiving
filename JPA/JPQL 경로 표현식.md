## 경로 표현식

- .(점)을 찍어 객체 그래프를 탐색하는 것
    
    ```sql
    select m.username -> 상태 필드 
    from Member m
    join m.team t -> 단일 값 연관 필드
    join m.orders o -> 컬렉션 값 연관 필드 
    where t.name = '팀A'
    ```
    
- **상태 필드**(state field) : 단순히 값을 저장하기 위한 필드
    - ex) `m.username`
    - 경로 탐색의 끝, 탐색 불가능
- **연관 필드** (association field) ‘ 연관관계를 위한 필드
    - **단일 값 연관 필드** : @ManytoOne, @OnetoOne, 대상이 엔티티 ex) `m.team`
        - **묵시적 내부 조인(inner join) 발생, 더 탐색이 가능하다**
    - **컬렉션 값 연괄 필드** : @OneToMany, @ManyToMany, 대상이 컬렉션 ex) `m.orders`
        - 묵시적 내부조인 발생, 탐색 불가능하다
        - **FROM절에서 명시적 조인을 통해 별칭을 얻으면 별칭을 통해 탐색 가능**
- 실무에서는 묵시적 내부 조인이 발생하는 경우를 최대한 지양한다
    - *운영시에 수많은 쿼리가 운영되기 때문에 쿼리 튜닝하기가 어렵다*

### SQL과의 경로 탐색 코드 비교

- **상태 필드 경로 탐색**
    
    ```sql
    JPQL: select m.username, m.age from Member m
    SQL: select m.username, m.age from Member m
    ```
    
- **단일 값 연관 경로 탐색**
    
    ```sql
    JPQL: select o.member from Order o
    SQL: select m.* 
    	from Orders o 
    	inner join Member m on o.member_id = m.id
    ```
    

### 명시적 조인, 묵시적 조인

- **명시적 조인** : join 키워드 직접 사용 (별칭을 얻으면 별칭을 통해 탐색 가능)
    
    ```sql
    //컬렉션 값 연괄 필드
  //팀에 소속된 멤버들의 이름을 알고 싶을때
    
    //잘못된 예
    select t.members.username(안됨) From Team t
    //명시적 조인 사용 (별칭을 줘서 그 별칭을 이용해 새로 탐색)
    select m.username(가능) from Team t join t.members m (별칭 부여 후 셀렉트)
    ```
    
- **묵시적 조인** : 경로 표현식에 의해 묵시적으로 SQL 조인 발생(내부 조인만 가능)
    - **사용하지 말 것 권장!!**
    - ex) `select m.team from Member m`