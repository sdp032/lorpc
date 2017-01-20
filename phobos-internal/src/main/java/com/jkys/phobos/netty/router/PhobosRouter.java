package com.jkys.phobos.netty.router;

import com.jkys.phobos.protocol.PhobosRequest;
import com.jkys.phobos.protocol.PhobosResponse;

/**
 * Created by zdj on 2016/7/11.
 */
public interface PhobosRouter {

    PhobosResponse route(PhobosRequest request) throws Exception;
}
