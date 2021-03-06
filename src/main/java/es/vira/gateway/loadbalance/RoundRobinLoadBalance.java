package es.vira.gateway.loadbalance;

import es.vira.gateway.mapping.Mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * round robin load balance
 *
 * @author Víctor Gómez
 * @since 3.1.2
 */
public class RoundRobinLoadBalance extends UrlMapping {
    /**
     * round robin map
     */
    private final Map<String, List<Mapper>> roundRobinMap;
    /**
     * request index map
     */
    private final Map<String, AtomicInteger> indexMap;

    public RoundRobinLoadBalance(Map<String, List<Mapper>> mapping) {
        super(mapping);
        roundRobinMap = new HashMap<>();
        indexMap = new HashMap<>();
        for (Map.Entry<String, List<Mapper>> entry : mapping.entrySet()) {
            List<Mapper> value = new ArrayList<>();
            int round = 1;
            boolean flag = true;
            while (flag) {
                flag = false;
                for (Mapper m : entry.getValue()) {
                    if (m.getWeight() >= round) {
                        value.add(m);
                        flag = true;
                    }
                }
                round++;
            }
            roundRobinMap.put(entry.getKey(), value);
            indexMap.put(entry.getKey(), new AtomicInteger(-1));
        }
    }

    @Override
    public Mapper getLoadBalance(String name, String host, String ip) {
        if (name != null) {
            List<Mapper> mappers = roundRobinMap.get(name);
            int index = indexMap.get(name).incrementAndGet();
            return mappers.get(Math.abs(index % mappers.size()));
        }
        return null;
    }
}
