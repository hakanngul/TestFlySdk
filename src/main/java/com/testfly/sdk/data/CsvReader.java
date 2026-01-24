package com.testfly.sdk.data;

import com.testfly.sdk.manager.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CsvReader {

    private static final Logger logger = LogManager.getLogger(CsvReader.class);

    public static List<String[]> readCsvFile(String filePath) throws IOException {
        logger.info("Reading CSV file: " + filePath);
        List<String[]> data = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = parseCSVLine(line);
                data.add(values);
            }
            logger.info("Read " + data.size() + " rows from CSV");
        }
        return data;
    }

    public static List<String[]> readCsvFileWithHeader(String filePath, boolean hasHeader) throws IOException {
        logger.info("Reading CSV file with header: " + hasHeader);
        List<String[]> data = readCsvFile(filePath);

        if (hasHeader && !data.isEmpty()) {
            data.remove(0);
        }
        logger.info("Read " + data.size() + " data rows from CSV");
        return data;
    }

    public static List<String> getColumnValues(String filePath, int columnIndex) throws IOException {
        logger.info("Reading column " + columnIndex + " from CSV");
        List<String[]> data = readCsvFile(filePath);
        List<String> columnValues = new ArrayList<>();

        for (String[] row : data) {
            if (columnIndex >= 0 && columnIndex < row.length) {
                columnValues.add(row[columnIndex]);
            }
        }
        return columnValues;
    }

    public static List<String> getRowValues(String filePath, int rowIndex) throws IOException {
        logger.info("Reading row " + rowIndex + " from CSV");
        List<String[]> data = readCsvFile(filePath);

        if (rowIndex >= 0 && rowIndex < data.size()) {
            return Arrays.asList(data.get(rowIndex));
        }
        return new ArrayList<>();
    }

    public static String[] getHeaders(String filePath) throws IOException {
        logger.info("Reading headers from CSV");
        List<String[]> data = readCsvFile(filePath);

        if (!data.isEmpty()) {
            return data.get(0);
        }
        return new String[0];
    }

    public static int getRowCount(String filePath) throws IOException {
        List<String[]> data = readCsvFile(filePath);
        return data.size();
    }

    public static int getColumnCount(String filePath) throws IOException {
        List<String[]> data = readCsvFile(filePath);
        if (data.isEmpty()) {
            return 0;
        }
        return data.get(0).length;
    }

    public static String[][] readCsvAsArray(String filePath) throws IOException {
        List<String[]> data = readCsvFile(filePath);
        return data.toArray(new String[0][]);
    }

    public static List<String> readCsvColumn(String filePath, String columnName) throws IOException {
        logger.info("Reading column by name: " + columnName + " from CSV");
        String[] headers = getHeaders(filePath);
        int columnIndex = -1;

        for (int i = 0; i < headers.length; i++) {
            if (headers[i].equalsIgnoreCase(columnName)) {
                columnIndex = i;
                break;
            }
        }

        if (columnIndex == -1) {
            throw new RuntimeException("Column not found: " + columnName);
        }

        return getColumnValues(filePath, columnIndex);
    }

    public static List<Map<String, String>> readCsvAsMap(String filePath) throws IOException {
        logger.info("Reading CSV as map");
        String[] headers = getHeaders(filePath);
        List<String[]> data = readCsvFileWithHeader(filePath, true);
        List<Map<String, String>> mappedData = new ArrayList<>();

        for (String[] row : data) {
            Map<String, String> rowMap = new HashMap<>();
            for (int i = 0; i < headers.length && i < row.length; i++) {
                rowMap.put(headers[i], row[i]);
            }
            mappedData.add(rowMap);
        }

        return mappedData;
    }

    public static boolean fileExists(String filePath) {
        return new java.io.File(filePath).exists();
    }

    private static String[] parseCSVLine(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder currentValue = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                result.add(currentValue.toString());
                currentValue = new StringBuilder();
            } else {
                currentValue.append(c);
            }
        }
        result.add(currentValue.toString());

        return result.toArray(new String[0]);
    }
}
