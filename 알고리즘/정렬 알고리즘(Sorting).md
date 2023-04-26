>### swap()
>- 배열의 두 인덱스의 원소를 교환하는 메소드이다.
>- 정렬의 특성상 자주 사용되며 다음에 나올 예시들에서도 사용해도 된다.
>```java
>public static void swap(int[] arr, int idx1, int idx2) {
>        int temp = arr[idx1];
>        arr[idx1] = arr[idx2];
>        arr[idx2] = temp;
>    }
>```

## 버블 정렬 (Bubble Sort)

- 순서대로 **근접한 두 수를 비교**해서 오른쪽 수가 왼쪽 수보다 더 작으면 교환
- 이 작업을 한 번 수행할 때마다 맨 끝자리에 가장 큰수가 가게 된다.
- 시간 복잡도 : `O(N^2)`

```java
private static void bubbleSort(int[] arr) {
        int temp;
        for (int i = 0; i < arr.length - 1; i++) { //회차
            for (int j = 0; j < arr.length - 1 - i; j++) {// 회차안의 인접한 숫자 비교
                if (arr[j] > arr[j + 1]) {
                    //swap 해주는 코드
                    temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                }
            }
        }
    }
```

## 선택 정렬 (Selection Sort)

- 맨 앞 인덱스부터 차례대로 들어갈 원소를 선택하여 정렬하는 알고리즘
- for문을 통해 가장 작은 값을 찾고, 맨 앞자리와 교환
- 시간 복잡도 : `O(N^2)`

```java
private static void selectSort(int[] arr) {
        int temp;
        for (int i = 0; i < arr.length - 1; i++) {
            int minIdx = i;
        
            //최솟값을 갖고 있는 인덱스 찾기
            for (int j = i + 1; j < arr.length; j++) {
                if (arr[j] < arr[minIdx]) {
                    minIdx = j;
                }
            }
            // i번째 값과 찾은 최솟값을 서로 교환
            temp = arr[i];
            arr[i] = arr[minIdx];
            arr[minIdx] = temp;
        }
    }
```

>-  인덱스 0~(n-1)을 돈다 → 마지막 수는 다음 수와 비교할 필요가 없을뿐더러 비교할 수도 없다.
>- 인덱스 0과 가장 작은 인덱스의 원소를 바꿔준다.
>- 다시 인덱스 1~(n-1)을 돌면서 원소의 값이 가장 작은 인덱스를 찾는다.
>- 인덱스 1과 가장 작은 인덱스의 원소를 바꿔준다.

## 삽입 정렬 (**Insertion** Sort)

- 인덱스 1의 원소부터 **앞 방향으로 들어갈 위치를 찾아 교환**하는 정렬 알고리즘
- 정렬이 되어 있는 배열의 경우 O(n)의 속도로 정렬되어 있을 수록 성능이 좋다.
- 시간 복잡도 : `O(n^2)`

```java
private static void insertionSort(int[] arr) {
        for (int i = 1; i < arr.length; i++) {
            //현재 타겟 넘버
            int current = arr[i];
            //이전 원소
            int prevIdx = i - 1;
            //현재 타겟이 이전 원소보다 크기 전까지 반복 -> 
            //이전 원소가 시작 지점인 0보다 커야하고, 이전 요소가 현재 타겟 넘버보다 커야 반복문을 돈다.
            while (prevIdx >= 0 && current < arr[prevIdx]) {
                arr[prevIdx + 1] = arr[prevIdx]; // 이전 원소를 한 칸씩 뒤로 민다.
                prevIdx--;
            }
            
            // 위 반복문에서 탈출하는 경우, 앞의 원소(prevIdx)가 현재 타겟(current)보다 작다는 의미이므로
            // 현재 타겟 원소는 prevIdx번째 원소 뒤에 와야한다.
            // 그러므로 현재 타켓은 prevIdx + 1에 위치하게 된다.
            arr[prevIdx + 1] = current;
        }
    }
```

>- 인덱스 1 ~ (n-1)의 원소들을 순차적으로 자신이 들어갈 위치에 넣는다.
>- while문을 사용하여 더 작으면 계속 앞으로 전진시키고, 비교한 원소를 한 칸씩 뒤로 민다.
## 병합 정렬 (Merge Sort)

- 병합 정렬은 분할 정복 알고리즘에 따라서 하나의 리스트를 두 개의 리스트로 분할한 다음 각각의 분할된 리스트를 정렬한 후에 합하여 정렬된 하나의 리스트로 만드는 방법이다.
- 순서는
    - 분할 : 입력된 리스트를 두 개의 리스트로 분할한다.
    - 정복 : 분할된 리스트를 정렬한다. 분할된 리스트의 크기가 작지 않다면 재귀 호출을 이용하여 다시 분할 정복한다.
    - 결합 : 정렬된 두 개의 리스트를 하나의 리스트로 결합한다.
- 시간복잡도 :  `O(nlogn)`
    
<img width="689" alt="image" src="https://user-images.githubusercontent.com/97447334/233991174-5dac6d7a-946f-4d8a-a582-0d29b55da7c2.png">
    
```java
    public class MergeSort {
        private static int[] arr;
        private static int[] temp;
    
        public static void main(String[] args) {
            arr = new int[]{1, 9, 8, 5, 4, 2, 3, 7, 6};
            temp = new int[arr.length];
            printArray(arr);
            merge_sort(0, arr.length - 1);
            printArray(arr);
        }
    
        private static void merge_sort(int start, int end) {
            if (start < end) {
                int mid = (start + end) / 2;
                merge_sort(start, mid);
                merge_sort(mid + 1, end);
                int p = start;
                int q = mid + 1;
                int idx = p;
                while (p <= mid || q <= end) {
                    if (q > end || (p <= mid && arr[p] < arr[q])) {
                        temp[idx++] = arr[p++];
                    } else {
                        temp[idx++] = arr[q++];
                    }
                }
                for (int i = start; i <= end; i++) {
                    arr[i] = temp[i];
                }
            }
        }
    
        public static void printArray(int[] a) {
            for (int i = 0; i < a.length; i++)
                System.out.print(a[i] + " ");
            System.out.println();
        }
    
    }
```
> - `satrt`는 merge_sort를 진행할 배열의 시작 인덱스, `end`는 마지막으로 포함될 배열의 인덱스를 의미
> - mid는 그 둘의 중간 지점
> - 이후 실제 분할을 진행하는데, 첫 분할은 시작점부터 중간지점까지인 `merge_sort(start,mid)`, 두번째 분할은 중간점 다음부터 끝점까지인 `merge_sort(mid+1, end)`가 된다.
> - int p와 int q에 **두 분할의 첫 번째 원소의 인덱스**를 저장
>   - 저장하는 이유는, 해당 인덱스의 값을 비교하여 어떤 원소를 참조할지 정하기 때문
>   - start는 항상 mid+1보다 작기 때문에, 분할의 저장 위치는 start지점이 된다 → `idx = p`
> - p가 mid 이하거나, q가 end이하일 때 동작해야 한다. 미만이 아닌 이유는 원소의 개수가 1개일 때까지 쪼개기 때문이다.
>   - 그리고 동시에 종료가 되지 않을 수 있으니 두 경우를 &&으로 하지 않고 ||로 하는 것에 주의
> - 첫 번째 분할에서 원소를 가져오는 경우는 다음과 같다.
>   1. 두 번째 분할의 원소를 이미 다 가져온 경우 (`q>end`)
>   2. 첫 번째 분할에서 가져오지 않은 원소가 있고, 첫번째 분할의 첫 원소 값이 두 번째 분할의 첫 원소 값보다 작은 경우
>   - 위의 두 조건 중 하나 이상 만족하면 첫 번째 분할에서 원소를 가져오므로 두 조건 사이에 or사용
> - if 문에 결과에 따라 1번 분할의 첫 번째 값이나, 2번 분할의 첫번째 값을 정렬된 값을 임시 저장하는 배열인 temp의 idx에 저장해준다.
>   - 이때, 가져온 원소의 다음 인덱스를 다시 비교하기 위해 p++ 또는 q++를 조건에 맞게 해 준다.
>   - (idx도 당연히 ++ 해줘야 한다. 그다음 최솟값을 찾아서 저장할 거니까 )
> - 그리고, 1번 분할과 2번 분할의 모든 원소를 가져오면, start 부터 end까지 정렬된 값을 다시 arr이라는 원래 배열에 저장해준다.
## 퀵 정렬 (Quick Sort)

- `분할 정복(divide and conquer)` 방법을 통해 주어진 배열 정렬
    - 문제를 작은 2개의 문제로 분리하고 각각을 해결한 후, 결과를 모아 원래의 문제를 해결하는 방법
- 시간복잡도 :  `O(nlogn)`
    
```java
    public class QuickSort {
        public static void main(String[] args) {
            int[] arr = {3, 1, 5, 6, 20, 10, 7, 11, 15, 9};
            quick_sort(arr);
            System.out.println("arr = " + arr);
        }
    
        private static void quick_sort(int[] arr) {
            quick_sort(arr, 0, arr.length - 1);
        }
    
        private static void quick_sort(int[] arr, int start, int end) {
            //start가 end보다 크거나 같다면 정렬할 원소가 1개 이하이므로 정렬하지 않고 return
            if (start >= end) {
                return;
            }
            //가장 왼쪽의 값을 pivot으로 지정, 실제 비교 검사는 start+1부터 시작
            int pivot = start;
            int left = start + 1;
            int right = end;
            //left는 현재 부분 배열의 왼쪽, right는 오른쪽을 의미
            //서로 엇갈리게 될 경우 while문 종료
            while (left <= right) {
                //피벗보다 큰 값을 만날 때까지
                while (left <= end && arr[left] <= arr[pivot]) {
                    left++;
                }
                //피벗보다 작은 값을 만날 때까지
                while (right > end && arr[right] <= arr[pivot]) {
                    right--;
                }
                //엇갈리면 피벗과 교체
                if (left > right) {
                    swap(arr, right, pivot);
                } else {
                    // 엇갈리지 않으면 left, right 값 교체
                    swap(arr, left, right);
                }
                //엇갈렸을 경우
                //피벗값과 right값을 교체한 후 해당 피벗을 기준으로 앞 뒤로 배열을 분할하여 정렬 진행
                quick_sort(arr, start, right - 1);
                quick_sort(arr, right + 1, end);
            }
        }
    
        private static void swap(int[] arr, int i, int j) {
            int temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }
    }
 ```

>- 배열 가운데 하나의 원소를 고르며, 그 원소를 pivot이라고 함(피벗을 기준으로 비균등하게 2개의 부분 배열로 분할)
>- 피벗을 기준으로 피벗 앞에는 피벗보다 작은 수가, 오른쪽에는 피벗보다 큰 수가 옮겨짐
>- 피벗을 제외한 왼쪽 리스트와 오른족 리스트를 다시 정렬(피벗을 다시 선택해 정렬을 반복)
>- 부분 리스트들이 더 이상 분할이 불가능할 때까지 반복
## 두 개의 스택으로 큐 만들기

- 스택 : 1,2,3을 push하고 pop하면 3,2,1 순으로 나오게 된다.
- 큐 : 1,2,3을 enqueue 하고 dequeue 하면 1,2,3 순으로 나오게 된다.

즉 스택과 큐는 역순이다. 나오는 자료를 역순으로 뒤집어 주기만 하면 2개의 스택으로 하나의 큐를 구현할 수 있다.

```java
public class Qstack {

    public static void main(String[] args) {
        Qstack a = new Qstack();
        a.enqueue(1);
        a.enqueue(2);
        a.enqueue(3);
        System.out.println(a.dequeue());
        System.out.println(a.dequeue());
        System.out.println(a.dequeue());
    }

    Stack<Integer> newStack;
    Stack<Integer> oldStack;

    public Qstack() {
        newStack = new Stack<>();
        oldStack = new Stack<>();
    }

    public void enqueue(int a) {
        newStack.push(a);
    }

    public int dequeue() {
        int result = -1;
        if (oldStack.isEmpty()) {
            while (!newStack.isEmpty()) {
                oldStack.push(newStack.pop());
            }
            result = oldStack.pop();
        }
         //oldStack에 남아있는 값이 있으면 다시 #1로 옮겨준다.
        if (!oldStack.isEmpty()) {
            while (!oldStack.isEmpty()) {
                newStack.push(oldStack.pop());
            }
        }
        return result;
    }
}
```