package co.ateunti.brolo.target.service;

import co.ateunti.brolo.target.model.StatusType;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Log4j2
@RequiredArgsConstructor
public class StatusScheduler {
    private final KafkaStatusService statusService;

    @PostConstruct
    void init() {
        this.statusService.sendStatus(StatusType.INITIAL);
    }

    @Scheduled(fixedDelay = 15)
    public void notifyStatus() {
        this.statusService.sendStatus(StatusType.PING);
    }
}
