# 🛠️ [리팩토링 1판](https://www.yes24.com/Product/Goods/7951038) 실습 레포지토리

# 리팩토링 방법

## 메서드 정리 (Composing Method)

### 1. Extract Method
> 그룹으로 함께 묶을 수 있는 코드 조각이 있으면 코드의 목적이 잘 드러나도록 메서드의 이름을 지어 별도의 메서드로 뽑아낸다.
``` java
void printOwing(double amount)
{
  printBanner();
  //상세 정보 표시
  System.out.println( "name:" + _name );
  System.out.println( "amount:" + amount );
}

===========================================

void printOwing(double amount)
{
  printBanner();
  printDetails(amount);
}
void printDetails(double amount)
{
  System.out.println( "name:" + _name );
  System.out.println( "amount:" + amount );
}
```

**🪄 동기 <br>**
1. 메서드가 잘 쪼개져 있을 때 다른 메서드에서 사용될 확률이 높아진다.
2. 고수준(high-level)의 메서드를 볼 때 일련의 주석을 읽는 것 같은 느낌을 들도록 할 수 있다.

<details>
<summary> ✅ 절차 </summary>
<div markdown="1">

- 메서드를 새로 만들고 의도를 잘 나타낼 수 있도록 이름을 정한다.
  - `어떻게 하는지`가 아닌 `무엇을 하는지`를 나타내게 이름을 정한다.
  - 뽑아내고자 하는 부분이 한 줄의 메시지나 함수 호출과 같이 아주 간단한 경우에는 새로운 메서드의 이름이 그 코드의 의도를 더 잘타나낼 수 있을 때만 뽑아낸다.
  - 더 이해하기 쉬운 이름을 지을 수 없다면 코드를 뽑아내지 않는 것이 낫다.
- 원래 메서드에서 뽑아내고자 하는 부분의 코드를 복사하여 새 메서드로 옮긴다.
- 원래 메서드에서 사용되고 있는 지역변수가 뽑아낸 코드에 있는지 확인한다. 이런 지역변수는 새로운 메서드의 지역변수나 파라미터가 된다.
- 뽑아낸 코드 내에서만 사용되는 임시변수가 있는지 본다. 있다면 새로운 메서드의 임시변수를 선언한다.
- 뽑아낸 코드 내에서 지역변수의 값이 수정되는지 본다. 하나의 지역변수만 수정 된다면, 뽑아낸 코드를 질의(query)로 보고 수정된 결과를 관련된 변수에 대입 할 수 있는지 본다.
  - 이렇게 하는것이 이상하거나 값이 수정되는 지역변수가 두개 이상 있다면 쉽게 메서드로 추출할 수 없는 경우이다.
  - 이럴때는 `Split Temporary Variable`을 사용한 다음 다시 시도해보자. 임시변수는 `Replace Temp with Query`로 제거할 수 있다.
- 뽑아낸 코드에서 읽기만 하는 변수는 새 메서드의 파라미터로 넘긴다
- 지역변수와 관련된 사항을 다룬 후에는 컴파일을 한다.
- 원래 메서드에서 뽑아낸 코드 부분은 새로 만든 메서드를 호출하도록 바꾼다.
  - 새로 만든 메서드로 옮긴 임시변수가 있는 경우 그 임시변수가 원래 메서드의 밖에서 선언되었는 지를 확인한다. 만약 그렇다면 새로 만들 메서드에서는 선언을 해줄 필요가 없다.
- 컴파일과 테스트를 한다.

</div>
</details>

---

### 2. Inline Method
> 메서드 몸체가 메서드의 이름 만큼이나 명확할 때는 호출하는 곳에 메서드의 몸체를 넣고, 메서드를 삭제하라
```java
// Before: 불필요하게 분리된 메서드
public class DeliveryRating {
    private int numberOfLateDeliveries;

    public int getRating() {
        return moreThanFiveLateDeliveries() ? 2 : 1;
    }

    private boolean moreThanFiveLateDeliveries() {
        return numberOfLateDeliveries > 5;
    }
}

// After: 간단한 조건을 인라인으로 통합
public class DeliveryRating {
    private int numberOfLateDeliveries;

    public int getRating() {
        return numberOfLateDeliveries > 5 ? 2 : 1;
    }
}
```
**🪄 동기**
1. 때로는 메서드의 몸체가 메서드의 이름 만큼이나 명확할 때가 있다.
2. 또는 메서드의 몸체를 메서드의 이름 만큼 명확하게 리펙토링 할 수도 있다.
3. `Inline Method`는 메서드가 잘못 나누어져 있을 때에도 사용할 수도 있다.
4. `Replace Method with Method Object`를 사용하기 전에 이 리팩토링을 사용한다면 좋다는 것을 알아냈다.
5. 모든 메서드가 단순히 다른 메서드에 위임을 하고 있어 그 인디렉션 속에서 길을 잃을 염려가 있을 때도 `Inline Method`를 사용한다.

<details>
<summary> ✅ 절차 </summary>
<div markdown="1">

- 메서드가 다형성을 가지고 있지 않은지 확인한다.
  - 서브 클래스에서 오버라이드 하고 있는 메서드에는 적용하지 않는다. 슈퍼 클래스에 없는 메서드를 서브 클래스에서 오버라이드 할 수 없다.
- 메서드를 호출하고 있는 부분을 모두 찾는다.
- 각각의 메서드 호출을 메서드 몸체로 바꾼다.
- 컴파일과 테스트를 한다.
- 메서드 정의를 제거한다.

</div>
</details>

**⚠️ 주의** 
- Inline Method는 간단하게 보인다.
- 재귀가 사용되는 경우나 리턴 포인트가 여러 곳인 경우에 대해 어떻게 하고, 접근자(accessor)가 없을 때는 어떻게 다른 객체로 인라인화 하는 지 등에 대해 여러 페이지에 걸쳐 설명할 수도 있다.
- 이런 설명을 하지 않는 이유는 이런 복잡한 경우에는 이 리펙토링을 하지 않는 것이 좋기 때문이다.

---

### 3. Inline Temp
> 간단한 수식의 결과값을 가지는 임시변수가 있고 그 임시변수가 다른 리팩토링을 하는데 방해가 된다면, 이 임시변수를 참조하는 부분을 원래의 수식으로 바꿔라

```java
// Before: 임시변수 사용
public boolean isEligibleForDiscount(Order order) {
    var basePrice = order.getBasePrice();
    return basePrice > 1000;
}

// After: 임시변수를 인라인으로 변경
public boolean isEligibleForDiscount(Order order) {
    return order.getBasePrice() > 1000;
}
```

**🪄 동기**

1. `Inline Temp`는 `Replace Temp with Query`의 한 부분으로 사용된다.
   1. 따라서 진짜 동기는 그쪽에 있다.
2. `Inline Temp`가 자신의 목적으로 사용되는 유일한 경우는 메서드 호출의 결과값이 임시변수에 대입되는 경우이다.
3. `Extract Method`와 같은 다른 리팩토링에 방해가 된다면, **인라인화** 하는 것이 좋다.

<details>
<summary> ✅ 절차 </summary>
<div markdown="1">

- 임시변수를 final로 선언한 다음 컴파일 한다.
  - 이것은 임시변수에 값이 단 한번만 대입되고 있는지를 확인하기 위한것이다.
- 임시변수를 참조하고 있는 곳을 모두 찾아 대입문(assignment)의 우변에 있는 수식으로 바꾼다.
- 각각의 변경에 대해 컴파일과 테스트한다.
- 임시변수의 선언과 대입문을 제거한다.
- 컴파일과 테스트한다.

</div>
</details>

---

### 4. Replace Temp with Query

> 어떤 수식의 결과값을 저장하기 위해서 **임시변수**를 사용하고 있다면 수식을 뽑아내서 메서드로 만들고, 임시변수를 참조하는 곳을 찾아 모두 **메서드 호출**로 바꾼다. <br>
> 새로 만든 메서드는 다른 메서드에서도 사용될 수 있다.

```java
// Before: 임시변수 사용
private int quantity;
private double itemPrice;

public double calculateDiscount() {
    var basePrice = quantity * itemPrice;
    
    if (basePrice > 1000) {
        return basePrice * 0.95;
    } else {
        return basePrice * 0.98;
    }
}

// After: 임시변수를 메서드로 추출
private int quantity;
private double itemPrice;

public double calculateDiscount() {
    if (getBasePrice() > 1000) {
        return getBasePrice() * 0.95;
    } else {
        return getBasePrice() * 0.98;
    }
}

private double getBasePrice() {
    return quantity * itemPrice;
}
```

**🪄 동기**

1. 임시변수는 임시로 사용되고 특정 부분에서만 의미를 가지므로 문제가 된다.
2. 임시변수는 그것이 사용되는 **메서드의 컨텍스트 안**에서만 볼 수 있으므로 임시변수가 사용되는 메서드는 보통 길이가 길어지는 경향이 있다.
3. **임시변수를 질의 메서드(query method)로 바꿈**으로써 클래스 내의 어떤 메서드도 임시변수에 사용될 정보를 얻을 수 있다.
4. `Replace Temp with Query`는 `Extract Method`를 적용하기 전의 필수 단계이다.
5. 지역변수는 메서드의 추출을 어렵게하기 때문에 가능한 많은 지역변수를 질의 메서드로 바꾸는 것이 좋다.
6. 이 리팩토링을 적용하는 데 있어 가장 간단한 경우는 임시변수에 값이 한번만 대입되고, 대입문(assignment)을 만드는 수식이 부작용을 초래하지 않는 경우이다.  
7. `Split Temporary Variable`이나 `Separate Query from Modifier`를 먼저 적용하는 것이 쉬울 것이다.
8. 만약 임시변수가 어떤 결과를 모으는 경우(루프를 돌면서 덧셈을 하는 경우와 같이) 질의 메서드 안으로 몇몇 로직을 복사할 필요가 있다.


<details>
<summary> ✅ 절차 </summary>
<div markdown="1">

- 임시변수가 값이 한 번만 대입되는지를 확인한다.
  - 임시변수에 값이 여러번 대입되는 경우에는 `Split Temporary Variable`을 먼저 적용한다.
- 임시변수를 final로 선언한다. 
- 컴파일한다.
  - 이렇게 하여 임시변수에 값이 한번만 대입되는지 확인한다.
- 대입문의 우변을 메서드로 추출한다. 
  - 처음에는 메서드를 `private`로 선언한다. 나중에 다른 곳에서도 사용하는 것이 좋을 것 같으면 그 때 쉽게 접근 권한을 바꿀 수 있다. 
  - 추출된 메서드에 부작용이 없는지(어느 객체의 속성을 바꾸거나 하면 안된다.)확인한다. 만약 부작용이 있는 경우에는 `Separate Query from Modifier`를 사용한다.
- 컴파일과 테스트를 한다.
- `Inline Temp`를 적용한다.

</div>
</details>
