package com.jkys.phobos.server;

import com.github.infrmods.xbus.client.XBusClient;
import com.github.infrmods.xbus.exceptions.XBusException;
import com.github.infrmods.xbus.item.ServiceEndpoint;
import com.jkys.phobos.config.ServerConfig;
import com.jkys.phobos.codec.PhobosRequestDecoder;
import com.jkys.phobos.codec.PhobosResponseEncoder;
import com.jkys.phobos.config.PhobosConfig;
import com.jkys.phobos.util.RegistryUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.CountDownLatch;

/**
 * Created by frio on 16/7/4.
 */
public class PhobosServer {
    private static int PLUG_TTL = 120;
    private static int PLUG_INTERVAL = 100;
    private static DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static Logger logger = LoggerFactory.getLogger(PhobosServer.class);
    private ServerContext context = ServerContext.getInstance();

    private XBusClient xBusClient;
    private volatile ChannelFuture channelFuture = null;
    private volatile CountDownLatch shutdownBarrier;
    private ServerChannelHandler serverChannelHandler = new ServerChannelHandler();

    public PhobosServer() {
        xBusClient = RegistryUtil.getXBus(PhobosConfig.getInstance().getRegistry());
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            stop();
            try {
                join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }));
    }

    public void start() {
        synchronized (this) {
            if (channelFuture == null || !channelFuture.channel().isRegistered()) {
                try {
                    startNetty();
                } catch (InterruptedException e) {
                    // FIXME
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void stop() {
        synchronized (this) {
            if (channelFuture != null && channelFuture.channel().isRegistered()) {
                channelFuture.channel().close();
                serverChannelHandler.stop();
            }
        }
    }

    public void join() throws InterruptedException {
        ChannelFuture closeFuture = null;
        synchronized (this) {
            if (channelFuture != null && channelFuture.channel().isRegistered()) {
                closeFuture = channelFuture.channel().closeFuture();
            }
        }
        if (closeFuture != null) {
            closeFuture.sync();
        }
        if (shutdownBarrier != null) {
            shutdownBarrier.await();
        }
    }

    private void startNetty() throws InterruptedException {
        ServerConfig config = PhobosConfig.getInstance().getServer();
        ServerBootstrap bootstrap = new ServerBootstrap();
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new PhobosRequestDecoder());
                            ch.pipeline().addLast(new PhobosResponseEncoder());
                            ch.pipeline().addLast(serverChannelHandler);
                        }
                    });

            channelFuture = bootstrap.bind(config.getBindHost(), config.getBindPort()).sync();
        } catch (Throwable e) {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
            throw e;
        }

        watchChannel(bossGroup, workerGroup);
        logger.info("phobos server listening on {}", config.getAddress());
    }

    private void watchChannel(EventLoopGroup bossGroup, EventLoopGroup workerGroup) {
        ServerConfig config = PhobosConfig.getInstance().getServer();

        shutdownBarrier = new CountDownLatch(1);
        final boolean[] doPlug = {true};
        new Thread(() -> {
            ServiceEndpoint endpoint = new ServiceEndpoint(config.getAddress(), null);
            while (doPlug[0]) {
                try {
                    xBusClient.plugServices(context.getServiceDescs(), endpoint, PLUG_TTL);
                } catch (XBusException e) {
                    logger.error("plug services fail", e);
                }
                try {
                    Thread.sleep(PLUG_INTERVAL * 1000);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }).start();
        channelFuture.channel().closeFuture().addListener(future -> {
            logger.info("phobos server is shutting down");
            doPlug[0] = false;
            try {
                bossGroup.shutdownGracefully();
                serverChannelHandler.waitStop();
                workerGroup.shutdownGracefully();
                logger.info("phobos server stop finished");
                System.out.println("phobos server stop finished at " + DATE_FORMAT.format(Calendar.getInstance().getTime()));
            } finally {
                shutdownBarrier.countDown();
            }
        });
    }
}
