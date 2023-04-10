# 상속관계 매핑
## 슈퍼타입 서브타입 논리 모델을 실제 물리 모델로 구현하는 방법
- 1.각각 테이블로 변환
  - 조인 전략
- 2.통합 테이블로 변환
  -  단일 테이블 전략
 - 3.서브타입 테이블로 변환
   - 구현 클래스마다 테이블 전략
### 주요 어노테이션
-  `@Inheritance(strategy=InheritanceType.XXX)`
   - **JOINED**: 조인 전략
    - **SINGLE_TABLE**: 단일 테이블 전략
    - **TABLE_PER_CLASS**: 구현 클래스마다 테이블 전략
-  `@DiscriminatorColumn(name=“DTYPE”)`
-  `@DiscriminatorValue(“XXX”)`
---
## 1.1 조인 전략
엔티티 각각을 테이블로 만들고 자식 테이블이 부모 테이블의 기본 키를 받아 기본 키 + 외래 키로 사용하는 전략<br>
자식 테이블 중 어느 테이블을 조회해야하는지 구분하기 위해 DTYPE이란 구분 컬럼을 사용한다
- `@Inheritance(strategy = InheritanceType.JOINED)`
  - 부모 클래스에 지정. 조인 전략이므로 InheritanceType.JOINED 설정
- `@DiscriminatorColumn(name = "DTYPE")`
  - 구분 컬럼 지정. Default값이 DTYPE이므로 name 속성은 생략 가능
- `@DiscriminatorValue("TEST")`
  - 구분 컬럼에 입력할 값 지정. Default값으로 엔티티 이름 사용
- `@PrimaryKeyJoinColumn(name = "XXX_ID")`
  - Default로 자식 테이블은 부토 테이블 id 컬럼명을 그대로 사용하나, 변경시 해당 설정값 추가
### 장점
- 테이블 정규화
- 외래 키 참조 무결성 제약조건 활용가능
- 저장공간 효율화
### 단점
- 조회시 조인을 많이 사용,성능 저하
- 조회 쿼리가 복잡함
- 데이터 저장시 INSERT SQL 2번 호출

## 1.2 단일 테이블 전략
하나의 테이블을 사용하며 구분 컬럼(DTYPE)을 활용해 데이터를 활용하는 전략
- `@Inheritance(strategy = InheritanceType.SINGLE_TABLE)`
  - 부모 클래스에 지정. 단일 테이블 전략이므로 InheritanceType.SINGLE_TABLE 설정
### 장점
- 조인이 필요 없으므로 일반적으로 조회 성능이 빠름
- 조회 쿼리가 단순함
### 단점
- 단일 테이블에 모든것을 저장하므로 테이블이 커질 수 있다.
- 상황에 따라서 조회 성능이 오히려 느려질 수 있다.
## 1.3 구현 클래스마다 테이블 전략
자식 엔티티마다 테이블 생성하는 전략. 추천하지 않는 전략
- `@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)`
  - 부모 클래스에 지정. 구현 클래스마다 테이블 전략이므로 InheritanceType.TABLE_PER_CLASS 설정
### 장점
- 서브 타입을 구분해서 처리할 때 효과적
- not null 제약조건 사용 가능
### 단점
- 여러 자식 테이블이 함께 조회시 성능 문제
- 자식 테이블을 통합해 쿼리가 어려움
## @MappedSuperclass?
- 공통 매핑 정보가 필요할 때 사용(id, name)
- 상속관계 매핑X, 엔티티X, 테이블과 매핑X
- 부모 클래스를 상속 받는 **자식 클래스에 매핑 정보만 제공**
- 조회, 검색 불가(**em.find(BaseEntity) 불가**)
- 직접 생성해서 사용할 일이 없으므로 **추상 클래스 권장**