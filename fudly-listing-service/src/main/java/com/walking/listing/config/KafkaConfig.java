package com.walking.listing.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic listingEventsTopic(AppProperties appProperties) {
        return TopicBuilder.name(appProperties.getKafka().getTopics().getListing())
                .partitions(3)
                .replicas(1)
                .build();
    }
}
