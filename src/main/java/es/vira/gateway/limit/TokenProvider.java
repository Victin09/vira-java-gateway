package es.vira.gateway.limit;

import es.vira.gateway.environment.ApplicationContext;
import es.vira.gateway.environment.FlowLimitsEnvironment;
import lombok.extern.slf4j.Slf4j;

/**
 * token provider
 *
 * @author Víctor Gómez
 * @since 1.3.0
 */
@Slf4j
public class TokenProvider implements Runnable {
    /**
     * application context
     */
    private final ApplicationContext applicationContext;

    public TokenProvider(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void run() {
        FlowLimitsEnvironment env = applicationContext.getContext(FlowLimitsEnvironment.class);
        if (env.isEnable()) {
            log.info("token start production.");
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    env.getTokenBucket().offer();
                    Thread.sleep(env.getRate());
                }
            } catch (InterruptedException e) {
                log.error("token production exception.", e);
                // restart
                run();
            }
        }
    }
}
