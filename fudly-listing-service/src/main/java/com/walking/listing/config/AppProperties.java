package com.walking.listing.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private Outbox outbox = new Outbox();

    @Getter
    @Setter
    public static class Outbox {
        private String topic;
        private int batchSize;
        private long fixedDelayMs;
        private long requeueDelayMs;
        private int leaseTimeMinutes;
    }
}
