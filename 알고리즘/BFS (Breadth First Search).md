# BFS (Breadth-first Search)

BFS는 너비 우선 탐색이라고 부르기도 한다.<br>
기본적으로 그래프 탐색에 사용되며, 가까운 노드부터 우선적으로 탐색하는 알고리즘이다.<br>
BFS는 큐(Queue) 자료구조를 사용해서 구현할 수 있다.

<img width="608" alt="image" src="https://user-images.githubusercontent.com/97447334/236400090-8aec5cdb-1b6f-4411-82a0-d0360d47c168.png">

위와 같은 그래프가 존재하고 노드의 탐색은 1번부터 시작한다고 가정하면
1. 큐에 1번 노드를 넣고 방문 처리(boolean)를 한다.
    1. 여기서 방문처리라는 것은 내가 해당 노드에 방문했음을 기록하는 것
2. 1번 노드와 가까운 노드를 큐에 넣고 방문 처리한다. (2, 3, 8번 노드)
    1. 순서는 상관없다
3. 큐에서 노드를 하나 꺼낸다.
    1. 연결된 노드가 없으면 3번으로 다시 돌아간다.
4. 연결된 노드가 있고 방문하지 않았으면 큐에 넣고 방문처리 후 3번으로 돌아간다.
5. 연결된 노드가 있지만 방문을 이미 한 경우에도 3번으로 돌아간다.
6. 3 ~ 6 과정을 큐가 빌 때까지 반복하며 큐가 비었으면 종료한다.

이러한 순서로 BFS를 진행하면 그래프에서 탐색 순서는 다음과 같다<br>
`1 → 2→ 3→ 6 → 8 → 5→ 4 → 7`
````java
public class BFS {
    public static void main(String[] args) {
        /**
         * 그래프를 2차원 배열로 표현
         * 배열의 인덱스를 노드와 매칭시켜서 사용하기 위해 인덱스 0은 아무것도 저장하지 않는다
         * 1번 인덱스는 1번 노드를 뜻하고 노드의 배열의 값은 연결된 노드들이다.
         * */
        int[][] graph = {{}, {2, 3, 8}, {1, 6, 8}, {1, 5}, {5, 7}, {3, 4, 7}, {2}, {4, 5}, {1, 2}};

        /**
         * 방묹 처리를 위한 boolean 배열 선언*/
        boolean[] visited = new boolean[9];
        System.out.println(bfs(1, graph, visited));
    }

    private static String bfs(int start, int[][] graph, boolean[] visited) {
        //탐색 순서를 출력하기 위한 용도
        StringBuilder sb = new StringBuilder();
        //BFS에 사용할 큐를 생성
        Queue<Integer> q = new LinkedList<Integer>();

        //큐에 BFS를 시작할 노드 번호를 넣어 준다.
        q.offer(start);
        //시작 노드 방문처리
        visited[start] = true;

        //큐가 빌 때까지 반복
        while (!q.isEmpty()) {
            int nodeIndex = q.poll();
            sb.append(nodeIndex + " -> ");
            //큐에서 꺼낸 노드와 연결된 노드들 체크
            for (int i = 0; i < graph[nodeIndex].length; i++) {
                int temp = graph[nodeIndex][i];
                //방문하지 않았으면 방문 처리 후 큐에 넣기
                if (!visited[temp]) {
                    visited[temp] = true;
                    q.offer(temp);
                }
            }
        }
        //탐색 순서 리턴
        return sb.toString();
        
        // 1 -> 2 -> 3 -> 8 -> 6 -> 5 -> 4 -> 7

    }
}
````