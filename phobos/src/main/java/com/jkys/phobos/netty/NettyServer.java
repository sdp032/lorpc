package com.jkys.phobos.netty;

import com.github.infrmods.xbus.item.ServiceDesc;
import com.github.infrmods.xbus.item.ServiceEndpoint;
import com.jkys.phobos.config.ServerConfig;
import com.jkys.phobos.job.Scheduled;
import com.jkys.phobos.job.XbusTask;
import com.jkys.phobos.netty.channel.AbstractServerChannelHandler;
import com.jkys.phobos.netty.channel.DefaultServerChannelHandler;
import com.jkys.phobos.netty.codec.PhobosResponseEncoder;
import com.jkys.phobos.netty.codec.PhotosRequestDecoder;
import com.jkys.phobos.config.PhobosConfig;
import com.jkys.phobos.server.ServerContext;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Created by frio on 16/7/4.
 */
public class NettyServer {

    private static Logger logger = LoggerFactory.getLogger(NettyServer.class);

    private ServerContext context = ServerContext.getInstance();

    private Class<? extends AbstractServerChannelHandler> handlerClass;

    public NettyServer() {
    }

    public void open() throws Exception {
        if (handlerClass == null) {
            handlerClass = DefaultServerChannelHandler.class;
        }

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            //PhobosRequest解码器
                            socketChannel.pipeline().addLast(new PhotosRequestDecoder());
                            //PhobosResponse编码器
                            socketChannel.pipeline().addLast(new PhobosResponseEncoder());
                            //socketChannel.pipeline().addLast(new ReadTimeoutHandler(60));
                            //业务处理器
                            socketChannel.pipeline().addLast(handlerClass.getConstructor().newInstance());
                        }
                    });
            ServerConfig config = PhobosConfig.getInstance().getServer();
            ChannelFuture future = bootstrap.bind(config.getBindHost(), config.getBindPort()).sync();
            synchronized (this) {
                this.notify();
            }
            //启动完成后启动定时器
            new Scheduled().addTask(
                    new XbusTask(1, 60, TimeUnit.SECONDS)
                            .plug(context.getServiceDescs(),
                                    new ServiceEndpoint(config.getAddress(), null), 120)
            ).run();

            future.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    private void listenOpen() throws InterruptedException {
        synchronized (this) {
            this.wait();
        }
    }

    public Thread noBlockOpen() {
        logger.info("netty服务器启动中。。。");
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    open();
                } catch (Exception e) {
                    e.printStackTrace();
                    System.exit(0);
                }
            }
        });
        t.start();
        try {
            listenOpen();
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(0);
        }
        logger.info("netty服务器启动完成。。。");
        return t;
    }
}
