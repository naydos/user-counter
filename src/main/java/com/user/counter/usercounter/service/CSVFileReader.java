package com.user.counter.usercounter.service;

import com.user.counter.usercounter.exception.FileReaderException;
import com.user.counter.usercounter.model.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.IntStream;

@Service
public class CSVFileReader {

    private final String CSV_TYPE = "csv";

    private final static int THREAD_POOL_SIZE = 10;

    private final SynchronousQueue<String> queueLines = new SynchronousQueue<>();

    public Integer readFile(MultipartFile file) {
        Instant start = Instant.now();
        Set<User> userInfos = new HashSet<>(); // accepts unique values
        validateFile(file);
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(SplitterTaskSyncQueue.COMMA_DELIMITER);
                if (values != null && values.length == 3) {
                    if (StringUtils.isNotBlank(values[0]) && StringUtils.isNotBlank(values[1]) && StringUtils.isNotBlank(values[2])) { //user- email - source
                        userInfos.add(new User(values[0], values[1], values[2]));
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        Integer totalCountUser = userInfos.size() > 0 ? (userInfos.size() - 1) : 0; //eliminate header information
        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).toMillis();
        System.out.println("Elapsed time:" + timeElapsed);
        System.out.println("Total user count is " + totalCountUser);
        return totalCountUser;

    }

    public Integer readFileWithThreads(MultipartFile file) {
        Instant start = Instant.now();
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        validateFile(file);

        Set<User> userInfos = new HashSet<>(); // accepts unique values

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));

            Collection<Callable<Set<User>>> tasks = new ArrayList<>();
            List<Future<Set<User>>> result = new ArrayList<>();
            IntStream.range(0, THREAD_POOL_SIZE).forEach(x -> {
                SplitterTaskSyncQueue task = createTask();
                result.add(executor.submit(task));
                tasks.add(task);
            });


            String line;
            while ((line = reader.readLine()) != null) {
                queueLines.put(line);

            }

            tasks.forEach(t -> {
                try {
                    queueLines.put(SplitterTaskSyncQueue.END_OF_QUEUE);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });


            for (Future<Set<User>> r : result) {
                userInfos.addAll(r.get());
            }


        } catch (
                Exception e) {
            e.printStackTrace();
        }

        Integer totalCountUser = userInfos.size() > 0 ? (userInfos.size() - 1) : 0; //eliminate header information
        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).toMillis();
        System.out.println(" Elapsed time (Thread):" + timeElapsed);
        System.out.println(" Total user count is " + totalCountUser);
        return totalCountUser;
    }

    private SplitterTaskSyncQueue createTask() {
        return new SplitterTaskSyncQueue(queueLines);
    }

    private String getFileExtension(String filename) {
        String extension = Optional.ofNullable(filename)
                .filter(s -> s.contains("."))
                .map(s -> s.substring(filename.lastIndexOf(".") + 1)).get();
        return extension;
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new FileReaderException("File is empty.");
        }
        if (!CSV_TYPE.equalsIgnoreCase(getFileExtension(file.getOriginalFilename()))) {
            throw new FileReaderException("File extension is not valid.");
        }
    }

}
