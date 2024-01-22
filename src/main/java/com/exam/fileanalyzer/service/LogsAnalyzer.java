package com.exam.fileanalyzer.service;


import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static org.apache.logging.log4j.util.Strings.isEmpty;


@Service
public class LogsAnalyzer {
    @Value("${path.output}")
    private String pathOutput;
    @Value("${path.zip}")
    private String pathZip;
    private final Pattern PATTERN_LOGS = Pattern.compile(".*logs_\\d{4}-\\d{2}-\\d{2}-access.log.*");
    private final Pattern PATTERN_DATA = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
    private Map<String, Integer> logs = new HashMap<>();
    private ZipFile zipFile;

    @PostConstruct
    public void init() throws IOException {

        zipFile = new ZipFile(pathZip);
    }

    /**
     * Given a zip file, a search query, and a date range,
     * count the number of occurrences of the search query in each file in the zip file
     * @param searchQuery  The string to search for in the file.
     * @param startDate    The start date of the search.
     * @param numberOfDays The number of days to search for.
     * @return A map of file names and the number of occurrences of the search query in the file.
     */
    public Map<String, Integer> countEntriesInZipFile(String searchQuery, LocalDate startDate, Integer numberOfDays) throws IOException {
        LocalDate endDate = startDate.plusDays(numberOfDays);

        logs.clear();
        cleanDirectory();

        for (Enumeration<? extends ZipEntry> iter = zipFile.entries(); iter.hasMoreElements(); ) {
            ZipEntry entry = iter.nextElement();
            String name = entry.getName();
            if (!entry.isDirectory() && name.matches(PATTERN_LOGS.pattern())) {
                Matcher matcher = PATTERN_DATA.matcher(name);
                if (matcher.find()) {
                    String data = matcher.group();
                    LocalDate localDate = LocalDate.parse(data);
                    if (localDate.isBefore(endDate) && localDate.isAfter(startDate)) {
                        String pathNewFile = saveInFile(zipFile, entry);
                        addCountWordAndWordInMap(searchQuery, pathNewFile);
                    }
                }
            }
        }
        return logs;
    }

    private File cleanDirectory() throws IOException {
        File file = new File(pathOutput);
        FileUtils.cleanDirectory(file);
        return file;
    }

    public String saveInFile(ZipFile zipFile, ZipEntry entry) throws IOException {
        Path path = getPath(entry);
        try (InputStream inputStream = zipFile.getInputStream(entry);
             FileOutputStream fileOutputStream = new FileOutputStream(path.toString());
             BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream, 1024);
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream, 1024);) {
            bufferedInputStream.transferTo(bufferedOutputStream);
        }

        return path.toString();
    }

    private Path getPath(ZipEntry entry) throws IOException {
        String nameZipFile = getNameZipFile(entry.getName());
        Path path = Path.of(pathOutput, nameZipFile);
        Files.createDirectories(path.getParent());
        return path;
    }

    private String getNameZipFile(String fileName) {
        return fileName.substring(fileName.lastIndexOf("/") + 1);
    }

    private Integer addCountWordAndWordInMap(String searchQuery, String pathNewFile) throws IOException {
        File file = new File(pathNewFile);
        String newName = getNameFile(file);
        int count = countWordInText(file, searchQuery);
        return logs.put(newName, count);
    }

    private String getNameFile(File file) {
        String name = file.getName();
        return name.replace(getExtension(name), "");
    }

    private String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }

    private int countWordInText(File file, String str) throws IOException {
        Reader reader = new FileReader(file);
        int count = 0;
        try (BufferedReader bufferedReader = new BufferedReader(reader);) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                count += countMatches(line, str);
            }
        }
        return count;
    }

    private int countMatches(String text, String str) {
        if (isEmpty(text) || isEmpty(str)) {
            return 0;
        }
        return text.split(str, -1).length - 1;
    }
}
