package co.ateunti.brolo.target.service;

import co.ateunti.brolo.target.model.StatusType;

public class StatusScheduler implements Runnable {
    private final KafkaService kafkaService;

    public StatusScheduler(KafkaService kafkaService) {
        this.kafkaService = kafkaService;
    }

    public void run() {
        this.kafkaService.sendMessage(StatusType.PING);
    }
}
