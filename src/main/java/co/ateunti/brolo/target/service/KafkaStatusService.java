package co.ateunti.brolo.target.service;

import co.ateunti.brolo.target.model.StatusInfo;
import org.springframework.stereotype.Service;

@Service
public class KafkaStatusService {
    public void notifyStatus(boolean isInitial) {
        // TODO: Use kafka topic to publish the message
        // up is always true from this service
        var status = new StatusInfo(true, isInitial);
    }
}
