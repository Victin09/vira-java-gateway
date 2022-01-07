package es.vira.gateway.environment;

import es.vira.gateway.mapping.Mapper;

import java.util.List;
import java.util.Map;

/**
 * mapping environment
 *
 * @author Víctor Gómez
 * @since 2.1.0
 */
public interface MappingEnvironment {
    /**
     * get mapping map
     *
     * @return mapping map
     */
    Map<String, List<Mapper>> get();

    /**
     * get proxy mapper by load balance
     *
     * @param host host
     * @param ip   ip
     * @return mapper
     */
    Mapper getLoadBalance(String name, String host, String ip);
}
