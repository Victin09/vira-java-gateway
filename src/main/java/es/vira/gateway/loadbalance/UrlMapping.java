package es.vira.gateway.loadbalance;

import es.vira.gateway.environment.MappingEnvironment;
import es.vira.gateway.mapping.Mapper;
import es.vira.gateway.util.MathUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * url mapping
 *
 * @author Víctor Gómez
 * @since 2.1.0
 */
@Slf4j
public class UrlMapping implements MappingEnvironment {
    /**
     * mapping map
     */
    protected Map<String, List<Mapper>> mapping;

    public UrlMapping(Map<String, List<Mapper>> mapping) {
        for (List<Mapper> ms : mapping.values()) {
            simplifyWeight(ms);
        }
        this.mapping = mapping;
    }

    @Override
    public final Map<String, List<Mapper>> get() {
        return mapping;
    }

    /**
     * default method,need override
     */
    @Override
    public Mapper getLoadBalance(String name, String host, String ip) {
        System.out.println("getLoadBalance 1");
        log.warn("USING THE METHOD IS NOT RECOMMENDED, PLEASE OVERRIDE THIS METHOD.");
        List<Mapper> list = mapping.get(host);
        int index = (int) System.currentTimeMillis() % list.size();
        return list.get(index);
    }

    /**
     * simplify mapper weight number
     *
     * @param mappers mapper list
     */
    private void simplifyWeight(List<Mapper> mappers) {
        int len = mappers.size();
        if (len == 0) {
            return;
        } else if (len == 1) {
            mappers.get(0).setWeight(1);
            return;
        }
        Mapper mapper0 = mappers.get(0);
        int tmp = mapper0.getWeight();
        for (int i = 1; i < mappers.size(); ++i) {
            tmp = MathUtils.maxCommonDivisor(tmp, mappers.get(i).getWeight());
        }
        for (Mapper m : mappers) {
            m.setWeight(m.getWeight() / tmp);
        }
    }
}
