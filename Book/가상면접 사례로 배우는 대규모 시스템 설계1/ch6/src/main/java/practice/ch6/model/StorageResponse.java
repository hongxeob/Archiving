package practice.ch6.model;

import java.io.Serializable;

/**
 * 스토리지 작업에 대한 응답을 캡슐화하는 클래스
 * 제네릭 타입 T를 사용하여 다양한 유형의 데이터를 포함할 수 있음
 *
 * @param <T> 응답 데이터의 타입
 */
public class StorageResponse<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    // 작업 성공 여부
    private boolean success;

    // 응답 메시지 (오류 설명 등)
    private String message;

    // 응답 데이터
    private T data;

    // 작업 수행 시간 (밀리초)
    private Long executionTimeMs;

    // 노드 ID (어떤 노드에서 응답했는지)
    private String nodeId;

    /**
     * 기본 생성자
     */
    public StorageResponse() {
    }

    /**
     * 기본 생성자
     *
     * @param success 작업 성공 여부
     * @param message 응답 메시지
     * @param data    응답 데이터
     */
    public StorageResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    /**
     * 모든 필드를  포함한 생성자
     *
     * @param success         작업 성공 여부
     * @param message         응답 메시지
     * @param data            응답 데이터
     * @param executionTimeMs 작업 수행 시간 (밀리초)
     * @param nodeId          응답 노드 ID
     */
    public StorageResponse(boolean success, String message, T data, Long executionTimeMs, String nodeId) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.executionTimeMs = executionTimeMs;
        this.nodeId = nodeId;
    }

    /**
     * 성공 응답을 생성하는 팩토리 메서드
     *
     * @param data 응답 데이터
     * @return 성공 응답 객체
     */
    public static <T> StorageResponse<T> success(T data) {
        return new StorageResponse<>(true, "Operation successful", data);
    }

    /**
     * 메시지를 포함한 성공 응답을 생성하는 팩토리 메서드
     *
     * @param data    응답 데이터
     * @param message 성공 메시지
     * @return 성공 응답 객체
     */
    public static <T> StorageResponse<T> success(T data, String message) {
        return new StorageResponse<>(true, message, data);
    }

    /**
     * 상세 정보를 포함한 성공 응답을 생성하는 팩토리 메서드
     *
     * @param data            응답 데이터
     * @param message         성공 메시지
     * @param executionTimeMs 작업 수행 시간 (밀리초)
     * @param nodeId          응답 노드 ID
     * @return 성공 응답 객체
     */
    public static <T> StorageResponse<T> success(T data, String message, Long executionTimeMs, String nodeId) {
        return new StorageResponse<>(true, message, data, executionTimeMs, nodeId);
    }

    /**
     * 실패 응답을 생성하는 팩토리 메서드
     *
     * @param message 실패 메시지
     * @return 실패 응답 객체
     */
    public static <T> StorageResponse<T> failure(String message) {
        return new StorageResponse<>(false, message, null);
    }

    /**
     * 상세 정보를 포함한 실패 응답을 생성하는 팩토리 메서드
     *
     * @param message 실패 메시지
     * @param nodeId  응답 노드 ID
     * @return 실패 응답 객체
     */
    public static <T> StorageResponse<T> failure(String message, String nodeId) {
        return new StorageResponse<>(false, message, null, null, nodeId);
    }

    /**
     * 예외로부터 실패 응답을 생성하는 팩토리 메서드
     *
     * @param exception 발생한 예외
     * @return 실패 응답 객체
     */
    public static <T> StorageResponse<T> failure(Exception exception) {
        return new StorageResponse<>(false, exception.getMessage(), null);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Long getExecutionTimeMs() {
        return executionTimeMs;
    }

    public void setExecutionTimeMs(Long executionTimeMs) {
        this.executionTimeMs = executionTimeMs;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    @Override
    public String toString() {
        return "StorageResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", executionTimeMs=" + executionTimeMs +
                ", nodeId='" + nodeId + '\'' +
                '}';
    }
}
