# 🦄`findByXXX()` VS `findAll() + stream(filter)`

`findAll()`로 DB에서 데이터를 모두 조회한 후 `stream(filter)`로 조회 하는 것과 조건에 맞는 데이터를 DB에서 한번에 가져오는 `findByXXX()`중 어떤 것이 성능에 이점이 있을까?

| 조회 방식 | 100건 | 1,000건 | 10,000건 | 100,000건 |
|-----------|--------|--------|---------|----------|
| findAll() | 8ms | 9ms    | 37ms    | 237ms    |
| findAllByNameIn() | 6.4ms | 5.45ms | 17ms    | 44ms     |
| findAll() + stream filter (by names) | 9ms | 7ms    | 49ms    | 256ms    |
| findAllByEmail() | 8ms | 10ms   | 38ms    | 260ms    |
| findAll() + stream filter (by email) | 9ms | 11ms   | 41ms    | 384ms    |

### 큰 차이가 나는 `findAllByNameIn()` / `findAll()` + stream Filter
1. 데이터 처리 위치의 차이
- `findAllByNameIn()`: DB에서 WHERE name IN ('name1', 'name2', 'name3',...) 조건으로 필터링
- `findAll()` + stream filter: 10만건의 데이터를 모두 애플리케이션으로 가져온 후 필터링

2. 메모리와 네트워크 부하
- `findAllByNameIn()`
    - n개의 이름에 해당하는 데이터만 네트워크로 전송
    - DB 인덱스를 활용하여 빠른 검색 가능 
    - 최종 결과만 메모리에 로드 (데이터 크기 ↓)
- `findAll()` + stream filter
  - 10만건 전체를 네트워크로 전송 (네트워크 부하 ↑)
  - 10만건 전체를 메모리에 로드 (메모리 사용량 ↑)
  - 10만건에 대해 각각 3번의 이름 비교 연산 수행
3. 실제 연산 횟수 비교
- `findAllByNameIn()`:
  - DB에서 인덱스를 통해 3개 이름에 해당하는 레코드만 빠르게 검색
  - 전체 연산: O(log n) * 3 (인덱스 검색)
- `findAll()` + stream filter:
  - 전체 100,000건 조회
  - 각 레코드마다 3개 이름과 비교
  - 전체 연산: O(n) * 3 (100,000 * 3 = 300,000번의 비교 연산)
4. DB 최적화의 이점
- DB는 이러한 IN 절 쿼리에 대해 최적화가 잘 되어있음
- 특히 name 컬럼에 인덱스가 있다면 더욱 효율적
- 병렬 처리나 쿼리 최적화 등 DB 엔진의 장점을 활용
