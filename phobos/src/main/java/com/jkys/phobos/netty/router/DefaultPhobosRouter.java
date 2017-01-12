package com.jkys.phobos.netty.router;

import com.jkys.phobos.codec.SerializeHandle;
import com.jkys.phobos.codec.SerializeHandleFactory;
import com.jkys.phobos.constant.ErrorEnum;
import com.jkys.phobos.protocol.PhobosRequest;
import com.jkys.phobos.protocol.PhobosResponse;
import com.jkys.phobos.protocol.Response;
import com.jkys.phobos.server.Provider;
import com.jkys.phobos.server.ServerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zdj on 2016/7/14.
 */
public class DefaultPhobosRouter implements PhobosRouter {
    private final Logger logger = LoggerFactory.getLogger(DefaultPhobosRouter.class);
    private ServerContext context = ServerContext.getInstance();

    public PhobosResponse route(PhobosRequest request) throws Exception {
        SerializeHandle handle = SerializeHandleFactory.create(request.getHeader().getSerializationType());
        String serviceName = request.getRequest().getServiceName();
        String serviceVersion = request.getRequest().getServiceVersion();
        Provider provider = context.getProvider(serviceName, serviceVersion);
        if (provider == null) {
            PhobosResponse phobosResponse = new PhobosResponse(request.getHeader(), new Response());
            phobosResponse.getResponse().setSuccess(false);
            phobosResponse.getResponse().setErrCode(ErrorEnum.UNKNOWN_SERVICE.name());
            return phobosResponse;
        }
        return new PhobosResponse(request.getHeader(), provider.invoke(handle, request.getRequest()));
    }
}
