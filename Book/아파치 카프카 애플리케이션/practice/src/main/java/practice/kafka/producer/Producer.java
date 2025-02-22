package practice.kafka.producer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

@Slf4j
public class Producer {
    private final static String TOPIC_NAME = "test";
    private final static String BOOTSTRAP_SERVER = "localhost:9092";

    public static void main(String[] args) {
        Properties configs = new Properties();

        configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        KafkaProducer<String, String> producer = new KafkaProducer<>(configs);

        String testMessage = "testMessage";
        ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC_NAME, testMessage);
        producer.send(record);
        log.info("{}", record);
        producer.flush();
        producer.close();

    }
}
