## 이진 트리

>이진 탐색 트리(Binary Search Tree)를 알아보기 전에 우선 이진 트리(Binary Tree)가 무엇인지 알아야한다. 정의는 **비어있거나, 한 개의 루트와 다른 두 개의 다른 값을 가진 노드의 집합**이다.

조금 더 풀어서 얘기하면, 최상의 루트 노드로부터 하위의 서브 트리 방향으로 점차 나아갈 때 각각 새롭게 루트가 되는 노드가 가지는 자식 노드의 개수가 2개 이하임을 말한다.

## 트리의 종류

- **루트가 있는 트리(Rooted Binary Tree)** : 모든 노드의 자식 노드가 최대 2개인 루트를 가지 트리
- **정 이진 트리(Full Binary Tree)** : 단말 노드가 아닌 모든 노드가 2개의 자식 노드를 가진 트리
- **포화 이진 트리(Perfect Binary Tree)** : 모든 단말 노드의 깊이가 같은 정 이진 트리(Full Binary Tree)
- **완전 이진 트리(Complete Binary Tree)** : 끝부분을 제외하고 모든 노드가 채워진 이진 트리

### 이진 트리

- 이진 트리(Binary Tree)의 정의는 모든 노드가 두 개 이하의 자식 노드를 가져야 한다.
- 그러니까 모든 노드의 차수(Degree)가 2 이하인 트리를 말한다.
- 그리고 이진 트리의 모든 서브 트리들은 모두 이진 트리다.


<img width="644" alt="image" src="https://user-images.githubusercontent.com/97447334/235600152-d5d53e52-92ab-443c-904f-38ece5517388.png">

노드 5의 서브 트리는 루트가 4인 트리와 8인 트리가 있다. 또 노드 8은 루트가 10인 오른쪽 서브 트리를 가지고 있다.

앞서 살펴본 트리 관련 용어를 적용해보면 위 트리의 높이는 3이 된다. 루트의 깊이가 0일 때, 가장 깊이 있는 노드 9, 11 까지의 경로를 카운트하면 된다. 또한 노드 4의 차수(Degree)는 2이며 자식이 없는 단말 노드는 3, 7, 9, 11이 된다.

### 완전 이진 트리

- 마지막 노드를 제외하고 모든 노드가 채워져 있는 트리를 말한다.
- 또한 마지막 레벨에서는 왼쪽부터 오른쪽으로 노드가 채워져있는 트리를 말한다.

<img width="641" alt="image" src="https://user-images.githubusercontent.com/97447334/235600318-07981827-fabd-4700-a361-744d18f36c70.png">

### 포화 이진 트리

- 모든 노드가 가득 차 있어야 한다.
- 왼쪽이든 오른쪽이든 가득 차 있어야 한다.

<img width="640" alt="image" src="https://user-images.githubusercontent.com/97447334/235600638-18907814-858d-4ce2-85a3-3e08451061e5.png">

완전 이진 트리는 포화 이진 트리가 될 수 없지만, 포화 이진 트리는 완전 이진 트리라고 할 수 있다.

## 이진 탐색 트리

이진 탐색 트리는 이진 트리다. 하지만 좀 더 세부적인 정의를 갖고 있는데,

1. 비어 있지 않다면 모든 노드는 서로 다른 값(Key)을 갖는다.
2. 왼쪽 서브 트리에 포함된 값들은 만일 존재한다면, 루트의 값보다 항상 작은 값을 갖는다.
3. 오른쪽 서브 트리에 포함된 값들은 만일 존재한다면, 루트의 값보다 항상 큰 값을 갖는다.
4. 왼쪽와 오른쪽 서브 트리는 각각 루트 노드를 가진 또 하나의 이진 탐색 트리 구조여야 한다.

## 이진 탐색 트리의 삽입과 삭제

### 삽입 연산

**삽입 연산**은 새로운 노드가 위치할 곳을 탐색하는 과정이 필요하다.

- 1-1) 루트 노드에 값이 없는 경우는 트리가 없는 경우이므로 루트에 넣는다.
- 1-2) 루트에 값이 있고 삽입되는 값이 루트보다 작으면 왼쪽으로 탐색시킨다.
- 1-3) 루트에 값이 있고 삽입되는 값이 루트보다 크면 오른쪽으로 탐색시킨다.
- 2)해당 방향으로 탐색을 진행했을 때, 탐색 위치에 값이 없으면 해당 위치에 노드를 추가한다.

### **삭제 연산**

**삭제 연산**은 삽입 연산에 비하여 조금 까다롭다. 삭제 대상 노드의 특성에 따라서 구분할 필요가 있다.

- 1)우선 삭제할 노드의 값을 루트로부터 비교하는 탐색을 시작한다.
    - 1-1) 삭제할 노드의 값이 루트보다 작으면 왼쪽으로 탐색하고 현재 포인터를 오른쪽 노드로 변경한다.
    - 1-2) 삭제할 노드의 값이 루트보다 크면 왼쪽으로 탐색하고 현재 포인터를 왼쪽 노드로 변경한다.
- 2-1) 삭제하려는 노드가 단말 노드인 경우
    - 2-1-1) 삭제 대상이 단말 노드이고 부모 노드의 왼쪽 자식 노드라면 부모 노드의 왼쪽 자식을 NULL로 만든다.
    - 2-1-2) 삭제 대상이 단말 노드이고 부모 노드의 오른쪽 자식 노드라면 부모 노드의 오른쪽 자식을 NULL로 만든다.
- 2-2) 삭제하려는 노드가 한쪽 자식 노드만 가지는 경우
    - 2-2-1) 오른쪽 자식 노드만 가진 경우, 삭제 대상 노드의 부모에게 자신의 오른쪽 자식 노드를 붙여준다.
    - 2-2-2) 왼쪽 자식 노드만 가진 경우, 삭제 대상 노드의 부모에게 자신의 왼쪽 자식 노드를 붙여준다.
- 2-3) 삭제하려는 노드가 모든 자식 노드를 가지는 경우
    - 2-3-1) 제되는 노드와 가장 비슷한 값을 선택하여야 한다. 그래야 다른 노드의 이동 없이 이진 탐색 트리가 유지된다.
    - 2-4-1) 삭제 대상의 왼쪽 서브 트리에서 가장 큰 값을 선택한다.
    - 2-4-2) 삭제 대상의 오른쪽 서브 트리에서 가장 작은 값을 선택한다.
    
## **이진 탐색 트리의 탐색**
    
탐색은 이진 트리의 모든 노드를 방문하는 것을 말한다. 탐색 방법에는 대표적으로 전위(pre-order), 후위(post-order) 그리고 중위(in-order)가 있다.
- **전위 탐색** : 내 노드, 왼쪽 자식 노드, 오른쪽 자식 노드 순서로 방문.
- **후위 탐색** : 왼쪽 자식 노드, 오른쪽 자식 노드, 내 노드 순서로 방문.
- **중위 탐색** : 왼쪽 자식 노드, 내 노드, 오른쪽 자식노드 순서로 방문.

### 시간 복잡도
이진 탐색 트리의 검색, 삽입, 삭제는 트리의 높이에 비례하여 시간 복잡도가 증가한다고 했다.
<br> 따라서, 이를 일반적인 Big-O 시간 복잡도로 표현하자면 위 식을 간소화한 `O(logN)` 으로 표현할 수 있는 것이다.

## 예시 코드
```java
/**
 * 노드를 타타낼 클래스 
 */
public class Node {
    int value; // 노드의 값
    Node leftChild; // 왼쪽 자식
    Node rightChild; // 오른쪽 자식

    public Node(int value) {
        this.value = value;
        this.leftChild = null;
        this.rightChild = null;
    }
}
```
```java
/**
 * 이진 탐색 트리를 구현
 */
public class BinaryTree {
    Node rootNode = null;

    /**
     * 새로운 노드 삽입
     */
    public void insertNode(int element) {
        /**
         *루트가 빈 경우, 즉 아무 노드도 없는 경우
         */
        if (rootNode == null) {
            rootNode = new Node(element);
        } else {
            Node head = rootNode;
            Node currentNode;
            while (true) {
                currentNode = head;
                /**
                 * 현재의 루트보다 작은 경우, 왼쪽으로 탐색
                 */
                if (head.value > element) {
                    head = head.leftChild;

                    /**
                     * 왼쪽 자식 노드가 비어있는 경우, 해당 위치에 추가할 노드 삽입
                     * 현재 currentNode head를 가리킴
                     */
                    if (head == null) {
                        currentNode.leftChild = new Node(element);
                        break;
                    }
                } else {
                    /**
                     * 현재의 루트보다 큰 경우, 오른쪽으로 탐색
                     */
                    head = head.rightChild;
                    /**
                     * 오른쪽 자식 노드가 비어있는 경우, 해당 위치에 추가할 노드를 삽입한다
                     */
                    if (head == null) {
                        currentNode.rightChild = new Node(element);
                        break;
                    }
                }
            }
        }
    }

    /**
     * 특정 노드 삭제
     */
    public boolean removeNode(int element) {
        Node removeNode = rootNode;
        Node parentOfRemoveNode = null;
        while (removeNode.value != element) {
            parentOfRemoveNode = removeNode;

            /* 삭제할 값이 현재 노드보다 작으면 왼쪽을 탐색. */
            if (removeNode.value > element) {
                removeNode = removeNode.leftChild;
            } else {
                removeNode = removeNode.rightChild;
            }
            /**
             * 값 대소를 비교하며 탐색했을 때
             * 잎 노드(leaf Node)인 경우 삭제를 위한 탐색 실패
             */
            if (removeNode == null) {
                return false;
            }
        }
        /* 자식 노드 가 모두 없을 때 */
        if (removeNode.leftChild == null && removeNode.rightChild == null) {
            /* 삭제 대상이 트리의 루트일 때 */
            if (removeNode == rootNode) {
                rootNode = null;
            } else if (removeNode == parentOfRemoveNode.rightChild) {
                parentOfRemoveNode.rightChild = null;
            } else {
                parentOfRemoveNode.leftChild = null;
            }
        }
        /* 왼쪽 자식 노드만 존재하는 경우 */
        else if (removeNode.rightChild == null) {
            if (removeNode == rootNode) {
                rootNode = removeNode.leftChild;
            } else if (removeNode == parentOfRemoveNode.rightChild) {
                parentOfRemoveNode.rightChild = removeNode.leftChild;
            } else {
                /**
                 * 삭제 대상의 왼쪽 자식을 삭제 대상 위치에 둔다.
                 */
                parentOfRemoveNode.leftChild = removeNode.leftChild;
            }
        }
        /**
         * 두 개의 자식 노드가 존재하는 경우
         * 삭제할 노드의 왼쪽 서브 트리에 있는 가장 큰 값 노드를 올리거나
         * 오른쪽 서브 트리에 있는 가장 작은 값 노드를 올리면 된다.
         * 구현 코드는 2번째 방법을 사용한다.
         */
        else {
            /* 삭제 대상 노드의 자식 노드 중에서 대체될 노드(replaceNode)를 찾는다. */
            Node parentOfReplaceNode = removeNode;
            /* 삭제 대상의 오른쪽 서브 트리 탐색 지정 */
            Node replaceNode = parentOfReplaceNode.rightChild;
            while (replaceNode.leftChild != null) {
                /* 가장 작은 값을 찾기 위해 왼쪽 자식 노드로 탐색한다 */
                parentOfReplaceNode = replaceNode;
                replaceNode = replaceNode.leftChild;
            }
            if (replaceNode != removeNode.rightChild) {
                /* 가장 작은 값을 선택하기 때문에 대체 노드의 왼쪽 자식은 빈 노드가 된다. */
                parentOfReplaceNode.leftChild = replaceNode.rightChild;
                /* 대체할 노드의 오른쪽 자식 노드를 삭제할 노드의 오른쪽으로 지정한다. */
                replaceNode.rightChild = removeNode.rightChild;
            }

            /* 삭제할 노드가 루트 노드인 경우 대체할 노드로 바꾼다. */
            if (removeNode == rootNode) {
                rootNode = replaceNode;
            } else if (removeNode == parentOfRemoveNode.rightChild) {
                parentOfRemoveNode.rightChild = replaceNode;
            } else {
                parentOfRemoveNode.leftChild = replaceNode;
            }

            /* 삭제 대상 노드의 왼쪽 자식을 잇는다. */
            replaceNode.leftChild = removeNode.leftChild;
        }

        return true;
    }

    /**
     * 중위 순회
     */
    public void inOrderTree(Node root, int depth) {
        if (root != null) {
            inOrderTree(root.leftChild, depth + 1);
            for (int i = 0; i < depth; i++) {
                System.out.print("ㄴ");
            }
            System.out.println(root.value);
            inOrderTree(root.rightChild, depth + 1);
        }
    }

    /**
     * 후위 순회
     */
    public void postOrderTree(Node root, int depth) {
        if (root != null) {
            postOrderTree(root.leftChild, depth + 1);
            postOrderTree(root.rightChild, depth + 1);
            for (int i = 0; i < depth; i++) {
                System.out.print("ㄴ");
            }
            System.out.println(root.value);
        }
    }

    /**
     * 전위 순회
     */
    public void preOrderTree(Node root, int depth) {
        if (root != null) {
            for (int i = 0; i < depth; i++) {
                System.out.print("ㄴ");
            }
            System.out.println(root.value);
            preOrderTree(root.leftChild, depth + 1);
            preOrderTree(root.rightChild, depth + 1);
        }
    }
}
```
```java
/**
 * 이진탐색 트리 실행
 */
public class BinarySearchTree {
    public static void main(String[] args) {
        BinaryTree tree = new BinaryTree();
        tree.insertNode(5);
        tree.insertNode(8);
        tree.insertNode(7);
        tree.insertNode(10);
        tree.insertNode(9);
        tree.insertNode(11);
        if (tree.removeNode(10)) {
            System.out.println("노드 삭제");
        }
        tree.preOrderTree(tree.rootNode, 0);
    }
}
```