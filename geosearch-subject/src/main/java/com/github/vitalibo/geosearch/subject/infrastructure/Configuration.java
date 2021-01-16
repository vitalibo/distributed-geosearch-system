package com.github.vitalibo.geosearch.subject.infrastructure;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class Configuration {

    private String module;
    private String environment;
    private Source source;
    private Channel channel;

    @Data
    public static class Source {

        private Random random;
        private Blitzortung blitzortung;

    }

    @Data
    public static class Random {

        private Integer backOffSleepIntervalMillis;
        private Double probability;

    }

    @Data
    public static class Blitzortung {

        private String hostUriPattern;
        private List<String> hosts;
        private Integer port;
        private Integer minRunningTimeToReconnect;

    }

    @Data
    public static class Channel {

        private Kafka kafka;

    }

    @Data
    public static class Kafka {

        private String bootstrapServers;
        private String securityProtocol;
        private String acknowledgments;
        private String schemaRegistryUrl;
        private String keySerializerClass;
        private String valueSerializerClass;
        private Map<String, Object> dynamicConf;

        private String topicGeoEvent;

    }

}
