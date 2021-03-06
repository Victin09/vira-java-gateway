package es.vira.gateway.handler;

import es.vira.gateway.environment.ApplicationContext;
import es.vira.gateway.thread.ThreadPoolGroup;
import es.vira.gateway.util.ResponseUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * http handler
 *
 * @author Víctor Gómez
 * @since 1.0.0
 */
@Slf4j
public class HttpHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    /**
     * https or http
     */
    private boolean isHttps;
    /**
     * application context
     */
    private final ApplicationContext applicationContext;

    public HttpHandler(ApplicationContext applicationContext, boolean isHttps) {
        this.isHttps = isHttps;
        this.applicationContext = applicationContext;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest request) {
        String host = request.headers().get(HttpHeaderNames.HOST);
        ByteBuf byteBuf = request.content();
        int len = (int) HttpUtil.getContentLength(request);
        byte[] bytes = null;
        if (len > 0) {
            bytes = new byte[len];
            byteBuf.readBytes(bytes);
            byteBuf.discardReadBytes();
        }
        applicationContext.getContext(ThreadPoolGroup.class).execute(host,
                new RequestHandler(channelHandlerContext.channel(), request, request.uri(), host, isHttps, bytes, applicationContext));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("Server catch exception.", cause);
        ctx.channel().writeAndFlush(ResponseUtils.buildFailResponse(HttpResponseStatus.INTERNAL_SERVER_ERROR))
                .addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        // connection timeout,close channel
        ctx.close();
    }
}
