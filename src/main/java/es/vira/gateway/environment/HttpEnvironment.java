package es.vira.gateway.environment;

import es.vira.gateway.data.HttpConfig;
import lombok.Getter;

/**
 * http environment
 *
 * @author Víctor Gómez
 * @since 1.0.0
 */
public class HttpEnvironment {
    /**
     * port
     */
    @Getter
    private int port;
    /**
     * redirect https or not
     */
    @Getter
    private boolean redirectHttps;

    public HttpEnvironment(ConfigEnvironment config) {
        HttpConfig obj = config.getConfig().getHttp();
        if (obj != null) {
            port = obj.getPort();
            redirectHttps = obj.isRedirectHttps();
        }
    }
}
