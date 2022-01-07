package es.vira.gateway.environment;

import es.vira.gateway.data.CorsConfig;
import lombok.Getter;

import java.util.List;

/**
 * cors config
 *
 * @author Víctor Gómez
 * @since 1.2.0
 */
public class CorsEnvironment {
    /**
     * enable
     */
    private final boolean enable;
    /**
     * white list
     */
    private List<String> whiteList;
    /**
     * allow methods
     */
    @Getter
    private List<String> allowMethods;

    public CorsEnvironment(ConfigEnvironment config) {
//        JSONObject obj = JSON.parseObject(config.getChild("cors"));
        CorsConfig obj = config.getConfig().getCors();
        if (obj == null) {
            enable = false;
        } else {
            enable = obj.isEnabled();
            allowMethods = obj.getAllowMethods();
//            JSONArray array = obj.getJSONArray("whiteList");
            whiteList = obj.getWhiteList();
//            Set<String> whiteList = new TreeSet<>();
//            for (int i = 0; i < array.size(); i++) {
//                whiteList.add(array.getString(i));
//            }
//            this.whiteList = whiteList;
        }
    }

    /**
     * check for origin legal
     *
     * @param origin origin host
     * @return legal or not
     */
    public boolean isLegal(String origin) {
        return enable && (whiteList.size() <= 0 || whiteList.contains(origin));
    }
}
