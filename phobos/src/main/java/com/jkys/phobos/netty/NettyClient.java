package com.jkys.phobos.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Created by frio on 16/7/4.
 */
public class NettyClient {

    private final String host;

    private final int port;

    private ChannelHandlerAdapter handler;

    public NettyClient(String host,int port){
        this.host = host;
        this.port = port;
    }

    public void connect() throws Exception{
        if(handler == null)
            handler = new DefaultClientChannelHandler();
        EventLoopGroup group = new NioEventLoopGroup();
        try{
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY,true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        public void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(handler);
                        }
                    });
            ChannelFuture f = bootstrap.connect(host,port).sync();
            f.channel().closeFuture().sync();
        }finally {
            group.shutdownGracefully();
        }
    }

    public void setHandler(ChannelHandlerAdapter handler) {
        this.handler = handler;
    }
}
