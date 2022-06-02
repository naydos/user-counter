package com.user.counter.usercounter.controller;

import com.user.counter.usercounter.service.CSVFileReader;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class CSVReaderController {
    private final CSVFileReader fileReader;

    @PostMapping("/upload")
    public ResponseEntity<Integer> uploadCSVFile(@RequestParam("file") MultipartFile file) {
        Integer userCount = fileReader.readFile(file);
        return ResponseEntity.ok(userCount);

    }

    @PostMapping("/upload-csv")
    public ResponseEntity<Integer> uploadCSVFileWithThreads(@RequestParam("file") MultipartFile file)  {
        Integer userCount = fileReader.readFileWithThreads(file);
        return ResponseEntity.ok(userCount);

    }
}
