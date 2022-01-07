package es.vira.gateway.environment;

import es.vira.gateway.data.HttpsConfig;
import lombok.Getter;

/**
 * https environment
 *
 * @author Víctor Gómez
 * @since 1.0.0
 */
public class HttpsEnvironment {
    /**
     * enable
     */
    @Getter
    private boolean enable;
    /**
     * port
     */
    @Getter
    private int port;
    /**
     * certificate password
     */
    @Getter
    private String keyPwd;
    /**
     * certificate file path
     */
    @Getter
    private String keyPath;

    public HttpsEnvironment(ConfigEnvironment config) {
        HttpsConfig obj = config.getConfig().getHttps();
        if (obj == null) {
            enable = false;
        } else {
            enable = obj.isEnabled();
            port = obj.getPort();
            keyPwd = obj.getKeyPwd();
            keyPath = obj.getKeyPath();
        }
    }
}
