package co.ateunti.brolo.client;

import co.ateunti.brolo.client.exception.TargetException;
import co.ateunti.brolo.client.service.PublisherService;
import co.ateunti.brolo.client.service.PublisherServiceFactory;
import co.ateunti.brolo.client.service.StatusScheduler;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static co.ateunti.brolo.client.config.CommandLineIndexes.EXPECTED_ARGUMENTS_SIZE;
import static co.ateunti.brolo.client.config.CommandLineIndexes.INTERVAL;
import static java.util.concurrent.TimeUnit.MINUTES;

public class TargetApp {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(TargetApp.class);

    public static void main(String[] args) {
        validateArgs(args);
        int executionInterval = getIntervalArgument(args);
        String hostname = getHostName();

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        PublisherService publisherService = PublisherServiceFactory.createPublisherService(args, hostname);

        Runnable producerTask = new StatusScheduler(publisherService);

        logger.info("Scheduling service to publish message every {} minutes", executionInterval);
        scheduler.scheduleWithFixedDelay(producerTask, 0, executionInterval, MINUTES);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutdown hook triggered. Shutting down scheduler and Kafka producer...");

            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(0, MINUTES)) {
                    logger.error("Scheduler did not terminate in the specified time.");
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
            }
            logger.info("Shutdown complete 🙂");
        }));

        logger.info("Application started. Press Ctrl+C to exit.");
    }

    private static int getIntervalArgument(String[] args) {
        int period = 1;
        try {
            period = Optional.of(args)
                    .map(x -> x[INTERVAL])
                    .map(Integer::parseInt)
                    .map(Math::abs)
                    .filter(x -> x > 0)
                    .orElse(1);
        } catch (Exception e) {
            logger.warn("Using default of 1 minute interval");
        }
        return period;
    }

    private static void validateArgs(String[] args) {
        if (args == null || args.length != EXPECTED_ARGUMENTS_SIZE) {
            logger.info("Usage: java -jar brolo-service-app.jar <kafka|pubsub> <kafka-host|pubsub-project-id> <topic-name> <publish-interval>");
            throw new TargetException("Please provide parameters to connect with broker");
        }
    }

    private static String getHostName() {
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            return localHost.getHostName();
        } catch (UnknownHostException e) {
            logger.error("Could not determine local host: ", e);
            return "unknown";
        }
    }
}
