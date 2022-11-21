package edu.byu.cs.tweeter.client.model.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.BackgroundTask;

public class BaseTaskService<T extends BackgroundTask> {
    private T task;
    public BaseTaskService(T task) {
        this.task = task;
    }

    public <T extends BackgroundTask> void runExecutor() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(task);
    }

    public <T extends BackgroundTask> void runExecutor(ExecutorService executor) {
        executor.execute(task);
    }
}
