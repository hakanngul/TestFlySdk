package com.testfly.sdk.utils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class FileUtils {

    private FileUtils() {
    }

    public static boolean fileExists(String filePath) {
        return Files.exists(Paths.get(filePath));
    }

    public static boolean directoryExists(String dirPath) {
        return Files.exists(Paths.get(dirPath)) && Files.isDirectory(Paths.get(dirPath));
    }

    public static String readFile(String filePath) throws IOException {
        return Files.readString(Paths.get(filePath), StandardCharsets.UTF_8);
    }

    public static List<String> readFileAsLines(String filePath) throws IOException {
        return Files.readAllLines(Paths.get(filePath), StandardCharsets.UTF_8);
    }

    public static byte[] readFileAsBytes(String filePath) throws IOException {
        return Files.readAllBytes(Paths.get(filePath));
    }

    public static void writeFile(String filePath, String content) throws IOException {
        Files.writeString(Paths.get(filePath), content, StandardCharsets.UTF_8);
    }

    public static void writeFile(String filePath, byte[] content) throws IOException {
        Files.write(Paths.get(filePath), content);
    }

    public static void appendFile(String filePath, String content) throws IOException {
        Files.writeString(Paths.get(filePath), content, StandardCharsets.UTF_8, 
                         java.nio.file.StandardOpenOption.CREATE, 
                         java.nio.file.StandardOpenOption.APPEND);
    }

    public static void deleteFile(String filePath) throws IOException {
        Files.deleteIfExists(Paths.get(filePath));
    }

    public static void deleteDirectory(String dirPath) throws IOException {
        org.apache.commons.io.FileUtils.deleteDirectory(new File(dirPath));
    }

    public static void createDirectory(String dirPath) throws IOException {
        Files.createDirectories(Paths.get(dirPath));
    }

    public static File createFile(String filePath) throws IOException {
        File file = new File(filePath);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }
        file.createNewFile();
        return file;
    }

    public static void copyFile(String sourcePath, String targetPath) throws IOException {
        Files.copy(Paths.get(sourcePath), Paths.get(targetPath));
    }

    public static void copyDirectory(String sourceDir, String targetDir) throws IOException {
        org.apache.commons.io.FileUtils.copyDirectory(new File(sourceDir), new File(targetDir));
    }

    public static void moveFile(String sourcePath, String targetPath) throws IOException {
        Files.move(Paths.get(sourcePath), Paths.get(targetPath));
    }

    public static void moveDirectory(String sourceDir, String targetDir) throws IOException {
        org.apache.commons.io.FileUtils.moveDirectory(new File(sourceDir), new File(targetDir));
    }

    public static long getFileSize(String filePath) throws IOException {
        return Files.size(Paths.get(filePath));
    }

    public static String getFileName(String filePath) {
        return Paths.get(filePath).getFileName().toString();
    }

    public static String getFileExtension(String filePath) {
        String fileName = getFileName(filePath);
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
    }

    public static String getFileParent(String filePath) {
        Path path = Paths.get(filePath).getParent();
        return path != null ? path.toString() : "";
    }

    public static boolean isFile(String filePath) {
        return Files.isRegularFile(Paths.get(filePath));
    }

    public static boolean isDirectory(String dirPath) {
        return Files.isDirectory(Paths.get(dirPath));
    }

    public static String getCurrentDirectory() {
        return System.getProperty("user.dir");
    }

    public static String getTempDirectory() {
        return System.getProperty("java.io.tmpdir");
    }

    public static String createTempFile(String prefix, String suffix) throws IOException {
        File tempFile = File.createTempFile(prefix, suffix);
        return tempFile.getAbsolutePath();
    }

    public static String createTempDirectory(String prefix) throws IOException {
        Path tempDir = Files.createTempDirectory(prefix);
        return tempDir.toAbsolutePath().toString();
    }

    public static List<String> listFiles(String dirPath) throws IOException {
        return Files.list(Paths.get(dirPath))
                     .map(Path::toString)
                     .toList();
    }

    public static List<String> listFilesWithExtension(String dirPath, String extension) throws IOException {
        return Files.list(Paths.get(dirPath))
                     .filter(path -> path.toString().toLowerCase().endsWith(extension.toLowerCase()))
                     .map(Path::toString)
                     .toList();
    }

    public static void cleanDirectory(String dirPath) throws IOException {
        org.apache.commons.io.FileUtils.cleanDirectory(new File(dirPath));
    }

    public static boolean isFileEmpty(String filePath) throws IOException {
        return Files.size(Paths.get(filePath)) == 0;
    }

    public static String normalizePath(String filePath) {
        return Paths.get(filePath).normalize().toString();
    }
}
