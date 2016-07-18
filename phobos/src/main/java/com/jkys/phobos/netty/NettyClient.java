package com.jkys.phobos.netty;

import com.jkys.phobos.client.InvokeInfo;
import com.jkys.phobos.client.PhobosClientContext;
import com.jkys.phobos.netty.listener.PhobosChannelActiveListener;
import com.jkys.phobos.netty.listener.PhobosChannelReadListener;
import com.jkys.phobos.remote.protocol.PhobosRequest;
import com.jkys.phobos.remote.protocol.PhobosResponse;
import com.jkys.phobos.remote.protocol.Response;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.TimeoutException;

/**
 * Created by frio on 16/7/4.
 */
public class NettyClient {

    private final String host;

    private final int port;

    private int startTimeOut;

    private boolean isConnect = false;

    private Class<? extends AbstractClientChannelHandler> handlerClass;

    private ChannelFuture future;

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
                                    .addPhobosListener(new PhobosChannelReadListener())
                                    .addPhobosListener(new PhobosChannelActiveListener())
                            );
                        }
                    });
            future = bootstrap.connect(host,port).sync();
            future.channel().closeFuture().sync();
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

    public InvokeInfo send(final PhobosRequest request) throws Exception{

        InvokeInfo invokeInfo = new InvokeInfo();
        invokeInfo.setRequest(request);
        PhobosClientContext.getInstance().setInvokeInfo(invokeInfo);
        new Thread(new Runnable() {
            public void run() {
                future.channel().writeAndFlush(request);
            }
        }).start();
        synchronized (invokeInfo){
            invokeInfo.wait(startTimeOut*1000);
            PhobosClientContext.getInstance().removeInvokeInfo(request.getHeader().getSequenceId());
            if(invokeInfo.isTimeOut()){
                throw new RuntimeException("invoke service time out for "+ request.getRequest().getServiceName() + "_" + request.getRequest().getMethodName());
            }
        }
        return invokeInfo;
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
