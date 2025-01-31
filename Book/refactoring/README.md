# 🛠️ [리팩토링 1판](https://www.yes24.com/Product/Goods/7951038) 실습 레포지토리

# 리팩토링 방법

## 1) 메서드 정리 (Composing Method)

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

### 5. Introduce Explaining Variable
> 복잡한 수식이 있는 경우에는 **수식의 결과**나 일부에 **자신의 목적을 달성하는 이름으로 된 임시변수**를 사용하라.
```java
public class BrowserCompatibilityChecker {
    private final String platform;
    private final String browser;
    private boolean initialized;
    private int resizeCount;

    // Before: 복잡한 조건문
    public boolean isCompatible() {
        if ((platform.toUpperCase().contains("MAC")) &&
            (browser.toUpperCase().contains("IE")) &&
            (isInitialized() && resizeCount > 0)) {
            // 작업...
            return true;
        }
        return false;
    }

    // After: 설명 변수 도입
    public boolean isCompatible() {
        final boolean isMacOS = platform.toUpperCase().contains("MAC");
        final boolean isIEBrowser = browser.toUpperCase().contains("IE");
        final boolean wasResized = resizeCount > 0;

        if (isMacOS && isIEBrowser && isInitialized() && wasResized) {
            // 작업...
            return true;
        }
        return false;
    }
}
```
**🪄 동기**

1. 수식은 매우 복잡해져 알아보기가 어려워질 수 있다.
2. 이런 경우 임시변수가 수식을 좀 더 다루기 쉽게 나누는데 도움이 될 수 있다.
3. `Introduce Explaining Variable`은 특히 조건문에서 각각의 조건의 뜻을 잘 설명하는 이름의 변수로 만들어 사용할 때 유용하다.
4. 다른 경우로 긴 알고리즘에서 각 단계의 계산 결과를 잘 지어진 임시변수로 설명할 수 있다.
5. `Introduce Explaining Variable`은 매우 일반적인 리팩토링이지만 이것보다 `Extract Method`를 더 자주 사용한다.
   1. 임시변수는 한 메서드의 컨텍스트 내에서만 유용하다!!
   2. 그러나 메서드는 객체의 모든 부분에서 뿐만 아니라 다른 객체에서도 유용하다.
   3. 하지만 때로는 지역변수 때문에 `Extract Method`를 사용하기 어려운 경우도 있는데, 이때 `Introduce Explaining Variable`를 사용할 때이다!

<details>
<summary> ✅ 절차 </summary>
<div markdown="1">

- final 변수를 선언하고 복잡한 수식의 일부를 이 변수에 대입한다.
- 원래 복잡한 수식에서 임시변수에 대입한 임시변수로 바꾼다.
  - 만약 이 부분이 반복된다면 반복되는 부분을 하나씩 바꿀 수 있다.
- 컴파일 & 테스트를 한다.
- 수식의 다른 부분에 대해서도 위의 작업을 반복한다.

</div>
</details>

`Introduce Explaining Variable`는 언제 사용되는가? => `Extract Method`를 사용하기가 더 어려울 때이다.
- 만약 수많은 지역변수를 사용하는 알고리즘을 개발하고 있다면 `Extract Method`를 쉽게 사용할 수는 없을 것이다.
- 코드를 이해하기 위해 `Introduce Explaining Variable`를 사용한다.
- 꼬였던 로직이 좀 풀리면 나중에 `Replace Temp with Query`를 적용한다.
- 만약 `Replace Method with Method Object`를 사용한다면 임시변수 또한 유용하다.

### 6) Split Temporary Variable

> 루프안에 있는 변수나 `collecting temporary variable`도 아닌 임시 변수에 값을 여러번 대입하는 경우에는 각각의 대입에 대해서 따로 따로 임시변수를 만들어라.

```java
public class Rectangle {
    private final double height;
    private final double width;

    public Rectangle(double height, double width) {
        this.height = height;
        this.width = width;
    }

    public void printDimensions() {
        // Before: 하나의 임시변수를 여러 목적으로 사용
        double temp = 2 * (height + width);
        System.out.println("Perimeter: " + temp);
        temp = height * width;
        System.out.println("Area: " + temp);

        // After: 각 계산 목적에 맞는 별도의 변수 사용
        final double perimeter = 2 * (height + width);
        System.out.println("Perimeter: " + perimeter);
        
        final double area = height * width;
        System.out.println("Area: " + area);
    }
}
```

**🪄 동기**
1. 임시변수는 여러 곳에서 다양하게 쓰일 수 있다.
2. 어떤 경우에는 임시변수에 여러번 값을 대입하게 된다. 루프에 사용되는 변수는 한 번 돌때마다 값이 바귄다.
3. `collecting temporary variable`은 메서드를 실행하는 동안 모이는 어떤 값을 모으는 변수다.
4. 다른 많은 임시변수는 주로 긴 코드에서 계산한 결과값을 나중에 쉽게 참조하기 위해서 보관하는 용도로 사용된다.
    - **이런 종류의 변수는 값이 한 번만 설정되어야 한다.**
5. 만약 여러번 설정된다면 그 변수는 메서드 안에서 여러가지 용도로 사용되고 있다는 뜻이다.
6. 어떤 변수든 여러가지 용도로 사용되는 경우에는 각각의 용도에 대해 따로 변수를 사용하도록 바꾸어야 한다.
7. 하나의 임시변수를 두가지 용도로 사용하면 코드를 보는 사람은 매우 혼란스러울 수 있다.

<details>
<summary> ✅ 절차 </summary>
<div markdown="1">

- 임시변수가 처음 **선언**된 곳과 임시 변수에 값이 처음 **대입**된 곳에서 변수의 이름을 바꾼다.
  - 만약 임시변수에 값을 대입할 때 `i = i + (수식)` 과 같은 형태라면, 이것은 이 변수가 `collecting temporary variable`이라는 뜻으로 분리하면 안 된다.
  - `collecting temporary variable`에 대한 연산은 보통 더하기, 문자열 연결(string concatenation), 스트림 쓰기, 컬렉션 요소(element)를 추가하기 등이다.
- 새로 만든 임시변수를 final로 선언한다.
- 임시변수에 두번째로 대입하는 곳의 직전까지 원래 임시변수를 참조하는 곳을 모두 바꾼다.
- 임시변수에 두번쨰로 대입하는 곳에서 변수를 선언한다.
- 컴파일 & 테스트를 한다.
- 각 단계(임시변수가 선언되는 곳에서부터 시작하여)를 반복한다. 그리고 임시변수에 다음으로 대입하는 곳까지 참조를 바꾼다.
</div>
</details>

### 7. Remove Assignments to Parameters
> 파라미터에 값을 대입하는 코드가 있으면 대신 임시변수를 사용하도록 하라

```java
public class PriceCalculator {
    // Before: 파라미터 직접 수정
    public int calculateDiscount(int price, int quantity, int yearToDate) {
        if (price > 50) {
            price -= 2;  // 파라미터를 직접 수정 - 안티패턴
        }
        return price;
    }

    // After: 임시 변수 사용
    public int calculateDiscount(final int price, final int quantity, final int yearToDate) {
        var discountedPrice = price;
        
        if (price > 50) {
            discountedPrice -= 2;
        }
        
        return discountedPrice;
    }
}
```

**🪄 동기**
1. 먼저 파라미터에 값을 대입한다는 말의 뜻을 명확히 하자.
2. 만약 파라미터로 객체를 넘긴 다음 파라미터에 다른 값을 대입하는 것은 파라미터가 다른 객체를 참조하게 하는 것을 뜻한다.
3. 파라미터로 넘겨진 객체로 어떤 작업을 하는 것은 아무런 문제가 없다.
4. **파라미터가 완전히 다른 객체를 참조하도록 하는 것에 반대한다.**
```java
    void aMethod(Object foo) {
      foo.modifiInSomeWay();  // 아무런 문제 없음
      foo = anotherObject;    // 문제가 됨
    }
```

5. 명확하지 않고 값에 의한 전달(pass by value)과 참조에 의한 전달(pass by reference)을 혼동하게 하기 때문이다.
6. 자바에서는 값에 의한 전달만 사용되고 여기서의 논의는 이것을 바탕으로 한다.
7. 값에 의한 전달에서는 파라미터의 어떤 변경을 가하더라도 호출하는 루틴 쪽에서는 반영되지 않는다.
8. 참조에 의한 전달을 사용하던 사람들에게는 아마도 이것이 헷갈릴 것이다.
9. 또한 메서드 몸체 안의 코드 자체에서도 혼돈이 된다. 따라서 파라미터는 전달된 그대로 쓰는 것이 일관적인 사용법이고 훨씬 명확하다.
10. 자바에서는 파라미터에 값을 대입해서는 안되고, 이런 코드를 보면 `Remove Assignments to Parameters`를 적용해야 한다.

<details>
<summary> ✅ 절차 </summary>
<div markdown="1">

- 파라미터를 위한 임시 변수를 만든다.
- 파라미터에 값을 대입한 코드 이후에서 **파라미터에 대한 참조를 임시변수로 바꾼다.**
- 파라미터에 대입하는 값을 임시변수에 대입하도록 바꾼다.
- 컴파일 & 테스트를 한다.
</div>
</details>

### 8. Replace Method with Method Object

> 긴 메서드가 있는데 지역변수 때문에 Extract Method를 적용할 수 없는 경우에는<br>
> 메서드를 그 자신을 위한 객체로 바꿔서 모든 지역변수가 그 객체의 필드가 되도록 한다.<br>
> 이렇게 하면 메서드를 같은 객체안의 여러 메서드로 분해할 수 있다.

```java
// Before: 복잡한 메서드를 포함한 클래스
public class Order {
    public double calculateTotal(double basePrice) {
        double discountRate;
        double shippingCost;
        
        // 복잡한 할인율 계산
        if (basePrice > 1000) {
            discountRate = 0.95;
        } else {
            discountRate = 0.98;
        }
        
        // 배송비 계산
        if (basePrice > 2000) {
            shippingCost = 0;
        } else {
            shippingCost = 5;
        }
        
        return (basePrice * discountRate) + shippingCost;
    }
}

// After: 메서드를 별도 클래스로 추출
public class TotalPriceCalculator {
    private final double basePrice;
    private double discountRate;
    private double shippingCost;

    public TotalPriceCalculator(double basePrice) {
        this.basePrice = basePrice;
    }

    public double compute() {
        calculateDiscountRate();
        calculateShippingCost();
        return (basePrice * discountRate) + shippingCost;
    }

    private void calculateDiscountRate() {
        if (basePrice > 1000) {
            discountRate = 0.95;
        } else {
            discountRate = 0.98;
        }
    }

    private void calculateShippingCost() {
        if (basePrice > 2000) {
            shippingCost = 0;
        } else {
            shippingCost = 5;
        }
    }
}

// 사용 예시
public class Order {
    public double calculateTotal(double basePrice) {
        var calculator = new TotalPriceCalculator(basePrice);
        return calculator.compute();
    }
}
```
**🪄 동기**
1. 작은 메서드는 늘 아름답다.
2. 거대한 메서드에서 작은 부분을 뽑아냄으로써 코드를 더 이해하기 쉽게 만든다.
3. 지역변수는 메서드를 분해할 때 어려움을 준다.
   - 즉 지역변수가 많으면 분해가 어려워질 수 있다.
4. `Replace Temp with Query`는 이런 짐을 덜도록 도와주지만 때로는 쪼개야하는 메서드를 쪼갤 수 없는 경우가 생길 수 있다.
5. 이런 경우에는 도구 상자의 깊숙한 부분에서 메서드 객체를 꺼내 사용한다.
6. `Replace Temp with Query`를 사용하는 것은 이런 모든 지역변수를 메서드 객체의 필드로 바꿔버린다.
7. 그런 다음에 이 새로운 객체에 `Extract Method`를 사용하여 원래의 메서드를 분해할 수 있다.

<details>
<summary> ✅ 절차 </summary>
<div markdown="1">

- **(복잡한) 메서드의 이름을 따서 새로운 클래스를 만든다.**
- 새로운 클래스에 원래 메서드가 있던 객체(소스 객체)를 보관하기 위한 final 필드를 하나 만들고 메서드에서 사용되는 임시변수와 파라미터를 위한 필드를 만들어준다.
- 새로운 클래스에 소스 객체와 파라미터를 취하는 생성자를 만들어 준다.
- 새로운 클래스에 `compute` (꼭X) 라는 이름의 메서드를 만들어준다.
- 원래의 메서드를 `compute` 메서드로 복사한다.
  - 원래의 객체에 있는 메서드를 사용하는 경우, 소스 객체 필드를 사용하도록 바꾼다.
- 컴파일 & 테스트
- 새로운 클래스의 객체를 만들고 원래 메서드를 새로 만든 객체의 `compute`메서드를 호출하도록 바꾼다.
</div>
</details>

### 9. Substitue Algorithm
> 알고리즘을 보다 명확한것으로 바꾸고 싶을때는 메서드의 몸체를 새로운 알고리즘으로 바꾼다.

```java
public class PersonFinder {
    // Before: 반복적인 if문을 사용한 검색
    public String findPerson(String[] people) {
        for (int i = 0; i < people.length; i++) {
            if (people[i].equals("Don")) {
                return "Don";
            }
            if (people[i].equals("John")) {
                return "John";
            }
            if (people[i].equals("Kent")) {
                return "Kent";
            }
        }
        return "";  // 빈 문자열 반환 (공백 대신)
    }

    // After: 스트림 API와 집합을 사용한 현대적인 방식
    public String findPerson(String[] people) {
        var candidates = Set.of("Don", "John", "Kent");
        
        return Arrays.stream(people)
                .filter(candidates::contains)
                .findFirst()
                .orElse("");
    }

    // 또는 for-each를 사용한 더 간단한 방식
    public String findPerson2(String[] people) {
        var candidates = Set.of("Don", "John", "Kent");
        
        for (String person : people) {
            if (candidates.contains(person)) {
                return person;
            }
        }
        return "";
    }
}
```

**🪄 동기**
1. 어떤 것을 할 때건 한 가지 이상의 방법이 있기 마련이다. 그 중 어떤 것은 분명 다른 것보다 쉬울 것이다.
2. 알고리즘에서도 마찬가지 이다. 어떤 것을 할 때 더 명확한 방법을 찾게 되면 복잡한 것을 명확한 것으로 바꾸어야 한다.
3. 리팩토링은 복잡한 것을 간단한 조각으로 분해하지만 때로는 전체 알고리즘을 간단한 것으로 바꾸어야 하는 시점에 도달하게 된다.
4. 이런 상황은 문제에 대해서 더 많이 알게 되고 그것을 하기 위해 더 쉬운 방법이 있다는 것을 깨닫게 될 때 발생한다.
5. 또한 여러분의 코드와 중복되는 기능지원하는 라이브러리를 사용하기 시작할 때에도 발생한다.
6. 어떤 때에는 어떤일을 조금 다르게 처리하기 위해 알고리즘을 바꾸고 싶을 때가 있는데 원하는 변경을 하기 위해 먼저 **간단한 것으로 치환**하는 것이 더 쉽다.
7. 이 단계를 거져야 할 때 가능한 많이 메서드를 분해해 두어야 한다.
8. 아주 크고 복잡한 알고리즘을 치환 하는 것은 매우 어렵다.
9. 따라서 알고리즘을 간단하게 해야 치환을 쉽게 할수 있다.

<details>
<summary> ✅ 절차 </summary>
<div markdown="1">

- 대체 알고리즘을 준비한다. 적용하여 컴파일 한다.
- 알고리즘을 테스트한다. 만약 결과가 같다면 작업은 끝난 것이다.
- 만약 결과가 같지 않다면 테스트에서 비교하기 위해 예전의 알고리즘을 사용하여 디버깅한다.
  - 예전 알고리즘과 새 알고리즘에 대해 각각의 테스트 케이스를 실행시키고 두 결과를 본다.
  - 이것은 어떤 테스트 케이스가 어떤 문제를 일으키는지 찾는데 도움을 줄 것이다.
</div>
</details>

## 2) 객체간의 기능 이동

### 1. Move Method
> 메서드가 자신이 정의된 클래스보다 다른 클래스의 기능을 더 많이 사용하고 있다면<br>
> 이 메서드를 가장 많이 사용하고 있는 클래스에 비슷한 몸체를 가진 새로운 메서드를 만들어라.<br>
> 그리고 이전 메서드는 간단한 위임으로 바꾸거나 완전히 제거하라.

```java
// Before: 잘못된 위치에 있는 메서드
public class Account {
    private AccountType accountType;
    private double balance;
    
    public double calculateOverdraftCharge() {  // 이 메서드는 AccountType의 특성을 더 많이 사용
        if (accountType.isPremium()) {
            double baseCharge = 10;
            if (daysOverdrawn() <= 7) {
                return baseCharge;
            } else {
                return baseCharge + (daysOverdrawn() - 7) * 0.85;
            }
        } else {
            return daysOverdrawn() * 1.75;
        }
    }
    
    private int daysOverdrawn() {
        // 초과 인출 일수 계산 로직
        return 5;  // 예시 값
    }
}

public class AccountType {
    private boolean premium;
    
    public boolean isPremium() {
        return premium;
    }
}

// After: 메서드를 적절한 클래스로 이동
public class Account {
    private AccountType accountType;
    private double balance;
    
    public double calculateOverdraftCharge() {
        return accountType.calculateOverdraftCharge(daysOverdrawn());
    }
    
    private int daysOverdrawn() {
        // 초과 인출 일수 계산 로직
        return 5;  // 예시 값
    }
}

public class AccountType {
    private final boolean premium;
    
    public AccountType(boolean premium) {
        this.premium = premium;
    }
    
    public boolean isPremium() {
        return premium;
    }
    
    public double calculateOverdraftCharge(int daysOverdrawn) {
        if (isPremium()) {
            var baseCharge = 10.0;
            if (daysOverdrawn <= 7) {
                return baseCharge;
            }
            return baseCharge + (daysOverdrawn - 7) * 0.85;
        }
        return daysOverdrawn * 1.75;
    }
}
```

**🪄 동기**
1. 메서드를 옮기는 것은 리팩토링에서 가장 중요하고 기본이 되는 것이다.
2. 클래스가 너무 많은 동작을 가지고 있거나, 다른 클래스와 공동으로 일하는 부분이 많아서 단단히 결합되어 있을 때 메서드를 옮긴다.
3. **메서드를 옮김으로써 클래스를 더 간단하게 할 수 있고 클래스는 맡고 있는 책임에 대해 더욱 명확한 구현을 가질 수 있게 된다.**
4. 옮길만한 메서드를 발견하면, 이 메서드를 호출하는 메서드, 이 메서드가 호출하는 메서드, 그리고 상속 계층에서 이 메서드를 재정의하고 있는 메서드를 살펴본다.
5. 그리고 옮기려고 하는 메서드와 상호작용을 더 많이 하고 있는 것처럼 보이는 클래스를 기초로 하여 계속 진행할지를 평가한다.

### 2. Move Field
> 필드가 자신이 정의된 클래스보다 다른 클래스의 기능을 더 많이 사용하고 있다면 타겟 클래스에 새로운 필드를 만들고 기존 필드를 사용하는 모든 부분을 변경하라.

**🪄 동기**
1. 어떤 필드가 자신이 속한 클래스보다 다른 클래스의 메서드에서 더 많이 사용되고 있는 것을 보면 그 필드를 옮기는 것을 고려한다.
2. 그러는 한편 다른 클래스가 get/set메서드를 통해서 이 필드를 간접적으로 많이 사용하고 있을지도 모른다는 생각도 한다.

```java
// Before: 필드가 잘못된 클래스에 위치
public class Account {
    private AccountType accountType;
    private double interestRate;  // 이 필드는 AccountType에 더 적합

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double rate) {
        this.interestRate = rate;
    }

    public double calculateInterest() {
        return balance * interestRate;
    }
}

public class AccountType {
    private String typeName;
    
    public boolean isPremium() {
        return "Premium".equals(typeName);
    }
}

// After: 필드를 적절한 클래스로 이동
public class Account {
    private final AccountType accountType;
    private double balance;

    public Account(AccountType accountType) {
        this.accountType = accountType;
    }

    public double calculateInterest() {
        return balance * accountType.getInterestRate();
    }
}

public class AccountType {
    private final String typeName;
    private double interestRate;  // 이자율은 계좌 타입의 특성이므로 여기에 더 적합

    public AccountType(String typeName, double interestRate) {
        this.typeName = typeName;
        this.interestRate = interestRate;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double rate) {
        this.interestRate = rate;
    }

    public boolean isPremium() {
        return "Premium".equals(typeName);
    }
}
```

### 3. Extract Class
> 두개의 클래스가 해야 할 일을 하나의 클래스가 하고 있는 경우, 새로운 클래스를 만들어서 관련 있는 필드와 메서드를 예전 클래스에서 새로운 클래스로 옮겨라.

**🪄 동기**
1. 클래스는 분명하게 추상화되어야 하고, 몇 가지 명확한 책임을 가져야 한다는 말 또는 이와 비슷한 지침을 들었을 것이다.
2. 실제로 클래스는 점점 커진다. 어떤 동작을 추가할 대도 있고 약간의 데이터를 추가할 때도 있다.
3. 우리는 별도의 클래스로 만들만한 가치가 없다고 느끼는 책임을 기존 클래스에 추가한다.
4. 클래스는 많은 메서드와 데이터를 가지고 있고 너무 커서 쉽게 이해할 수도 없다.
5. 이제 우리는 그 클래스를 분리할 방법을 생각하고 클래스를 분리해야 한다.
6. 데이터의 부분 집합과 메서드의 부분 집합이 같이 몰려다니는 것은 별도의 클래스로 분리할 수 있다는 좋은 신호이다.
7. 보통 같이 변하거나 특별히 서로에게 의존적인 데이터의 부분 집합 또한 별도의 클래스로 분리할 수 있다는 좋은 신호이다.
8. 만약 일부 데이터나 메서드를 제거한다면 다른 필드나 메서드가 의미없는 것이 될지를 자신에게 물어보는 것은 편리한 테스트 방법이다.
9. 개발의 후반부에 종종 나타나는 신호중의 하나는 클래스가 서브타입이 되는 방법이다.
10. 서브타이핑이 단지 몇몇 기능에만 영향에 미친다는 것을 알게 되거나 또는 어떤 부분은 이런 식으로 서브타입이 되어야 하고 다른 부분은 또 다른 방법으로 서브타입이 되어야 한다는 것을 알게 될 것이다

```java
// Before: 너무 많은 책임을 가진 큰 클래스
public class Person {
    private String name;
    private String homePhone;
    private String officePhone;
    private String mobilePhone;
    private String street;
    private String city;
    private String postalCode;
    
    public String getName() {
        return name;
    }
    
    public String getHomePhone() {
        return homePhone;
    }
    
    public String getOfficePhone() {
        return officePhone;
    }
    
    public String getFullAddress() {
        return street + ", " + city + " " + postalCode;
    }
    // ... 더 많은 메서드들
}

// After: 책임에 따라 분리된 클래스들
public class Person {
    private final String name;
    private final PhoneNumbers phoneNumbers;
    private final Address address;
    
    public Person(String name, PhoneNumbers phoneNumbers, Address address) {
        this.name = name;
        this.phoneNumbers = phoneNumbers;
        this.address = address;
    }
    
    public String getName() {
        return name;
    }
    
    public PhoneNumbers getPhoneNumbers() {
        return phoneNumbers;
    }
    
    public Address getAddress() {
        return address;
    }
}

public class PhoneNumbers {
    private final String homePhone;
    private final String officePhone;
    private final String mobilePhone;
    
    public PhoneNumbers(String homePhone, String officePhone, String mobilePhone) {
        this.homePhone = homePhone;
        this.officePhone = officePhone;
        this.mobilePhone = mobilePhone;
    }
    
    public String getHomePhone() {
        return homePhone;
    }
    
    public String getOfficePhone() {
        return officePhone;
    }
    
    public String getMobilePhone() {
        return mobilePhone;
    }
}

public class Address {
    private final String street;
    private final String city;
    private final String postalCode;
    
    public Address(String street, String city, String postalCode) {
        this.street = street;
        this.city = city;
        this.postalCode = postalCode;
    }
    
    public String getFullAddress() {
        return String.format("%s, %s %s", street, city, postalCode);
    }
    
    public String getCity() {
        return city;
    }
    
    // ... 필요한 메서드들
}
```

### 4. Inline Class
> 클래스가 하는 일이 많지 않은 경우에는 그 클래스에 있는 모든 변수와 메서드를 다른 클래스로 옮기고 그 클래스를 제거하라.

**🪄 동기**
1. `Inline Class`는 `Extract Class`의 반대이다.
2. 클래스가 더 이상 제 몫을 하지 못하고 더 이상 존재할 필요가 없다면 `Inline Class`를 사용한다.

```java
// Before: 너무 작은 책임을 가진 클래스들
public class Person {
    private final PersonalDetails details;
    private final Address address;
    
    public String getName() {
        return details.getName();
    }
    
    public String getPhoneNumber() {
        return details.getPhoneNumber();
    }
}

public class PersonalDetails {
    private final String name;
    private final String phoneNumber;
    
    public PersonalDetails(String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
    }
    
    public String getName() {
        return name;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
}

// After: 불필요한 클래스를 인라인하여 단순화
public class Person {
    private final String name;
    private final String phoneNumber;
    private final Address address;
    
    public Person(String name, String phoneNumber, Address address) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }
    
    public String getName() {
        return name;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
}
```

<details>
<summary> ✅ 절차 </summary>
<div markdown="1">

- 흡수하는 클래스에 소스 클래스의 public 필드와 메서드를 선언한다.
- 소스 클래스 메서드에 대한 인터페이스를 분리하는 것이 이치에 맞다면, 인라인화 하기 전에 `Extract Interface`를 사용하라.
- 소스 클래스를 참조하고 있는 모든 부분을 흡수하는 클래스를 참조하도록 변경한다.
- 패키지 밖에서 참조하는 부분(out-of-package 참조)을 없애기 위해서 소스 클래스를 `private`으로 선언하라. 또한 컴파일러가 소스 클래스에 대한 모든 죽은 참조(dangling reference) 찾도록 소스 클래스의 이름을 변경한다.
- 컴파일 & 테스트 한다.
- `Move Method`와 `Move Field`를 사용하여, 소스 클래스에 있는 모든 변수와 메서드를 흡수하는 클래스로 옮긴다.
- **짧고 간단한 장례식을 거행한다.**
</div>
</details>

### 5. Hide Delegate
> 클라이언트가 객체의 위임 클래스를 직접 호출하고 있는 경우 서버에 메서드를 만들어 대리 객체(delegate)를 숨겨라.

**🪄 동기**
1. 캡슐화는 객체에서 가장 중요한 개념 가운데 하나이다.
    - 캡슐화는 객체가 시스템의 다른 부분에 대해 적게 알아도 된다는 것을 의미한다.
   - 캡슐화가 되어 있는 경우에는 어떤 것이 변경되었을 때 시스템의 다른 부분이 영향을 덜 받으므로 결과적으로 변경을 좀 더 쉽게 할 수 있게 한다.
2. 자바는 필드가 public으로 선언되는 것을 허용하지만, 객체를 다루는 사람이라면 필드는 숨겨져야 한다는 것을 알고 있다.
3. 여러분은 점점 세련되어 질수록 캡슐화 할 수 있다는 것이 더 많아진다는 것을 알게 된다.
4. 클라이언트가 서버 객체의 필드에 들어있는 객체에 정의된 메서드를 호출한다면, 클라이언트는 대리객체(delegate)에 대해서 알아야 한다.
5. 이와 같은 경우에 서버 객체에 간단한 위임 메서드를 두어 위임을 숨김으로서 이런 종속성을 제거할 수 있다.
6. 서버의 일부 또는 모든 클라이언트에 대해서 Extract Class를 사용할 가치가 있다는 것을 발견할지도 모른다.
7. 만약 모든 클라이언트에게 실제로 일을 처리하는 부분을 숨기고 있다면 서버의 인터페이스에서 위임과 관련된 모든 부분을 제거할 수 있다.

```java
// Before: 클라이언트가 위임 객체를 직접 접근
public class Person {
    private final Department department;
    
    public Person(Department department) {
        this.department = department;
    }
    
    public Department getDepartment() {
        return department;
    }
}

public class Department {
    private final Employee manager;
    
    public Department(Employee manager) {
        this.manager = manager;
    }
    
    public Employee getManager() {
        return manager;
    }
}

// 클라이언트 코드
public class Client {
    public void someMethod() {
        Person person = new Person(new Department(new Employee("John")));
        // 클라이언트가 위임 객체를 직접 탐색 (Law of Demeter 위반)
        Employee manager = person.getDepartment().getManager();
    }
}

// After: 위임을 숨기는 메서드 추가
public class Person {
    private final Department department;
    
    public Person(Department department) {
        this.department = department;
    }
    
    // 위임을 숨기는 메서드 추가
    public Employee getDepartmentManager() {
        return department.getManager();
    }
}

// 클라이언트 코드
public class Client {
    public void someMethod() {
        Person person = new Person(new Department(new Employee("John")));
        // 단순화된 인터페이스를 통해 접근
        Employee manager = person.getDepartmentManager();
    }
}
```
<details>
<summary> ✅ 절차 </summary>
<div markdown="1">

- 대리 객체의 각각의 메서드에 대해, 서버에서 간단한 위임 메서드를 만든다.
- 클라이언트가 서버를 호출하도록 바꾼다.
  - 클라이언트가 서버와 같은 패키지에 있지 않다면 실제로 일을 처리하는 메서드의 접근 권한을 `package`로 변경하는 것을 고려하라.
- 각각의 메서드를 알맞게 바꾸고 나서 컴파일 & 테스트를 한다.
- 어떤 클라이언트에서도 더 이상 대리객체에 접근할 필요가 없다면, 서버 클래스에서 대리객체에 대한 접근자를 제거한다.
</div>
</details>

#### The Law of Demeter (LoD)
> "최소 지식 원칙"은 객체지향 설계의 중요한 원칙 중 하나이다.<br>
> 각 객체는 자신과 직접적으로 관련된 객체와만 상호작용해야 한다는 원칙이다.

```java
// Law of Demeter 위반 예시
public class Customer {
    private Wallet wallet;
    
    public Wallet getWallet() {
        return wallet;
    }
}

public class Store {
    public void purchaseItem(Customer customer, double itemPrice) {
        // 나쁜 예: 다른 객체의 내부 구조를 너무 많이 알고 있음
        if (customer.getWallet().getMoney() >= itemPrice) {
            customer.getWallet().deductMoney(itemPrice);
        }
    }
}

// Law of Demeter 준수 예시
public class Customer {
    private Wallet wallet;
    
    public boolean canAfford(double amount) {
        return wallet.hasSufficientFunds(amount);
    }
    
    public void pay(double amount) {
        wallet.deductMoney(amount);
    }
}

public class Store {
    public void purchaseItem(Customer customer, double itemPrice) {
        // 좋은 예: 객체의 내부 구현에 대해 알 필요가 없음
        if (customer.canAfford(itemPrice)) {
            customer.pay(itemPrice);
        }
    }
}
```

> LoD를 준수하는 방법
> - 객체는 다음과 직접 대화해야 한다. 
>   - 자신의 필드 
>   - 메서드의 파라미터 
>   - 자신이 생성한 객체 
>   - 직접적인 컴포넌트 객체
> - '한 단계'만 호출하기
> - 체이닝 피하기

### 6. Remove Middle Man
> 클래스가 간단한 위임을 너무 많이 하고 있는 경우에는 클라이언트가 대리객체(Delegate)를 직접 호출하도록 하라.

**🪄 동기**
1. `Hide Delegate`를 사용하는 동기를 이야기할 때 대리객체 사용을 캡술화 하는 것의 장점에 대해서 이야기 했다.
   - 그러나 여기에는 그만한 대가를 치러야 한다.
2. 클라이언트 대리객체의 새로운 메서드를 사용하려 할 때 마다 서버 클래스는 간단한 위임 메서드를 추가해야하는 것이다.
   - 새로운 메서드를 추가하려면 추가 비용이 들게 된다.
3. 서버 클래스는 단지 미들맨(Middle Man)에 지나지 않게 되는데 아마도 이때가 클라이언트로 하여금 대리객체를 직접 호출하도록 해야할 때일 것이다.
4. 어느 정도를 숨기는 것이 적절한지 판단하는 것은 어렵다.
5. 다행이도 Hide Delegate와 Remove Middle Man에서는 이것이 별로 중요하지 않다.
6. 시간이 지나고 시스템이 변할수록 얼마나 숨겨야 하는지에 대한 원칙 또한 변경된다.

```java
// Before: 과도한 위임 메서드
public class Person {
    private final Department department;
    
    public Person(Department department) {
        this.department = department;
    }

    // 단순 위임 메서드들이 너무 많음
    public Employee getManager() {
        return department.getManager();
    }
    
    public List<Employee> getTeamMembers() {
        return department.getTeamMembers();
    }
    
    public String getDepartmentName() {
        return department.getName();
    }
    
    public Location getDepartmentLocation() {
        return department.getLocation();
    }
    
    public Budget getDepartmentBudget() {
        return department.getBudget();
    }
}

// After: 위임 객체를 직접 접근하도록 변경
public class Person {
    private final Department department;
    
    public Person(Department department) {
        this.department = department;
    }
    
    // 필요한 경우 department 직접 접근 허용
    public Department getDepartment() {
        return department;
    }
}

// 클라이언트 코드
public class Client {
    public void someMethod(Person person) {
        // 직접 department의 메서드 호출
        Department dept = person.getDepartment();
        Employee manager = dept.getManager();
        List<Employee> team = dept.getTeamMembers();
        String deptName = dept.getName();
        Location location = dept.getLocation();
        Budget budget = dept.getBudget();
    }
}
```
<details>
<summary> ✅ 절차 </summary>
<div markdown="1">

- 대리객체에 대한 접근자를 만든다.
- 서버 클래스에 있는 위임 메서드를 사용하는 각각의 클라이언트에 대해 클라이언트가 대리객체의 메서드를 호출하도록 바꾸고 서버 클래스에 있는 메서드를 제거한다.
- 각각의 메서드에 대한 작업을 마칠 때 마다 컴파일 & 테스트 한다. 
</div>
</details>

### 7) Introduce Foreign Method
> 사용하고 있는 서버 클래스에 부가적인 메서드가 필요하지만 클래스를 수정할 수 없는 경우에는 첫 번째 인자로 서버 클래스의 인스턴스를 받는 메서드를 클라이언트에 만들어라.

**🪄 동기**
1. 모든 서비스를 제공하는 정말로 멋진 클래스를 사용하고 있다.
2. 그러나 꼭 필요하지만 그 클래스가 제공하지 않는 서비스가 하나 있다.
3. 소스 코드를 변경할 수 없다면 부족한 메서드를 클라이언트 쪽에 만들어야한다.
4. 클라이언트 클래스에서 필요한 메서드를 단지 한 번만 사용한다면 추가 코딩은 큰 문제가 아니고, 이런 경우에는 아마도 서버 클래스에 메서드를 추가할 필요가 없을 것이다.
5. 새로 만드는 메서드를 외래 메서드(`Foreign method`)로 만들어서 이 메서드가 실제로는 서버 클래스에 있어야 하는 메서드라는 것을 명확하게 나타낼 수 있다.
6. 만약 서버 클래스의 외래 메서드를 많이 만들어야 한다는 것을 깨닫게 되거나 많은 클래스가 동일한 외래 메서드를 필요로 한다는 것을 알게 된다면 `Introduce Local Extension` 대신 사용해야 한다.
7. 외래 메서드는 임시 방편이라는 것을 잊지마라!
8. 만약 할 수 있다면, 외래 메서드를 그들이 원래 있어야 하는 위치로 옮기는 것을 시도해 봐라.
9. 코드 소유권이 문제가 된다면 외래 메서드를 서버 클래스의 소유자에게 보내고 그 소유자에게 그 메서드를 구현해 달라고 요청하라.
```java
// 수정할 수 없는 서버 클래스 (예: 라이브러리 클래스)
public final class Date {
    // 자바의 Date 클래스라고 가정
    // 수정 불가능한 클래스
}

// Before: 클라이언트 코드에서 반복적인 날짜 계산
public class DateReport {
    public void someMethod() {
        Date date = new Date();
        
        // 다음날을 구하는 로직이 여러 곳에서 반복됨
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, 1);
        Date nextDate = calendar.getTime();
    }
    
    public void anotherMethod() {
        Date date = new Date();
        
        // 같은 로직이 반복됨
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, 1);
        Date nextDate = calendar.getTime();
    }
}

// After: Foreign Method 도입
public class DateReport {
    // Foreign Method - Date 클래스를 확장하는 것처럼 사용
    public static Date nextDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, 1);
        return calendar.getTime();
    }
    
    // 실제 사용
    public void someMethod() {
        Date date = new Date();
        Date nextDate = nextDay(date);
    }
    
    public void anotherMethod() {
        Date date = new Date();
        Date nextDate = nextDay(date);
    }
}
================================================================================================
// 더 현대적인 접근: 확장 함수 사용 (Kotlin)
fun Date.nextDay(): Date {
    val calendar = Calendar.getInstance()
    calendar.time = this
    calendar.add(Calendar.DATE, 1)
    return calendar.time
}

// 또는 Java의 유틸리티 클래스 사용
public class DateUtils {
    private DateUtils() { } // 인스턴스화 방지
    
    public static Date nextDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, 1);
        return calendar.time;
    }
}
```

<details>
<summary> ✅ 절차 </summary>
<div markdown="1">

- 필요한 작업을 하는 메서드를 클라이언트 클래스에 만든다.
  - 그 메서드는 클라이언트 클래스의 어떤 부분에도 접근해서는 안 된다.
  - 값이 필요하다면 값을 파라미터로 넘겨야 한다.
- 첫 번째 파라미터로 서버 클래스의 인스턴스를 받도록 한다.
- 메서드에 `'외래 메서드, 원래는 서버 클래스에 있어야 한다.'`와 같은 주석을 달아 놓는다.
  - 이렇게 해두면 나중에 이들 메서드를 옮길 기회가 생겼을 때 텍스트 검색을 이용하여 외래 메서드를 쉽게 찾을 수 있다.

</div>
</details>
