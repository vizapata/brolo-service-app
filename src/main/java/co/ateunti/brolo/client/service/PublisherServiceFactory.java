package co.ateunti.brolo.client.service;

import co.ateunti.brolo.client.exception.TargetException;

import static co.ateunti.brolo.client.config.CommandLineIndexes.HOST;
import static co.ateunti.brolo.client.config.CommandLineIndexes.SERVICE;
import static co.ateunti.brolo.client.config.CommandLineIndexes.TOPIC;

public class PublisherServiceFactory {

    public static PublisherService createPublisherService(String[] args, String hostname) {
        return switch (args[SERVICE]) {
            case "kafka" -> new KafkaService(args[HOST], args[TOPIC], hostname);
            case "pubsub" -> new PubSubService(args[HOST], args[TOPIC], hostname);
            default -> throw new TargetException("Unknown service " + args[SERVICE]);
        };
    }
}
