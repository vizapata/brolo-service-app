package co.ateunti.brolo.target;

import co.ateunti.brolo.target.service.KafkaService;
import co.ateunti.brolo.target.service.StatusScheduler;

import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Logger;

import static java.util.concurrent.TimeUnit.MINUTES;

public class TargetApp {
    private static final Logger logger = Logger.getLogger(TargetApp.class.getSimpleName());

    public static void main(String[] args) {
        validateArgs(args);
        int executionInterval = parsePeriod(args);
        String host = parseHost(args);

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        Runnable producerTask = new StatusScheduler(new KafkaService(host));

        logger.info("Scheduling Kafka (" + host + ") to run every " + executionInterval + " minutes");
        scheduler.scheduleWithFixedDelay(producerTask, 0, executionInterval, MINUTES);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutdown hook triggered. Shutting down scheduler and Kafka producer...");

            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(0, MINUTES)) {
                    logger.severe("Scheduler did not terminate in the specified time.");
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
            }
            logger.info("Shutdown complete 🙂");
        }));

        logger.info("Application started. Press Ctrl+C to exit.");
    }

    private static int parsePeriod(String[] args) {
        int period = 1;
        try {
            period = Optional.of(args)
                    .map(x -> x[1])
                    .map(Integer::parseInt)
                    .map(Math::abs)
                    .filter(x -> x > 0)
                    .orElse(1);
        } catch (Exception e) {
            logger.warning("Unable to parse date format. Using default of 1 minute");
        }
        return period;
    }

    private static void validateArgs(String[] args) {
        if (args == null || args.length < 2) {
            throw new RuntimeException("Please provide parameters to connect with broker");
        }
    }

    private static String parseHost(String[] args) {
        return args[0];
    }
}
