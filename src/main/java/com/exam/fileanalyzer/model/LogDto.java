package com.exam.fileanalyzer.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class LogDto {
    String word;
    LocalDate date;
    Integer numberDay;
}
