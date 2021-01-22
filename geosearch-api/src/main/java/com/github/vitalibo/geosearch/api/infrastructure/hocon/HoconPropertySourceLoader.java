package com.github.vitalibo.geosearch.api.infrastructure.hocon;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import lombok.Getter;
import org.springframework.boot.env.PropertySourceLoader;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class HoconPropertySourceLoader implements PropertySourceLoader {

    @Getter
    private final String[] fileExtensions = {"hocon", "conf"};

    @Override
    public List<PropertySource<?>> load(String name, Resource resource) throws IOException {
        Config config = ConfigFactory.parseURL(resource.getURL()).resolve();
        Map<String, Object> properties = flattenMap(config.root().unwrapped());
        if (properties.isEmpty()) {
            return Collections.emptyList();
        }

        return Collections.singletonList(new MapPropertySource(name, properties));
    }

    private static Map<String, Object> flattenMap(Map<String, Object> config) {
        return flattenMap("", config)
            .collect(HashMap::new, (m, v) -> m.put(v.getKey(), v.getValue()), HashMap::putAll);
    }

    private static Stream<Map.Entry<String, Object>> flattenMap(String root, Map<String, Object> config) {
        return config.entrySet().stream()
            .flatMap(entry -> flattenMap(root + entry.getKey(), entry.getValue()));
    }

    @SuppressWarnings("unchecked")
    private static Stream<Map.Entry<String, Object>> flattenMap(String key, Object value) {
        if (value instanceof Map) {
            return flattenMap(key + ".", (Map<String, Object>) value);
        } else if (value instanceof Collection) {
            AtomicInteger index = new AtomicInteger(0);
            return ((Collection<Object>) value).stream()
                .flatMap(o -> flattenMap(key, Collections.singletonMap("[" + (index.getAndIncrement()) + "]", o)));
        }

        return Stream.of(new AbstractMap.SimpleEntry<>(key, value));
    }

}
