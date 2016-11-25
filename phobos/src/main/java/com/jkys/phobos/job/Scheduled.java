package com.jkys.phobos.job;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by zdj on 2016/11/24.
 */
public class Scheduled {

    List<Task> tasks = new ArrayList();
    ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    public void run(){
        for(Task task : tasks){
            scheduledExecutorService.scheduleAtFixedRate(task.getRunnable(), task.initialDelay, task.period, task.timeUnit);
        }
    }

    public Scheduled addTask(Task task){
        tasks.add(task);
        return this;
    }
}
