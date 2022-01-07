package es.vira.gateway.environment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.vira.gateway.constant.SystemConstant;
import es.vira.gateway.data.Config;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * config environment
 *
 * @author Víctor Gómez
 * @since 1.0.0
 */
@Slf4j
public class ConfigEnvironment {
    /**
     * config json map
     */
    @Getter
    private Config config;
    /**
     * develop environment or not
     */
    @Getter
    private boolean isDevelop;

    public ConfigEnvironment(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Config config = null;
            config = mapper.readValue(json, Config.class);
            this.config = config;
            this.isDevelop = config != null && config.getMode().equals(SystemConstant.DEVELOP);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
