package es.vira.gateway.util;

import es.vira.gateway.data.MappingConfig;
import es.vira.gateway.data.MappingUrlConfig;
import es.vira.gateway.environment.ConfigEnvironment;
import es.vira.gateway.environment.MappingEnvironment;
import es.vira.gateway.loadbalance.RandomLoadBalance;
import es.vira.gateway.loadbalance.UrlMapping;
import es.vira.gateway.mapping.Mapper;

import java.lang.reflect.Constructor;
import java.util.*;

/**
 * application context utils
 *
 * @author Víctor Gómez
 * @since 3.0.2
 */
public class ContextUtils {
    /**
     * build mapping environment
     *
     * @param config json config
     * @return mapping environment
     * @throws Exception build error
     */
    public static MappingEnvironment buildMappingEnvironment(ConfigEnvironment config) throws Exception {
//        JSONObject mappingObj = JSON.parseObject(config.getChild("mapping"));
        MappingConfig mappingObj = config.getConfig().getMapping();
//        JSONObject obj = mappingObj.getJSONObject("list");
        List<MappingUrlConfig> obj = mappingObj.getMaps();
        Map<String, List<Mapper>> map = new HashMap<>(obj.size() / 3 * 4);
        List<Mapper> urls = new ArrayList<>();
        for (MappingUrlConfig entry : obj) {
//            JSONArray arr = (JSONArray) entry.getValue();
//            int len = arr.size();
//            for (int i = 0; i < len; i++) {
//                JSONObject object = arr.getJSONObject(i);
                urls.add(new Mapper(entry.getName(), entry.getUrl(), entry.getWeight()));
//            }
            map.put(entry.getName(), urls);
        }

        String className = mappingObj.getMode();
        if (className == null) {
            return new RandomLoadBalance(map);
        }
        Class<? extends UrlMapping> clazz = Class.forName(className).asSubclass(UrlMapping.class);
        Constructor<? extends UrlMapping> constructor = clazz.getDeclaredConstructor(Map.class);
        constructor.setAccessible(true);
        return constructor.newInstance(map);
    }
}
