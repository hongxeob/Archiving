## BinarySearch

- 이분 탐색 이라고도 한다.
- 정렬된 배열 안에서 특정 원소를 찾을 때 인덱스 i부터 j의 **중간값과 비교** 한다.
- 중간값이 찾는 원소가 아니라면 인덱스 i와 j를 다시 정해준다.
- **인덱스 i와 j를 정할 때마다 탐색 범위는 반으로 줄어든다.**
- 시간 복잡도 : `O(log N)`
```java
public class BinarySearch {
    static int[] arr;
    static int answer;

    public static void main(String[] args) {
        arr = new int[]{1, 2, 4, 6, 7, 8, 9};
        binarySearch(8);
        System.out.println("count = " + answer);
    }

    private static int binarySearch(int goal) {
        int low = 0;
        int high = arr.length - 1;

        while (high >= low) {
            int mid = (low + high) / 2;

            if (goal == arr[mid]) {
                return mid;
            } else if (goal > arr[mid]) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        //원하는 값 찾지 못함
        return -1;
    }
}
```

>- 처음 범위는 인덱스 0~끝까지 이다. 이때 중간 인덱스를 mid로 한다.
>- mid의 값과 찾는 원소를 비교한다.
>  - 목표하는 값이 mid와 같다면 탐색을 종료한다.
>  - 목표하는 값이 mid보다 크다면 low의 값을 mid+1로 범위를 옮겨준다.
>  - 목표하는 값이 mid보다 작다면 high의 값을 mid-1로 범위를 옮겨준다.
>  - 만약 high < left가 된다면 해당 배열에 찾는 원소가 없는 것이다.