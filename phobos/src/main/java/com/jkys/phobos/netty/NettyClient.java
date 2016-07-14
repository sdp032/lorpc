package com.jkys.phobos.netty;

import com.jkys.phobos.netty.listener.PhobosChannelActiveListener;
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

    private boolean isConnect = false;

    private Class<? extends AbstractClientChannelHandler> handlerClass;

    public NettyClient(String host,int port,int startTimeOut){
        this.host = host;
        this.port = port;
        this.startTimeOut = startTimeOut;
    }

    public void connect() throws Exception{
        final NettyClient sourse = this;
        if(handlerClass == null)
            handlerClass = DefaultClientChannelHandler.class;
        EventLoopGroup group = new NioEventLoopGroup();
        try{
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY,true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        public void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new PhobosRequestEncoder());
                            socketChannel.pipeline().addLast(new PhobosResponseDecoder());
                            socketChannel.pipeline().addLast(handlerClass.getConstructor(NettyClient.class).newInstance(sourse)
                                    .addPhobosListener(new PhobosChannelActiveListener())
                            );
                        }
                    });
            ChannelFuture f = bootstrap.connect(host,port).sync();
            f.channel().closeFuture().sync();
        }finally {
            group.shutdownGracefully();
        }
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
        synchronized (this){
            this.wait(startTimeOut*1000);
            if(!this.isConnect()){
                throw new RuntimeException("start netty client time out");
            }
        }
        System.out.println("client启动成功");
    }

    public boolean isConnect() {
        return isConnect;
    }

    public void setConnect(boolean connect) {
        isConnect = connect;
    }

    public Class<? extends AbstractClientChannelHandler> getHandlerClass() {
        return handlerClass;
    }

    public void setHandlerClass(Class<? extends AbstractClientChannelHandler> handlerClass) {
        this.handlerClass = handlerClass;
    }
}
