package com.testfly.sdk.data;

import com.testfly.sdk.manager.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExcelReader {

    private static final Logger logger = LogManager.getLogger(ExcelReader.class);

    public static List<String[]> readExcel(String filePath, String sheetName) throws IOException {
        logger.info("Reading Excel file: " + filePath + ", sheet: " + sheetName);
        List<String[]> data = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                throw new RuntimeException("Sheet not found: " + sheetName);
            }

            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    continue;
                }
                String[] rowData = new String[row.getLastCellNum() + 1];
                for (int i = 0; i < row.getLastCellNum() + 1; i++) {
                    Cell cell = row.getCell(i);
                    rowData[i] = cell != null ? getCellValueAsString(cell) : "";
                }
                data.add(rowData);
            }
            logger.info("Read " + data.size() + " rows from Excel");
        }
        return data;
    }

    public static List<String[]> readExcel(String filePath) throws IOException {
        return readExcel(filePath, 0);
    }

    public static List<String[]> readExcel(String filePath, int sheetIndex) throws IOException {
        logger.info("Reading Excel file: " + filePath + ", sheet index: " + sheetIndex);
        List<String[]> data = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(sheetIndex);
            if (sheet == null) {
                throw new RuntimeException("Sheet not found at index: " + sheetIndex);
            }

            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    continue;
                }
                String[] rowData = new String[row.getLastCellNum() + 1];
                for (int i = 0; i < row.getLastCellNum() + 1; i++) {
                    Cell cell = row.getCell(i);
                    rowData[i] = cell != null ? getCellValueAsString(cell) : "";
                }
                data.add(rowData);
            }
            logger.info("Read " + data.size() + " rows from Excel");
        }
        return data;
    }

    private static String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf(cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula();
            case BLANK -> "";
            default -> "";
        };
    }

    public static String[][] readExcelAsArray(String filePath, String sheetName) throws IOException {
        List<String[]> data = readExcel(filePath, sheetName);
        return data.toArray(new String[0][]);
    }

    public static List<String> getColumnValues(String filePath, String sheetName, int columnIndex) throws IOException {
        List<String[]> data = readExcel(filePath, sheetName);
        List<String> columnValues = new ArrayList<>();
        for (String[] row : data) {
            if (columnIndex < row.length) {
                columnValues.add(row[columnIndex]);
            }
        }
        return columnValues;
    }

    public static List<String> getRowValues(String filePath, String sheetName, int rowIndex) throws IOException {
        List<String[]> data = readExcel(filePath, sheetName);
        if (rowIndex < data.size()) {
            return java.util.Arrays.asList(data.get(rowIndex));
        }
        return new ArrayList<>();
    }

    public static int getRowCount(String filePath, String sheetName) throws IOException {
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheet(sheetName);
            return sheet != null ? sheet.getPhysicalNumberOfRows() : 0;
        }
    }

    public static int getColumnCount(String filePath, String sheetName) throws IOException {
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet != null && sheet.getPhysicalNumberOfRows() > 0) {
                return sheet.getRow(0).getLastCellNum() + 1;
            }
            return 0;
        }
    }

    public static List<String> getSheetNames(String filePath) throws IOException {
        List<String> sheetNames = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                sheetNames.add(workbook.getSheetName(i));
            }
        }
        return sheetNames;
    }

    public static boolean sheetExists(String filePath, String sheetName) throws IOException {
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {
            return workbook.getSheet(sheetName) != null;
        }
    }
}
