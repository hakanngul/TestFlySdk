package com.testfly.sdk.manager;

import org.apache.logging.log4j.Logger;

public class LogManager {

    private LogManager() {
    }

    public static Logger getLogger(Class<?> clazz) {
        return org.apache.logging.log4j.LogManager.getLogger(clazz);
    }

    public static Logger getLogger(String name) {
        return org.apache.logging.log4j.LogManager.getLogger(name);
    }

    public static void info(Class<?> clazz, String message) {
        getLogger(clazz).info(message);
    }

    public static void info(Class<?> clazz, String message, Object... params) {
        getLogger(clazz).info(message, params);
    }

    public static void debug(Class<?> clazz, String message) {
        getLogger(clazz).debug(message);
    }

    public static void debug(Class<?> clazz, String message, Object... params) {
        getLogger(clazz).debug(message, params);
    }

    public static void warn(Class<?> clazz, String message) {
        getLogger(clazz).warn(message);
    }

    public static void warn(Class<?> clazz, String message, Object... params) {
        getLogger(clazz).warn(message, params);
    }

    public static void error(Class<?> clazz, String message) {
        getLogger(clazz).error(message);
    }

    public static void error(Class<?> clazz, String message, Throwable throwable) {
        getLogger(clazz).error(message, throwable);
    }

    public static void error(Class<?> clazz, String message, Object... params) {
        getLogger(clazz).error(message, params);
    }

    public static void fatal(Class<?> clazz, String message) {
        getLogger(clazz).fatal(message);
    }

    public static void fatal(Class<?> clazz, String message, Throwable throwable) {
        getLogger(clazz).fatal(message, throwable);
    }

    public static void trace(Class<?> clazz, String message) {
        getLogger(clazz).trace(message);
    }

    public static void trace(Class<?> clazz, String message, Object... params) {
        getLogger(clazz).trace(message, params);
    }

    public static void logStep(Class<?> clazz, String stepDescription) {
        getLogger(clazz).info("STEP: " + stepDescription);
    }

    public static void logInfo(Class<?> clazz, String message) {
        getLogger(clazz).info("INFO: " + message);
    }

    public static void logError(Class<?> clazz, String message) {
        getLogger(clazz).error("ERROR: " + message);
    }

    public static void logWarning(Class<?> clazz, String message) {
        getLogger(clazz).warn("WARNING: " + message);
    }
}
