package com.jkys.phobos.spring.client.beans;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * Created by lo on 1/6/17.
 */
public class ClientBean implements ApplicationListener<ContextRefreshedEvent> {
    public static final String NAME = "_phobosClientBean";

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

    }
}
