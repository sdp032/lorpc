package com.jkys.phobos.netty;

import com.github.infrmods.xbus.item.ServiceEndpoint;
import com.jkys.phobos.job.Scheduled;
import com.jkys.phobos.job.XbusTask;
import com.jkys.phobos.server.PhobosContext;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

/**
 * Created by frio on 16/7/4.
 */
public class NettyServer {

    private static Logger logger = LoggerFactory.getLogger(NettyServer.class);

    private final Integer port;

    private PhobosContext context = PhobosContext.getInstance();

    private Class<? extends AbstractServerChannelHandler> handlerClass;

    public NettyServer(Integer port){
        this.port = port;
    }

    public void open() throws Exception{

        if(handlerClass == null){
            handlerClass = DefaultServerChannelHandler.class;
        }

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try{
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup,workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG,1024)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        //PhobosRequest解码器
                        socketChannel.pipeline().addLast(new PhotosRequestDecoder());
                        //PhobosResponse编码器
                        socketChannel.pipeline().addLast(new PhobosResponseEncoder());
                        //业务处理器
                        socketChannel.pipeline().addLast(handlerClass.getConstructor().newInstance());
                    }
                });
            ChannelFuture future = bootstrap.bind(port).sync();
            synchronized (this){
                this.notify();
            }
            //启动完成后启动定时器
            //获取本机IP地址
            String ip = InetAddress.getLocalHost().getHostAddress();

            new Scheduled().addTask(
                    new XbusTask(1, 60, TimeUnit.SECONDS, context.getXbusAddrs(), context.getKeystorePath(), context.getKeystorePassword())
                        .plug(context.getServiceDescs(), new ServiceEndpoint(ip + ":" + context.getPort(), null), 120)
            ).run();

            future.channel().closeFuture().sync();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public void listenOpen() throws InterruptedException{
        synchronized (this){
            this.wait();
        }
    }

    public void noBlockOpen(){
        System.out.println("netty启动中");
        new Thread(new Runnable() {
            public void run() {
                try {
                    open();
                }catch (Exception e){
                    e.printStackTrace();
                    System.exit(0);
                }
            }
        }).start();
        try{
            listenOpen();
        }catch (InterruptedException e){
            e.printStackTrace();
            System.exit(0);
        }
        System.out.println("netty启动完成");
    }

    public Class<? extends AbstractServerChannelHandler> getHandlerClass() {
        return handlerClass;
    }

    public void setHandlerClass(Class<? extends AbstractServerChannelHandler> handlerClass) {
        this.handlerClass = handlerClass;
    }
}
