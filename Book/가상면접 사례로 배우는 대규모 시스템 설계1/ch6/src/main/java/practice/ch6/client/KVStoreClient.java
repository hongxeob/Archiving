package practice.ch6.client;

public interface KVStoreClient {

    /**
     * @param key 조회할 키
     * @return 키에 해당하는 값, 키가 존재하지 않으면 null 반환
     */
    byte[] get(String key);

    /**
     * @param key   저장할 키
     * @param value 저장할 값
     * @return 작업 성공 여부
     */
    boolean put(String key, byte[] value);

    /**
     * @param key        저장할 키
     * @param value      저장할 값
     * @param ttlSeconds 값의 유효기간 (초)
     * @return 작업 성공 여부
     */
    boolean put(String key, byte[] value, int ttlSeconds);
}
