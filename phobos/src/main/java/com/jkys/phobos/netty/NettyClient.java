package com.jkys.phobos.netty;

import com.jkys.phobos.netty.listener.PhobosChannelActiveListener;
import com.jkys.phobos.netty.listener.PhobosTestListener;
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

    private int startTimeOut;

    private AbstractClientChannelHandler handler = new DefaultClientChannelHandler();

    public NettyClient(String host,int port,int startTimeOut){
        this.host = host;
        this.port = port;
        this.startTimeOut = startTimeOut;
    }

    public void connect() throws Exception{
        handler.addPhobosListener(new PhobosChannelActiveListener());
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

    public void setHandler(AbstractClientChannelHandler handler) {
        this.handler = handler;
    }

    public void noBlockConnect() throws Exception{
        new Thread(new Runnable() {
            public void run() {
                try {
                    connect();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
        synchronized (handler){
            handler.wait(startTimeOut*1000);
            if(!handler.isActive()){
                throw new RuntimeException("start netty client time out");
            }
        }
        System.out.println("client启动成功");
    }
}
