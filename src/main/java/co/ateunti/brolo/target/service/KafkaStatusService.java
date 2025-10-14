package co.ateunti.brolo.target.service;

import co.ateunti.brolo.target.model.StatusInfo;
import co.ateunti.brolo.target.model.StatusType;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static co.ateunti.brolo.target.config.Topics.EVENT_TOPIC;

@Service
@Log4j2
public class KafkaStatusService {
    private final KafkaTemplate<String, StatusInfo> kafkaTemplate;

    public KafkaStatusService(KafkaTemplate<String, StatusInfo> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendStatus(StatusType type) {
        var status = new StatusInfo(type);
        kafkaTemplate.send(EVENT_TOPIC, status)
                .thenAccept(result -> log.info("Status notified {}", result.getProducerRecord().value()));
    }
}
