package es.vira.gateway.listener;

import es.vira.gateway.constant.SystemConstant;
import es.vira.gateway.constant.HttpConstant;
import es.vira.gateway.environment.ApplicationContext;
import es.vira.gateway.environment.HttpEnvironment;
import es.vira.gateway.handler.HttpHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * Http listener
 *
 * @author Víctor Gómez
 * @since 1.0.0
 */
@Slf4j
public class HttpListener implements Runnable {
    /**
     * application context
     */
    private final ApplicationContext applicationContext;
    /**
     * boss thread pool
     */
    private final EventLoopGroup boss;
    /**
     * worker thread pool
     */
    private final EventLoopGroup worker;
    /**
     * channel mode
     */
    private final Class<? extends ServerChannel> channelClass;

    public HttpListener(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        if (SystemConstant.LINUX.equals(System.getProperty(SystemConstant.OS_NAME))) {
            boss = new EpollEventLoopGroup();
            worker = new EpollEventLoopGroup();
            channelClass = EpollServerSocketChannel.class;
            log.info("Http listener used epoll model");
        } else {
            boss = new NioEventLoopGroup();
            worker = new NioEventLoopGroup();
            channelClass = NioServerSocketChannel.class;
            log.info("Http listener used nio model");
        }
    }

    /**
     * shutdown
     */
    public void shutdown() {
        boss.shutdownGracefully();
        worker.shutdownGracefully();
    }

    @Override
    public void run() {
        HttpEnvironment env = applicationContext.getContext(HttpEnvironment.class);
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(boss, worker);
        bootstrap.channel(channelClass);
        bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
        bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) {
                ChannelPipeline p = socketChannel.pipeline();
                p.addLast(new HttpResponseEncoder());
                p.addLast(new HttpRequestDecoder());
                p.addLast(new IdleStateHandler(0, 0, 20));
                p.addLast(new HttpContentCompressor());
                p.addLast(new HttpObjectAggregator(HttpConstant.MAX_CONTENT_LEN));
                p.addLast(new HttpHandler(applicationContext, false));
            }
        });
        try {
            ChannelFuture channelFuture = bootstrap.bind(env.getPort()).sync();
            if (channelFuture.isSuccess()) {
                log.info("Http is started in {}.", env.getPort());
            }
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("Http listen fail.", e);
        } finally {
            shutdown();
        }
    }
}
