package co.ateunti.brolo.target.service;

import co.ateunti.brolo.target.model.StatusType;
import jakarta.annotation.PostConstruct;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class StatusScheduler {
    private final KafkaStatusService statusService;

    public StatusScheduler(KafkaStatusService statusService) {
        this.statusService = statusService;
    }

    @PostConstruct
    void init() {
        this.statusService.sendStatus(StatusType.INITIAL);
    }

    @Scheduled(fixedDelay = 15)
    public void notifyStatus() {
        this.statusService.sendStatus(StatusType.PING);
    }
}
