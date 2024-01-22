package com.exam.fileanalyzer.service;

import com.exam.fileanalyzer.model.LogDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class LogServiceImpl implements LogService{
    private final LogsAnalyzer logsAnalyzer;
    @Override
    public Map<String, Integer> getLogInfo(LogDto logDto) throws IOException {
        return logsAnalyzer.countEntriesInZipFile(logDto.getWord(), logDto.getDate(), logDto.getNumberDay());
    }
}
