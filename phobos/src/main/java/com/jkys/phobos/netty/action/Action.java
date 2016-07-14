package com.jkys.phobos.netty.action;

/**
 * Created by zdj on 2016/7/12.
 */
public interface Action <Req ,Res>{

    Res serverInfo();

    Res execute(Req request);
}
