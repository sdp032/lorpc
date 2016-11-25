package com.jkys.phobos.job;

import java.util.concurrent.TimeUnit;

/**
 * Created by zdj on 2016/11/24.
 */
public abstract class Task {

    protected long initialDelay;
    protected long period;
    protected TimeUnit timeUnit;

    public Task(long initialDelay, long period, TimeUnit timeUnit){
        this.initialDelay = initialDelay;
        this.period = period;
        this.timeUnit = timeUnit;
    }

    protected Runnable getRunnable(){
        return new Runnable() {
            @Override
            public void run(){
                try {
                    execute();
                }catch (Exception e){
                    e.printStackTrace();
                    throw new RuntimeException();
                }
            }
        };
    }

    public abstract void execute() throws Exception;
}
