package es.vira.gateway.thread;

import es.vira.gateway.handler.RequestHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * default thread pool reject handler
 *
 * @author Víctor Gómez
 * @since 1.7.2
 */
@Slf4j
public class DefaultRejectedExecutionHandler implements RejectedExecutionHandler {
    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        if (r instanceof RequestHandler) {
            ((RequestHandler) r).release(true);
        }
        log.warn("MISSION REJECTED.", executor);
    }
}
