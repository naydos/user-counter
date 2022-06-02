package com.user.counter.usercounter.service;

import com.user.counter.usercounter.model.User;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

public class SplitterTaskSyncQueue implements Callable<Set<User>> {

    private BlockingQueue<String> lineQueue;
    private Set<User> userInfos = new HashSet<>();
    public static final String COMMA_DELIMITER = ",";
    public static final String END_OF_QUEUE = "END_OF_QUEUE";

    public SplitterTaskSyncQueue(BlockingQueue<String> lineQueue) {
        this.lineQueue = lineQueue;
    }

    @Override
    public Set<User> call() {
        while (true) {
            String line = null;
            try {
                line = lineQueue.take();
                if (line.equals(END_OF_QUEUE)) {
                    return userInfos;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String[] values = line.split(COMMA_DELIMITER);
            if (values != null && values.length == 3) {
                if (StringUtils.isNotBlank(values[0]) && StringUtils.isNotBlank(values[1])) { //user- email
                    userInfos.add(new User(values[0], values[1], values[2]));
                }
            }
        }

    }
}
