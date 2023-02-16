> [Entity에서 Setter를 지양하고 Builder 패턴 적용기 (with.DTO)](https://hongseob.tistory.com/50)
### 혼자서 만들어보던 프로젝트의 대략적인 기능의 추가를 완료 하였다  

1차적인 구현은 끝낸 상태에서 전체적인 코드들을 한 번 쭈욱 보니 내가 짜고도 의미를 알 수 없는 코드들이 많았다  
그래서 다음 step으로는 몇가지 내가 해볼 수 있는 **리팩토링**을 진행하고자 했다

### \[리팩토링 첫 단계\] Entity의 Setter -> 빌더 패턴으로 바꿔보자

나는 Entity. 즉 도메인단에 Setter를 최대한 지양하라는 점들을 익히 들어 왔었다  
Setter라 하면 lombok에서 제공해주는 @Setter, @Data 등을 사용하는 것을 말할 것이다  
그리하여 어느곳에서도 set필드명() 을 통해 해당 도메인의 필드들을 호출하여 추가하고,수정할 수 있는 간편함이 있다

> 어떻게 보면 큰 신경쓰지 않고 편할 수 있는 장점이 있는데  
> 왜 Setter를 지양하라고 하는 것일까?

## Setter를 사용하면 안 되는 이유

-   Setter 메서드를 사용하면 값을 변경한 의도를 파악하기 힘들다 ~(난 이 문제 때문에 리팩토링을 마음 먹었다)~
    -   값의 추가인지, 수정인지 등에 대한 의도를 파악하기가 힘들다
-   객체의 일관성을 유지하기 어렵다
    -   자바 빈 규약을 따르는 Setter는 public으로 언제든지 변경할 수 있는 상태가 된다
    -   만약 회원의 정보를 변경하는 메서드를 만들었다해도, 다른 코드를 짜다가 어쩔 수 없이 회원의 정보를 변경해야 할때가 되면,  
        무작정 setter로 값을 바꿀 것이다. 그러면 정보를 변경하는 메서드는 의미가 없다. 즉 코드의 기능이 꼬이게 된다

### 그럼 Setter를 대체할 수 있는 방법은?

1.  생성자를 오버로딩 한다
2.  Builder 패턴을 사용한다
3.  정적 팩토리 메소드를 사용한다

나는 위의 방법들 중 **Builder 패턴**을 중점으로 사용해서 리팩토링을 진행하였다  
우선 기존의 코드들이다

```
// Board.java 리팩토링 전

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity // User 클래스가 MySQL에 자동으로 테이블 생성
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;


    @Column(nullable = false, length = 100)
    private String title;


    @Lob 
    private String content; 
   
    private int views;

    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime localDateTime;

    @PrePersist
    public void createdAt() {
        this.localDateTime = LocalDateTime.now();
    }

    @ManyToOne(fetch = FetchType.EAGER) 
    @JoinColumn(name = "userId")
    private User user;

    @OneToMany(mappedBy = "board",
            fetch = FetchType.EAGER, 
            cascade = CascadeType.REMOVE)
    private List<Reply> replyList;
```

보이는 코드와 같이, lombok의 @Data 어노테이션을 사용하여 getter,setter를 사용할 수 있게 구현하였다

```
// BoardService.java 리팩토링 전

@Service
@RequiredArgsConstructor
@Slf4j
public class BoardService {
    private final BoardRepository boardRepository;

    @Transactional
    public void write(Board board, User user) {
        board.setViews(0);
        board.setUser(user);
        boardRepository.save(board);
    }
    
    @Transactional
    public void update(Long id, Board requestBoard) {
        Board board = boardRepository.findById(id).orElseThrow(() -> {
            return new IllegalArgumentException("글을 찾을 수 없습니다");
        }); 
        board.setTitle(requestBoard.getTitle());
        board.setContent(requestBoard.getContent());
     
    }
}
```

BoardService. 즉 서비스단의 코드들이다  
글의 작성,수정 메서드에서 setter를 사용한 점을 볼 수 있다  
특히 수정 부분 메서드의 코드에서는, 다른 사람들이 보면 set이 어떠한 이유로 추가의 set인지, 수정의 set인지 명확하게 알 수 없다

---

또한, 나는 **DTO(Data Transfer Object)** 를 사용하지 않고 구현한 점을 볼 수 있다  
이는 Entity를 바로 호출하여 값을 변경해 주고 있는데, 굉장히 위험한 행위(?)라고 볼 수 있다  
이러한 문제점들을 가지고 리팩토링을 진행했다  
  
이제 **Builder 패턴**을 사용하여 리팩토링한 코드를 보자면  
우선 Entity로 데이터를 주고 받는게 아닌, 계층 간의 데이터 교환만을 하기 위해 BoardDto를 만들어 주었다

```
// BoardDto.java

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class BoardDto {
    private Long id;
    @NonNull
    private String title;
    private String content;
    private int views;
    private User user;

    public Board toEntity() {
        return Board.builder()
                .id(id)
                .title(title)
                .content(content)
                .views(views)
                .user(user)
                .build();
    }
```

순수Entity가 아닌 BoardDto로 다른 곳에서 데이터를 주고 받을 것이다  
또한 파라미터의 갯수가 많다고 느껴 Builder 패턴을 이용하여 Dto <-> Entity를 변환해주는 `toEntity()` 메서드를 만들었다

```
// Board.java 리팩토링 후

//@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@AllArgsConstructor
@Builder
@Entity 
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Lob 
    private String content;

    private int views;

    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime localDateTime;

    @PrePersist
    public void createdAt() {
        this.localDateTime = LocalDateTime.now();
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userId")
    private User user;

    @OneToMany(mappedBy = "board",
            fetch = FetchType.EAGER, 
            cascade = CascadeType.REMOVE)
    private List<Reply> replyList;

    public void updateBoard(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
```

Board 도메인에서 @Data 어노테이션을 제거하여 Setter를 사용하지 못하게 해주었다  
그리고 코드 마지막에 updateBoard()라는 메서드가 보일 것이다  
이 메서드를 만들어 준 이유는 BoardService에서 게시물 수정을 할때 사용할 메서드이다  
게시물 수정에 **필요한 필드**만 받고 더욱 **직관적인 이름**으로 알아챌 수 있는 메서드를 추가해 주었다  
  
사용하는 코드로 자세히 보자

```
// BoardSerivce.java 리팩토링 후

@Service
@RequiredArgsConstructor
@Slf4j
public class BoardService {
    private final BoardRepository boardRepository;

    @Transactional
    public void write(BoardDto boardDto, User user) {
        boardDto.setUser(user);
        Board saveBoard = boardDto.toEntity();
        boardRepository.save(saveBoard);
    }
    
    @Transactional
    public void update(Long id, BoardDto requestBoardDto) {
        Board board = boardRepository.findById(id).orElseThrow(() -> {
            return new IllegalArgumentException("글을 찾을 수 없습니다");
        }); 
        board.updateBoard(requestBoardDto.getTitle(), requestBoardDto.getContent());
    }
}
```

**첫번째**로 각 메서드에서 데이터를 주고 받을 때 순수 Board Entity가 아닌 BoardDto를 사용하였다  
이제 Board의 데이터를 무분별하게 여기저기서 주고 받는게 아닌, BoardDto 라는 계층간 데이터 교환을 위한 객체(DTO)를 사용하여 데이터를 주고 받는다  
  
**두번째**로는 Dto에서 사용한 Builder 패턴을 적용시켰다  
비록 write()메서드, 즉 글을 작성할 때 쓰는 메서드 내부에 사용하였는데, 신규 게시글 작성시에는 모든 필드가 결국 다 필요하기 때문에 toEntity()를 통해 필요한 것만 불러오는 것이 아닌 모든 필드들을 불러온거라 썩 효율적인지 모르겠다  
전체 필드가 필요한 경우가 아닌 필요한 필드만 받아오는 경우가 있다면 한 번 더 사용해 보아야겠다  
  
**세번째**로는 리팩토링한 Board 도메인에서 봤던 updateBoard() 메서드를 서비스단의 update() 메서드내에 사용했다  
해당 게시물의 id를 통해 BoardRepository에서 찾아온 뒤, updateBoard()를 사용하여 수정을 해주었다  
이전에는 set필드명()을 사용하여 해당 로직이 게시물 추가인지, 수정인지 알 수 없었다  
하지만 updateBoard()라는 직관적인 이름을 가진 메서드를 호출해 외부에서 보아도 의미를 알 수 있는 코드가 되었다

---

이렇게 간단한 리팩토링을 하면서도 고민을 많이 했고 시간도 많이 걸렸다

#### 특히 어려운 점과 아직 의문인 점들이 많다..

**1\. Board 도메인에** **updateBoard()****라는 필요한 필드만 사용하는 메서드를 만들었는데 이것도 결국 setter와 관련된 것이지 않을까?**  
  
**2\. BoardService에서 필요에 의해서이지만,** **update()** **메서드에서 요청의 데이터 DTO를 통해 순수 Entity로 바뀐 데이터를 바꿔주었는데 이것이 DTO를 잘 이용한 것일까?**  
\- (요청의 데이터를 순수 Entity가 아닌 DTO로 받고, 그것을 순수 Entity로 넣어줬는데, 이게 write() 메서드에서 썼던 toEntity()의 기능과 같은 역할이라고 보면 될까?)  
  
3\. 나는 게시글을 적으면 해당 게시물의 작성자의 id도 함께 작성(FK)이 되게 DB를 구현하였다  
또 나는 Dto의 toEntity() 메서드에 User객체도 함께 필드로 넣어줬기에, 서비스단의 write() 메서드에서 toEntity로 호출하면 지정한 필드가 모두 같이 들어갈 줄 알았다  
하지만 앞서 말한 설계대로 적용되지 않고, user가 null인채로 DB에 들어가게 되었다

```
   // 처음 작성한 로직 => DB에 user가 같이 저장되지 않았다
   @Transactional
    public void write(BoardDto boardDto, User user) {
        Board saveBoard = boardDto.toEntity();
        boardRepository.save(saveBoard);
    }
    
    // 해결한 로직 => DB에 user가 같이 저장이 되었다
    @Transactional
    public void write(BoardDto boardDto, User user) {
        boardDto.setUser(user);
        Board saveBoard = boardDto.toEntity();
        boardRepository.save(saveBoard);
    }
```

toEntity()로직에 User 필드도 있으니 당연히 들어갈 줄 알고 삽질을 많이 했다......  
위의 두번째 로직대로 boardDto.setUser(user) 로 user객체를 따로 넣어주고, 나머지 필드를 toEntity()로 변환시켜주니 user 객체도 함께 DB에 저장이 되었다

```
@RequiredArgsConstructor
@RestController
public class BoardApiController {
    private final BoardService boardService;

    @PostMapping("/api/board")
    public ResponseDto<Integer> write(@RequestBody BoardDto boardDto, @AuthenticationPrincipal PrincipalDetail principalDetail) {
        boardService.write(boardDto, principalDetail.getUser());
        return new ResponseDto<Integer>(HttpStatus.OK.value(), 1);
    }
```

> 내가 유추해본 이유는  
> ApiController에서 받은 boardDto에는 user값이 없을 것이고 (ajax 함수에서 title,content 값만 넘겨주었다)  
> write(boardDto, user)는 로그인한 user의 값을 넣어주기 위함이니  
> toEntity() 함수를 toEntity(user) 로 하면 될 것이다! 라고 유추했다

---

백지 상태에서 개발을 공부한 지 이제 막 4개월 정도 된 상태에서 처음 리팩토링을 해보았다  
엄청 간단한 리팩토링을 3~4시간 걸려 고민하고 수정하고 오류를 맛보면서 진행했다  
오래 걸린 만큼 고민도 많이 했고, 특히 **\`왜?\`** 라는 고민을 많이 한 것 같다  
정리하며 글을 쓰고, 코드를 한 번 더 보니 Builder 패턴을 제대로 쓴 로직이 없는 거 같기도 하고 setter를 없앤 것만 보이는데 효율적인지도 확신이 안 서 잘 모르겠다..  
지금 와서 보니 잘한 리팩토링 인지도 모르겠다 ㅋㅋㅋ  
  
아직도 내가 짠 코드를 보면서 스스로도 이해가 안되는 부분이 많아 더욱 발전 해야겠다고 느꼈다  
그래도 나름 혼자서 고뇌하고 씨름한 것이 좋은 경험이 되었고, 스스로도 더욱 발전할 수 있겠구나라고 느낀 계기가 되었다!!  
이번주에는 또 다른 도메인 User 도메인을 리팩토링 해보려 한다!!  
또한 여기엔 보여지지 않았지만 api컨트롤러에서는 Entity가 노출된 부분이 많다  
이 부분도 공부를 해보며 적용시켜 리팩토링해 보아야겠다
