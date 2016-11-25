package com.jkys.phobos.job;

import com.github.infrmods.xbus.client.TLSInitException;
import com.github.infrmods.xbus.client.XBusClient;
import com.github.infrmods.xbus.client.XbusConfig;
import com.github.infrmods.xbus.exceptions.XBusException;
import com.github.infrmods.xbus.item.ServiceDesc;
import com.github.infrmods.xbus.item.ServiceEndpoint;

import java.util.concurrent.TimeUnit;

/**
 * Created by zdj on 2016/11/24.
 */
public class XbusTask extends Task {

    private XBusClient client;

    private Long leaseId;

    public XbusTask(long initialDelay, long period, TimeUnit timeUnit, String[] endpoints, String keystorePath, String keystorePassword ) throws TLSInitException{
        super(initialDelay, period, timeUnit);
        client = new XBusClient(new XbusConfig(endpoints, keystorePath, keystorePassword));
    }

    @Override
    public void execute() throws Exception {
        if(leaseId == null){
            throw new RuntimeException("leaseId is null");
        }
        client.keepAliveLease(leaseId);
        System.out.println("keepAliveLease -->" + leaseId);
    }

    public XbusTask plug(ServiceDesc[] desces, ServiceEndpoint endpoint, Integer ttl) throws XBusException {
        this.leaseId = client.plugServices(desces, endpoint, ttl);
        return this;
    }
}
