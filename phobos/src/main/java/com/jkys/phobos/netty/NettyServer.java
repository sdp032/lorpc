package com.jkys.phobos.netty;

import com.jkys.phobos.remote.URL;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by frio on 16/7/4.
 */
public class NettyServer {

    private static Logger logger = LoggerFactory.getLogger(NettyServer.class);

    private final Integer port;

    private ChannelHandlerAdapter handler;

    public NettyServer(Integer port){
        this.port = port;
    }

    public void open() throws Exception{
        if(handler == null)
            handler  = new DefaultServerChannelHandler();
        EventLoopGroup bossGroup = new NioEventLoopGroup(3);
        EventLoopGroup workerGroup = new NioEventLoopGroup(3);
        try{
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup,workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG,1024)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(handler);
                    }
                });
            ChannelFuture future = bootstrap.bind(port).sync();
            synchronized (this){
                this.notify();
            }
            future.channel().closeFuture().sync();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public void setHandler(ChannelHandlerAdapter handler) {
        this.handler = handler;
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
}
