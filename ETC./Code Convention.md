## 👨‍💻 Code Convention
> 💡 **동료들과 말투를 통일하기 위해 컨벤션을 지정합니다.**
> 오합지졸의 코드가 아닌, **한 사람이 짠 것같은 코드**를 작성하는 것이 추후 유지보수나 협업에서 도움이 됩니다. 내가 코드를 생각하면서 짤 수 있도록 해주는 룰이라고 생각해도 좋습니다!

### Code
- 하나의 메서드(method) 길이 12줄, 깊이(depth) 3 이내로 작성합니다.

### Entity
- id 자동 생성 전략은 IDENTITY를 사용합니다.
- @NoArgsConstructor 사용 시 access를 PROTECTED로 제한합니다.

### Enum
- Enum 값은 import static 호출로 사용합니다.

### DTO
- Controller에서 요청/응답하는 DTO와 Service에서 사용하는 DTO를 분리합니다.
    - Layered Architecture를 엄격하게 준수합니다.
    - 확장/번경에 용이하게 합니다.
    - 매개변수가 5개 미만일 경우 controller-service간 Dto를 사용하지 않습니다. 
- 네이밍은 아래와 같이 정의합니다.
    - Controller DTO: `${Entity명}${복수형일 경우 List 추가}${행위 또는 상태}${Request/Response}`
    - Service DTO: `${Entity명}${복수형일 경우 List 추가}${행위 또는 상태}Service${Request/Response}`

### Service, Repository
- DB를 호출하는 경우 메서드명에 save, find, update, delete 용어를 사용합니다.
- 비즈니스 로직일 경우 메서드명에 create, get, update, delete, 그 외 용어를 사용합니다.
- 복수형은 ${Entity명}s로 표현합니다.
- Service 파일이 비즈니스 로직 5개 이상으로 커지면 조회, 비조회(Transactional)로 클래스를 분리합니다.

---

### 참조
> https://github.com/depromeet/layer-server?tab=readme-ov-file#-code-convention