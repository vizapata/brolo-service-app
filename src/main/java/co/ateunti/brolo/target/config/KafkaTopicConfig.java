package co.ateunti.brolo.target.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import static co.ateunti.brolo.target.config.Topics.EVENT_TOPIC;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic statusTopic() {
        return TopicBuilder.name(EVENT_TOPIC).build();
    }
}