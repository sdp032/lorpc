package com.jkys.phobos.netty.listener;

import com.jkys.phobos.client.InvokeInfo;
import com.jkys.phobos.client.PhobosClientContext;
import com.jkys.phobos.codec.MsgpackUtil;
import com.jkys.phobos.netty.NettyClient;
import com.jkys.phobos.remote.protocol.Header;
import com.jkys.phobos.remote.protocol.Request;
import com.jkys.phobos.remote.protocol.Response;
import com.jkys.phobos.server.ServerInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Created by zdj on 2016/7/8.
 */
public class PhobosChannelActiveListener implements PhobosListener<PhobosChannelActiveEvent> {

    private static Logger logger = LoggerFactory.getLogger(PhobosChannelActiveListener.class);

    public void onPhobosEvent(PhobosChannelActiveEvent event) throws Exception {

        NettyClient client = (NettyClient) event.getSource();
        Map<String, List<NettyClient>> connectInfo = PhobosClientContext.getInstance().getConnectInfo();

        InvokeInfo invokeInfo = event.getInvokeInfo();
        Header header = invokeInfo.getRequest().getHeader();
        Request request = invokeInfo.getRequest().getRequest();
        Response response = invokeInfo.getResponse().getResponse();

        ServerInfo serverInfo = null;
        byte[] data = response.getData();
        if (header.getSerializationType() == Header.SerializationType.MAGPACK.serializationType) {
            serverInfo = MsgpackUtil.MESSAGE_PACK.read(data, ServerInfo.class);
        }
        logger.info("serverName:{}-->", serverInfo.getServerAppName());
        for (String s : serverInfo.getServiceList()) {
            logger.info("service:{}", s);
            List<NettyClient> list = connectInfo.get(s);
            if (list != null) {
                list.add(client);
            }
        }

        synchronized (event.getSource()) {
            client.setConnect(true);
            client.notify();
        }
    }

    public Class<PhobosChannelActiveEvent> getEventClass() {
        return PhobosChannelActiveEvent.class;
    }
}
