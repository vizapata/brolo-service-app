package co.ateunti.brolo.target.service;

import co.ateunti.brolo.target.model.StatusInfo;
import co.ateunti.brolo.target.model.StatusType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static co.ateunti.brolo.target.config.Topics.EVENT_TOPIC;

@Service
public class KafkaStatusService {
    private final KafkaTemplate<String, StatusInfo> kafkaTemplate;

    public KafkaStatusService(KafkaTemplate<String, StatusInfo> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendStatus(StatusType type) {
        var status = new StatusInfo(type);
        kafkaTemplate.send(EVENT_TOPIC, status);
    }
}
