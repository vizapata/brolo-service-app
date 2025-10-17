package co.ateunti.brolo.client.service;

import co.ateunti.brolo.client.model.StatusType;

public class StatusScheduler implements Runnable {
    private final PublisherService publisherService;
    private StatusType statusType = StatusType.INITIAL;

    public StatusScheduler(PublisherService publisherService) {
        this.publisherService = publisherService;
    }

    public void run() {
        this.publisherService.sendMessage(statusType);
        statusType = StatusType.PING;
    }
}
