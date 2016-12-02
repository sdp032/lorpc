package com.jkys.phobos.job;

import com.github.infrmods.xbus.client.TLSInitException;
import com.github.infrmods.xbus.client.XBusClient;
import com.github.infrmods.xbus.client.XbusConfig;
import com.github.infrmods.xbus.exceptions.XBusException;
import com.github.infrmods.xbus.item.ServiceDesc;
import com.github.infrmods.xbus.item.ServiceEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Created by zdj on 2016/11/24.
 */
public class XbusTask extends Task {

    private final Logger logger = LoggerFactory.getLogger(XbusTask.class);

    private XBusClient client;

    private Long leaseId;

    private ServiceDesc[] desces;

    private ServiceEndpoint endpoint;

    private Integer ttl;

    public XbusTask(long initialDelay, long period, TimeUnit timeUnit, String[] endpoints, String keystorePath, String keystorePassword) throws TLSInitException {
        super(initialDelay, period, timeUnit);
        client = new XBusClient(new XbusConfig(endpoints, keystorePath, keystorePassword));
    }

    @Override
    public void execute() throws Exception {
        try {
            if (leaseId == null) {
                this.leaseId = client.plugServices(desces, endpoint, ttl);
            }
            client.keepAliveLease(leaseId);
            logger.info("keepAliveLease --> {}", leaseId);
        }catch (XBusException e){
            leaseId = null;
            logger.error("keepAliveLease failed! leaseId : {} , xbus exception code : {}, xbus exception message: {}", leaseId, e.code, e.message);

        }
    }

    public XbusTask plug(ServiceDesc[] desces, ServiceEndpoint endpoint, Integer ttl) throws XBusException {
        this.leaseId = client.plugServices(desces, endpoint, ttl);
        logger.info("plug service --> {}", leaseId);
        this.desces = desces;
        this.endpoint = endpoint;
        this.ttl = ttl;
        return this;
    }
}
