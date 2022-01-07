package es.vira.gateway.environment;

import es.vira.gateway.data.StaticConfig;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * local file environment
 *
 * @author Víctor Gómez
 * @since 3.1.1
 */
public class LocalFileEnvironment {
    /**
     * file path mapping map
     */
    @Getter
    private Map<String, String> pathMapping;

    public LocalFileEnvironment(ConfigEnvironment config) {
        StaticConfig obj = config.getConfig().getStaticContent();
        if (obj != null) {
            pathMapping = new HashMap<>();
            pathMapping.put(obj.getUrl(), obj.getPath());
        }
        for (Map.Entry<String, String> e: pathMapping.entrySet()) {
            System.out.println("key: " + e.getKey() + " value: " + e.getValue());
        }
    }

    /**
     * get local file path
     *
     * @param host host
     * @return file path
     */
    public String getPath(String host) {
        return pathMapping.get(host);
    }
}
