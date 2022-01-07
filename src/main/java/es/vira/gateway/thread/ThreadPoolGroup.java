package es.vira.gateway.thread;

import es.vira.gateway.data.ThreadPoolConfig;
import es.vira.gateway.environment.ConfigEnvironment;
import es.vira.gateway.environment.MappingEnvironment;
import es.vira.gateway.environment.LocalFileEnvironment;
import es.vira.gateway.handler.RequestHandler;
import es.vira.gateway.mapping.Mapper;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * thread pool group management
 *
 * @author Víctor Gómez
 * @since 1.0.0
 */
@Slf4j
public class ThreadPoolGroup {
    /**
     * thread pool group
     */
    private Map<String, ThreadPoolExecutor> threadPoolMap;

    /**
     * execute mission in mapping thread pool
     *
     * @param host host
     * @param r    mission
     */
    public void execute(String host, RequestHandler r) {
        ThreadPoolExecutor executor = threadPoolMap.get(host);
        if (executor == null) {
            r.release(true);
            log.warn("{} IS NOT FOUND.", host);
            return;
        }
        executor.execute(r);
    }

    /**
     * shutdown thread pool
     */
    public void shutdown() {
        for (ThreadPoolExecutor t : threadPoolMap.values()) {
            t.shutdown();
        }
    }

    public ThreadPoolGroup(ConfigEnvironment config, MappingEnvironment mappingEnv, LocalFileEnvironment staticEnv) {
//        JSONObject obj = JSON.parseObject(config.getChild("threadPool"));
        ThreadPoolConfig obj = config.getConfig().getThreadPool();
        if (obj != null) {
            int core = obj.getCore();
            int max = obj.getMax();
            int timeout = obj.getTimeout();
            Map<String, List<Mapper>> mappingMap = mappingEnv.get();
            Map<String, String> pathMap = staticEnv.getPathMapping();
            threadPoolMap = new HashMap<>((mappingMap.size() + pathMap.size()) / 3 * 4);
            for (String key : mappingMap.keySet()) {
                threadPoolMap.put(key, new ThreadPoolExecutor(core, max, timeout, TimeUnit.MILLISECONDS,
                        new ArrayBlockingQueue<>(core >> 1), new DefaultThreadFactory(key),
                        new DefaultRejectedExecutionHandler()));
            }
            for (String key : pathMap.keySet()) {
                if (!threadPoolMap.containsKey(key)) {
                    threadPoolMap.put(key, new ThreadPoolExecutor(core, max, timeout, TimeUnit.MILLISECONDS,
                            new ArrayBlockingQueue<>(core >> 1), new DefaultThreadFactory(key),
                            new DefaultRejectedExecutionHandler()));
                }
            }
        }
    }
}
