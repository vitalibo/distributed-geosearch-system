package com.github.vitalibo.geosearch.processor.infrastructure;

import lombok.Data;

import java.util.Map;

@Data
public class Configuration {

    private String module;
    private String environment;
    private Integer geohashLength;
    private Kafka kafka;

    @Data
    public static class Kafka {

        private String applicationId;
        private String bootstrapServers;
        private String securityProtocol;
        private String schemaRegistryUrl;
        private Integer replicationFactor;
        private Map<String, Object> dynamicConf;
        private Map<String, Object> schemaRegistryDynamicConf;

        private String topicGeoEvent;
        private String topicGeoSearchQuery;
        private String topicGeoSearchResult;

    }

}
