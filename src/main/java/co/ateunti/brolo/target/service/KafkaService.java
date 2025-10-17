package co.ateunti.brolo.target.service;

import co.ateunti.brolo.target.model.StatusInfo;
import co.ateunti.brolo.target.model.StatusType;
import co.ateunti.brolo.target.serializer.StatusInfoSerializer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.logging.Logger;

import static co.ateunti.brolo.target.config.Topics.EVENT_TOPIC;

public class KafkaService {
    private final Properties properties;
    private static final Logger logger = Logger.getLogger(KafkaService.class.getSimpleName());

    public KafkaService(String host) {
        properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, host);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StatusInfoSerializer.class.getName());
    }

    public void sendMessage(StatusType type) {
        var message = new StatusInfo(type);

        try {
            KafkaProducer<String, StatusInfo> producer = new KafkaProducer<>(properties);
            ProducerRecord<String, StatusInfo> producerRecord = new ProducerRecord<>(EVENT_TOPIC, message);
            try {
                producer.send(producerRecord, (metadata, exception) -> {
                    if (exception == null) {
                        logger.info("Successfully sent message!");
                        logger.info("Topic: " + metadata.topic());
                        logger.info("Partition: " + metadata.partition());
                        logger.info("Offset: " + metadata.offset());
                        logger.info("Timestamp: " + metadata.timestamp());
                    } else {
                        logger.severe("Error while producing message: " + exception.getMessage());
                    }
                });

            } finally {
                producer.flush();
                producer.close();
            }
        } catch (Exception e) {
            logger.severe("Error while trying to connect to broker: " + e.getMessage());
        }
    }
}