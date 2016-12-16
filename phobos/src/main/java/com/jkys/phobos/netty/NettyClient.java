package com.jkys.phobos.netty;

import com.jkys.phobos.client.InvokeInfo;
import com.jkys.phobos.client.PhobosClientContext;
import com.jkys.phobos.netty.channel.AbstractClientChannelHandler;
import com.jkys.phobos.netty.channel.DefaultClientChannelHandler;
//import com.jkys.phobos.netty.channel.HeartBeatPingChannelHandler;
import com.jkys.phobos.netty.codec.PhobosRequestEncoder;
import com.jkys.phobos.netty.codec.PhobosResponseDecoder;
import com.jkys.phobos.netty.listener.PhobosChannelActiveListener;
import com.jkys.phobos.netty.listener.PhobosChannelReadListener;
import com.jkys.phobos.remote.protocol.PhobosRequest;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by frio on 16/7/4.
 */
public class NettyClient {

    private final Logger logger = LoggerFactory.getLogger(NettyClient.class);

    private final String host;

    private final int port;

    private int startTimeOut;

    private boolean isConnect = false;

    private Class<? extends AbstractClientChannelHandler> handlerClass;

    private ChannelFuture future;

    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    public NettyClient(String host, int port, int startTimeOut) {
        this.host = host;
        this.port = port;
        this.startTimeOut = startTimeOut;
    }

    public void connect() throws Exception {
        final NettyClient sourse = this;
        if (handlerClass == null)
            handlerClass = DefaultClientChannelHandler.class;
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        public void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new PhobosRequestEncoder());
                            socketChannel.pipeline().addLast(new PhobosResponseDecoder());
                            socketChannel.pipeline().addLast(handlerClass.getConstructor(NettyClient.class).newInstance(sourse)
                                    .addPhobosListener(new PhobosChannelReadListener())
                                    .addPhobosListener(new PhobosChannelActiveListener()));
                            //socketChannel.pipeline().addLast(new HeartBeatPingChannelHandler(sourse));

                        }
                    });
            future = bootstrap.connect(host, port).sync();
            future.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
            setConnect(false);
            //clean client context
            HashMap<String ,List<NettyClient>> connectInfo = PhobosClientContext.getInstance().getConnectInfo();
            for(List<NettyClient> list : connectInfo.values()){
                Iterator<NettyClient> it = list.iterator();
                while (it.hasNext()){
                    NettyClient c = it.next();
                    if(!c.isConnect)
                        it.remove();
                }
            }
            executor.execute(()->{
                try {
                    TimeUnit.SECONDS.sleep(5);
                    connect();
                } catch (Exception e) {
                    logger.error("reconnection service:{}:{} failed!", host, port);
                    e.printStackTrace();
                }
            });

        }
    }

    public void noBlockConnect() throws Exception {
        new Thread(new Runnable() {
            public void run() {
                try {
                    connect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        synchronized (this) {
            this.wait(startTimeOut * 1000);
            if (!this.isConnect()) {
                throw new RuntimeException("start netty client time out : address : " + this.host + ":" + this.port);
            }
        }
        logger.info("client启动成功");
    }

    public InvokeInfo send(final PhobosRequest request) throws Exception {

        InvokeInfo invokeInfo = new InvokeInfo();
        invokeInfo.setRequest(request);
        PhobosClientContext.getInstance().setInvokeInfo(invokeInfo);

        try {
            boolean bool = future.channel().writeAndFlush(request).await(PhobosClientContext.getInstance().getRequestTimeOut() * 1000);
            logger.info("send finish");
        }catch (Exception e){
            PhobosClientContext.getInstance().removeInvokeInfo(request.getHeader().getSequenceId());
            throw e;
        }

        //如果异步线程已经notify 则不需要wait
        if(invokeInfo.isTimeOut()){
            synchronized (invokeInfo){
                invokeInfo.wait(PhobosClientContext.getInstance().getRequestTimeOut() * 1000);
            }
        }

        PhobosClientContext.getInstance().removeInvokeInfo(request.getHeader().getSequenceId());
        if (invokeInfo.isTimeOut()) {
            throw new RuntimeException("invoke service time out for " + request.getRequest().getServiceName() + "_" + request.getRequest().getMethodName());
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

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
}
