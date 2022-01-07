package es.vira.gateway.environment;

import es.vira.gateway.data.FilterConfig;
import es.vira.gateway.filter.AfterFilter;
import es.vira.gateway.filter.BeforeFilter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * filter environment
 *
 * @author Víctor Gómez
 * @since 2.2.0
 */
public class FilterEnvironment {
    /**
     * pre filter list
     */
    private List<BeforeFilter> beforeFilters;
    /**
     * post filter list
     */
    private List<AfterFilter> afterFilters;

    public FilterEnvironment(ConfigEnvironment config) {
//        JSONObject obj = JSON.parseObject(config.get);
        try {
            FilterConfig obj = config.getConfig().getFilter();
            if (obj != null) {
                beforeFilters = new ArrayList<>();
    //            JSONArray arr = obj.getJSONArray("before");
                List<String> arr = obj.getBefore();
                if (arr != null) {
                    for (String className : arr) {
                        Class<?> clazz = Class.forName(className);
                            beforeFilters.add((BeforeFilter) clazz.getDeclaredConstructor().newInstance());
                    }
                }
//                arr = obj.getJSONArray("after");
                arr = obj.getAfter();
                if (arr != null) {
                    afterFilters = new ArrayList<>();
                    for (String className : arr) {
//                        String className = arr.getString(i);
                        Class<?> clazz = Class.forName(className);
                        afterFilters.add((AfterFilter) clazz.getDeclaredConstructor().newInstance());
                    }
                }
            }
        } catch (InvocationTargetException | ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * pre filters
     *
     * @param request request
     * @param content request context
     * @return continue or not
     */
    public boolean beforeFilter(FullHttpRequest request, byte[] content) {
        for (BeforeFilter filter : beforeFilters) {
            boolean isContinue = filter.filter(request, content);
            if (!isContinue) {
                return false;
            }
        }
        return true;
    }

    /**
     * post filters
     *
     * @param response response
     */
    public void afterFilter(FullHttpResponse response) {
        for (AfterFilter filter : afterFilters) {
            filter.filter(response);
        }
    }
}
