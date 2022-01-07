package es.vira.gateway.environment;

import es.vira.gateway.data.FlowLimitsConfig;
import es.vira.gateway.limit.TokenBucket;
import lombok.Getter;

/**
 * flow limits
 *
 * @author Víctor Gómez
 * @since 1.3.0
 */
public class FlowLimitsEnvironment {
    /**
     * enable
     */
    @Getter
    private final boolean enable;
    /**
     * token provide rate
     */
    @Getter
    private long rate;
    /**
     * token bucket
     */
    @Getter
    private TokenBucket tokenBucket;

    public FlowLimitsEnvironment(ConfigEnvironment config) {
//        JSONObject obj = JSON.parseObject(config.getChild("flowLimits"));
        FlowLimitsConfig obj = config.getConfig().getFlowLimits();
        if (obj == null) {
            enable = false;
        } else {
            enable = obj.isEnabled();
            rate = obj.getRate();
            tokenBucket = new TokenBucket(obj.getMaxSize());
        }
    }
}
