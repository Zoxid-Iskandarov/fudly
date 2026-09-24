package com.walking.inventory.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    private Kafka kafka = new Kafka();
    private Reservation reservation = new Reservation();

    @Getter
    @Setter
    public static class Kafka {
        private int partitions;
        private int replicas;
        private Topics topics = new Topics();
        private Outbox outbox = new Outbox();
        private Consumers consumers = new Consumers();

        @Getter
        @Setter
        public static class Topics {
            private String inventory;
            private String listing;
        }

        @Getter
        @Setter
        public static class Outbox {
            private int batchSize;
            private long fixedDelayMs;
            private long requeueDelayMs;
            private int leaseTimeMinutes;
        }

        @Getter
        @Setter
        public static class Consumers {
            private String listingStatusSync;
        }
    }

    @Getter
    @Setter
    public static class Reservation {
        private long ttlMinutes;
    }
}
