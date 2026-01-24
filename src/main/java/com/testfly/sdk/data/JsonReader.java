package com.testfly.sdk.data;

import com.testfly.sdk.manager.LogManager;
import org.apache.logging.log4j.Logger;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JsonReader {

    private static final Logger logger = LogManager.getLogger(JsonReader.class);
    private static final Gson gson = new Gson();

    public static JsonObject readJsonFile(String filePath) throws IOException {
        logger.info("Reading JSON file: " + filePath);
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            JsonElement element = gson.fromJson(reader, JsonElement.class);
            if (element != null && element.isJsonObject()) {
                logger.info("Successfully parsed JSON file");
                return element.getAsJsonObject();
            }
            throw new RuntimeException("Invalid JSON format or empty file");
        }
    }

    public static <T> T readJsonFile(String filePath, Class<T> clazz) throws IOException {
        logger.info("Reading JSON file to class: " + clazz.getName());
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            T object = gson.fromJson(reader, clazz);
            logger.info("Successfully parsed JSON to object");
            return object;
        }
    }

    public static JsonObject readJsonString(String jsonString) {
        logger.info("Parsing JSON string");
        JsonElement element = gson.fromJson(jsonString, JsonElement.class);
        if (element != null && element.isJsonObject()) {
            return element.getAsJsonObject();
        }
        throw new RuntimeException("Invalid JSON string");
    }

    public static <T> T readJsonString(String jsonString, Class<T> clazz) {
        logger.info("Parsing JSON string to class");
        return gson.fromJson(jsonString, clazz);
    }

    public static <T> List<T> readJsonArray(String filePath, Class<T> clazz) throws IOException {
        logger.info("Reading JSON array from file");
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            JsonElement element = gson.fromJson(reader, JsonElement.class);
            if (element != null && element.isJsonArray()) {
                List<T> list = new ArrayList<>();
                for (JsonElement jsonElement : element.getAsJsonArray()) {
                    T obj = gson.fromJson(jsonElement, clazz);
                    list.add(obj);
                }
                logger.info("Successfully parsed JSON array with " + list.size() + " items");
                return list;
            }
            throw new RuntimeException("Invalid JSON array format");
        }
    }

    public static String writeJson(Object object) {
        logger.info("Writing object to JSON string");
        String json = gson.toJson(object);
        logger.debug("JSON output: " + json);
        return json;
    }

    public static String writePrettyJson(Object object) {
        logger.info("Writing object to pretty JSON string");
        return gson.toJson(object);
    }

    public static Map<String, Object> toMap(JsonObject jsonObject) {
        Map<String, Object> map = new java.util.HashMap<>();
        for (String key : jsonObject.keySet()) {
            map.put(key, jsonObject.get(key));
        }
        return map;
    }

    public static String getValue(JsonObject json, String key) {
        if (json.has(key)) {
            return json.get(key).getAsString();
        }
        return null;
    }

    public static <T> T getValue(JsonObject json, String key, Class<T> clazz) {
        if (json.has(key)) {
            return gson.fromJson(json.get(key), clazz);
        }
        return null;
    }

    public static String[] getValues(JsonObject json, String key) {
        if (json.has(key) && json.get(key).isJsonArray()) {
            List<String> values = new ArrayList<>();
            for (JsonElement element : json.get(key).getAsJsonArray()) {
                values.add(element.getAsString());
            }
            return values.toArray(new String[0]);
        }
        return new String[0];
    }

    public static boolean hasKey(JsonObject json, String key) {
        return json.has(key);
    }

    public static JsonObject getNestedObject(JsonObject json, String... keys) {
        JsonObject current = json;
        for (String key : keys) {
            if (current.has(key) && current.get(key).isJsonObject()) {
                current = current.getAsJsonObject(key);
            } else {
                throw new RuntimeException("Nested key not found or not an object: " + key);
            }
        }
        return current;
    }

    public static String getNestedValue(JsonObject json, String... keys) {
        JsonObject current = json;
        for (int i = 0; i < keys.length - 1; i++) {
            if (current.has(keys[i]) && current.get(keys[i]).isJsonObject()) {
                current = current.getAsJsonObject(keys[i]);
            } else {
                throw new RuntimeException("Nested key not found: " + keys[i]);
            }
        }
        String lastKey = keys[keys.length - 1];
        if (current.has(lastKey)) {
            return current.get(lastKey).getAsString();
        }
        return null;
    }

    public static List<String> getAllKeys(JsonObject json) {
        List<String> keys = new ArrayList<>();
        for (String key : json.keySet()) {
            keys.add(key);
        }
        return keys;
    }

    public static int getSize(JsonObject json) {
        return json.keySet().size();
    }
}
