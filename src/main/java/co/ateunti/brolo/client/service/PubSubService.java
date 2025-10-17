package co.ateunti.brolo.client.service;

import co.ateunti.brolo.client.exception.TargetException;
import co.ateunti.brolo.client.model.StatusInfo;
import co.ateunti.brolo.client.model.StatusType;
import co.ateunti.brolo.client.serializer.StatusInfoSerializer;
import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.api.gax.rpc.ApiException;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.TopicName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class PubSubService implements PublisherService {
    private static final Logger logger = LoggerFactory.getLogger(PubSubService.class);
    private final String projectId;
    private final String topic;
    private final String hostname;
    private final StatusInfoSerializer serializer = new StatusInfoSerializer();

    public PubSubService(String projectId, String topic, String hostname) {
        this.projectId = projectId;
        this.topic = topic;
        this.hostname = hostname;
    }

    @Override
    public void sendMessage(StatusType type) {
        TopicName topicName = TopicName.of(projectId, topic);
        Publisher publisher = null;
        var message = new StatusInfo(hostname, type);
        var serializedMessage = serializer.serialize(topic, message);

        try {
            publisher = Publisher.newBuilder(topicName).build();
            ByteString data = ByteString.copyFrom(serializedMessage);
            PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(data).build();

            ApiFuture<String> future = publisher.publish(pubsubMessage);
            ApiFutures.addCallback(
                    future,
                    new ApiFutureCallback<>() {

                        @Override
                        public void onFailure(Throwable throwable) {
                            if (throwable instanceof ApiException apiException) {
                                logger.info(apiException.getStatusCode().getCode().name());
                                logger.info("{}", apiException.isRetryable());
                            }
                            logger.info("Error publishing message : {}", message);
                        }

                        @Override
                        public void onSuccess(String messageId) {
                            logger.info("Published message ID: {}", messageId);
                        }
                    },
                    MoreExecutors.directExecutor());
        } catch (IOException e) {
            throw new TargetException(e.getMessage());
        } finally {
            if (publisher != null) {
                publisher.shutdown();
                try {
                    publisher.awaitTermination(1, TimeUnit.MINUTES);
                } catch (InterruptedException e) {
                    logger.error("Unable to terminate publisher", e);
                }
            }
        }
    }
}
