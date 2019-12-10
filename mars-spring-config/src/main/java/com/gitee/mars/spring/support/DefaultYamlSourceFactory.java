package com.gitee.mars.spring.support;


import com.gitee.mars.spring.enums.ConfigTypeEnum;
import com.gitee.mars.spring.exception.CreatePropertySourceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.parser.ParserException;

import java.util.*;

/**
 * @author fashionbrot
 * @version 0.1.0
 * @date 2019/12/10 0:06
 */

@Slf4j
public class DefaultYamlSourceFactory implements MarsPropertySourceFactory {


    protected static Yaml createYaml() {
        return new Yaml(new MapAppenderConstructor());
    }

    @Override
    public Properties createPropertySource( String context)throws CreatePropertySourceException {
        final Properties result = new Properties();
        try {
            process(new MatchCallback() {
                @Override
                public void process(Properties properties, Map<String, Object> map) {
                    result.putAll(properties);
                }
            }, createYaml(), context);
        }catch (Exception e){
            throw new CreatePropertySourceException(e);
        }
        return result;
    }

    @Override
    public String sourceType() {
        return ConfigTypeEnum.YAML.getType();
    }


    protected static boolean process(MatchCallback callback, Yaml yaml, String content) {
        int count = 0;
        if (log.isDebugEnabled()) {
            log.debug("Loading from YAML: " + content);
        }
        for (Object object : yaml.loadAll(content)) {
            if (object != null && process(asMap(object), callback)) {
                count++;
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("Loaded " + count + " document" + (count > 1 ? "s" : "") + " from YAML resource: " + content);
        }
        return (count > 0);
    }

    protected static boolean process(Map<String, Object> map, MatchCallback callback) {
        Properties properties = new Properties();
        properties.putAll(getFlattenedMap(map));

        if (log.isDebugEnabled()) {
            log.debug("Merging document (no matchers set): " + map);
        }
        callback.process(properties, map);
        return true;
    }

    @SuppressWarnings("unchecked")
    protected static Map<String, Object> asMap(Object object) {
        // YAML can have numbers as keys
        Map<String, Object> result = new LinkedHashMap();
        if (!(object instanceof Map)) {
            // A document can be a text literal
            result.put("document", object);
            return result;
        }

        Map<Object, Object> map = (Map<Object, Object>) object;
        for (Map.Entry<Object, Object> entry : map.entrySet()) {
            Object key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof Map) {
                value = asMap(value);
            }
            if (key instanceof CharSequence) {
                result.put(key.toString(), value);
            }
            else {
                result.put("[" + key.toString() + "]", value);
            }
        }
        return result;
    }

    protected static Map<String, Object> getFlattenedMap(Map<String, Object> source) {
        Map<String, Object> result = new LinkedHashMap();
        buildFlattenedMap(result, source, null);
        return result;
    }

    protected static void buildFlattenedMap(Map<String, Object> result, Map<String, Object> source, String path) {
        for (Map.Entry<String, Object> entry : source.entrySet()) {
            String key = entry.getKey();
            if (StringUtils.isNotBlank(path)) {
                if (key.startsWith("[")) {
                    key = path + key;
                } else {
                    key = path + '.' + key;
                }
            }
            Object value = entry.getValue();
            if (value instanceof String) {
                result.put(key, value);
            } else if (value instanceof Map) {
                // Need a compound key
                @SuppressWarnings("unchecked")
                Map<String, Object> map = (Map<String, Object>) value;
                buildFlattenedMap(result, map, key);
            } else if (value instanceof Collection) {
                // Need a compound key
                @SuppressWarnings("unchecked")
                Collection<Object> collection = (Collection<Object>) value;
                int count = 0;
                for (Object object : collection) {
                    buildFlattenedMap(result, Collections.singletonMap("[" + (count++) + "]", object), key);
                }
            } else {
                result.put(key, (value != null ? value.toString() : ""));
            }
        }
    }

    protected interface MatchCallback {

        /**
         * Put Map to Properties
         *
         * @param properties {@link Properties}
         * @param map {@link Map}
         */
        void process(Properties properties, Map<String, Object> map);
    }

    protected static class MapAppenderConstructor extends Constructor {

        MapAppenderConstructor() {
            super();
        }

        @Override
        protected Map<Object, Object> constructMapping(MappingNode node) {
            try {
                return super.constructMapping(node);
            } catch (IllegalStateException ex) {
                throw new ParserException("while parsing MappingNode", node.getStartMark(), ex.getMessage(), node.getEndMark());
            }
        }

        @Override
        protected Map<Object, Object> createDefaultMap() {
            final Map<Object, Object> delegate = super.createDefaultMap();
            return new AbstractMap<Object, Object>() {
                @Override
                public Object put(Object key, Object value) {
                    if (delegate.containsKey(key)) {
                        throw new IllegalStateException("Duplicate key: " + key);
                    }
                    return delegate.put(key, value);
                }

                @Override
                public Set<Entry<Object, Object>> entrySet() {
                    return delegate.entrySet();
                }
            };
        }
    }

}
