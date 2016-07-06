package com.jkys.phobos.netty;

import com.jkys.phobos.remote.URL;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Created by frio on 16/7/4.
 */
public class NettyServer {

    private Integer port;

    private ChannelHandlerAdapter handler;

    public NettyServer(URL url){

    }

    public NettyServer(Integer port){
        this.port = port;
    }

    public void open() throws Exception{
        if(handler == null)
            handler  = new DefaultServerChannelHandler();
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
                        socketChannel.pipeline().addLast(handler);
                    }
                });
            ChannelFuture future = bootstrap.bind(port).sync();
            future.channel().closeFuture().sync();

        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public void setHandler(ChannelHandlerAdapter handler) {
        this.handler = handler;
    }
}
