package com.exam.fileanalyzer.service;

import com.exam.fileanalyzer.model.LogDto;

import java.io.IOException;
import java.util.Map;

public interface LogService {
    Map<String, Integer> getLogInfo(LogDto logDto) throws IOException;
}
