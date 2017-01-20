package com.jkys.phobos.spring.server;

import com.jkys.phobos.internal.Helper;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;


/**
 * Created by lo on 1/6/17.
 */
public class ServerBean implements ApplicationListener<ContextRefreshedEvent> {
    public static final String NAME = "_phobosServerBean";

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        Helper.triggerServer(event.getApplicationContext());
    }

    public void joinServer() throws InterruptedException {
        Helper.joinServer();
    }
}
