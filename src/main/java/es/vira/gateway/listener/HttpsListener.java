package es.vira.gateway.listener;

import es.vira.gateway.constant.SystemConstant;
import es.vira.gateway.constant.HttpConstant;
import es.vira.gateway.environment.ApplicationContext;
import es.vira.gateway.environment.HttpsEnvironment;
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
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import java.io.FileInputStream;
import java.security.KeyStore;

/**
 * Https listener
 *
 * @author Víctor Gómez
 * @since 1.0.0
 */
@Slf4j
public class HttpsListener implements Runnable {
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

    public HttpsListener(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        if (SystemConstant.LINUX.equals(System.getProperty(SystemConstant.OS_NAME))) {
            boss = new EpollEventLoopGroup();
            worker = new EpollEventLoopGroup();
            channelClass = EpollServerSocketChannel.class;
            log.info("Https listener used epoll model");
        } else {
            boss = new NioEventLoopGroup();
            worker = new NioEventLoopGroup();
            channelClass = NioServerSocketChannel.class;
            log.info("Https listener used nio model");
        }
    }

    /**
     * shutdown
     */
    public void shutdown() {
        boss.shutdownGracefully();
        worker.shutdownGracefully();
    }

    /**
     * get ssl context
     *
     * @param keyPath certificate file path
     * @param pwd     certificate password
     * @return ssl context
     * @throws Exception read certificate file error
     */
    private SSLContext sslContext(String pwd, String keyPath) throws Exception {
        char[] passArray = pwd.toCharArray();
        SSLContext sslContext = SSLContext.getInstance("TLSv1");
        KeyStore ks = KeyStore.getInstance("JKS");
        FileInputStream inputStream = new FileInputStream(keyPath);
        ks.load(inputStream, passArray);
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(ks, passArray);
        sslContext.init(kmf.getKeyManagers(), null, null);
        inputStream.close();
        return sslContext;
    }

    @Override
    public void run() {
        HttpsEnvironment env = applicationContext.getContext(HttpsEnvironment.class);
        if (!env.isEnable()) {
            return;
        }
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(boss, worker);
        bootstrap.channel(channelClass);
        bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
        bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                SSLEngine sslEngine = sslContext(env.getKeyPwd(), env.getKeyPath()).createSSLEngine();
                sslEngine.setUseClientMode(false);
                ChannelPipeline p = socketChannel.pipeline();
                p.addLast(new SslHandler(sslEngine));
                p.addLast(new HttpResponseEncoder());
                p.addLast(new HttpRequestDecoder());
                p.addLast(new IdleStateHandler(0, 0, 20));
                p.addLast(new HttpContentCompressor());
                p.addLast(new HttpObjectAggregator(HttpConstant.MAX_CONTENT_LEN));
                p.addLast(new HttpHandler(applicationContext, true));
            }
        });
        try {
            ChannelFuture channelFuture = bootstrap.bind(env.getPort()).sync();
            if (channelFuture.isSuccess()) {
                log.info("https is started in {}.", env.getPort());
            }
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("https listen fail.", e);
        } finally {
            shutdown();
        }
    }
}
