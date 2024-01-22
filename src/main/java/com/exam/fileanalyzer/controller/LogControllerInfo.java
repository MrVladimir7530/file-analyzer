package com.exam.fileanalyzer.controller;

import com.exam.fileanalyzer.model.LogDto;
import com.exam.fileanalyzer.service.LogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Map;

@RequiredArgsConstructor
@RestController()
@RequestMapping("/logs")
public class LogControllerInfo {
    private final LogService logService;

    @PostMapping
    public ResponseEntity<Map<String, Integer>> getStringInLogs(@RequestBody LogDto log) {
        try {
            Map<String, Integer> logInfo = logService.getLogInfo(log);
            return ResponseEntity.ok().body(logInfo);
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
