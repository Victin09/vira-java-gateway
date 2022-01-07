package es.vira.gateway.filter;

import io.netty.handler.codec.http.FullHttpResponse;

/**
 * post filter
 *
 * @author Víctor Gómez
 * @since 2.2.0
 */
public interface AfterFilter {
    /**
     * filter
     *
     * @param response response
     */
    void filter(FullHttpResponse response);
}
