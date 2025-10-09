package co.ateunti.brolo.target.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import static java.util.concurrent.TimeUnit.MINUTES;

@Component
public class StatusScheduler {
    private final KafkaStatusService statusService;
    private static boolean initialStatus = true;

    public StatusScheduler(KafkaStatusService statusService) {
        this.statusService = statusService;
    }

    @Scheduled(fixedDelay = 60, timeUnit = MINUTES, initialDelay = 1)
    public void notifyStatus() {
        this.statusService.notifyStatus(initialStatus);
        markStatusInitiated();
    }

    private static void markStatusInitiated() {
        initialStatus = false;
    }
}
