package co.ateunti.brolo.client.service;

import co.ateunti.brolo.client.model.StatusInfo;
import co.ateunti.brolo.client.model.StatusType;
import co.ateunti.brolo.client.serializer.StatusInfoSerializer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class KafkaService implements PublisherService {
    private final Properties properties;
    private final String topic;
    private final String hostname;
    private static final Logger logger = LoggerFactory.getLogger(KafkaService.class);

    public KafkaService(String host, String topic, String hostname) {
        this.topic = topic;
        this.hostname = hostname;
        properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, host);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StatusInfoSerializer.class.getName());
    }

    @Override
    public void sendMessage(StatusType type) {
        var message = new StatusInfo(hostname, type);

        try (KafkaProducer<String, StatusInfo> producer = new KafkaProducer<>(properties)) {
            ProducerRecord<String, StatusInfo> producerRecord = new ProducerRecord<>(topic, message);
            try {
                producer.send(producerRecord, (metadata, exception) -> {
                    if (exception == null) {
                        logger.info("Successfully sent message!");
                        logger.info("Topic: {}", metadata.topic());
                        logger.info("Partition: {}", metadata.partition());
                        logger.info("Offset: {}", metadata.offset());
                        logger.info("Timestamp: {}", metadata.timestamp());
                    } else {
                        logger.error("Error while producing message: ", exception);
                    }
                });

            } finally {
                producer.flush();
            }
        } catch (Exception e) {
            logger.error("Error while trying to connect to broker: ", e);
        }
    }
}