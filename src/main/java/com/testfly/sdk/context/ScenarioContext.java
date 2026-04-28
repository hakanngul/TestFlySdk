package com.testfly.sdk.context;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Thread-Safe repository used to carry data throughout the test scenario
 * (Runtime).
 * <p>
 * Features:
 * 1. Thread-Safe: Data does not get mixed during parallel test runs.
 * 2. Type-Safe: Reduces the risk of casting errors thanks to generic methods.
 * 3. Logged: Data addition/reading operations are logged, making debugging
 * easier.
 * </p>
 */
public class ScenarioContext {

    private static final Logger logger = LogManager.getLogger(ScenarioContext.class);

    // Holds an isolated HashMap for each thread (Test).
    private static final ThreadLocal<Map<String, Object>> threadLocalContext = ThreadLocal.withInitial(HashMap::new);

    private ScenarioContext() {
        // Static class, cannot be instantiated.
    }

    // ========================================================================
    // 1. SET METHODS (DATA SAVING)
    // ========================================================================

    /**
     * Adds data to Context with an Enum key. (Recommended Method)
     * 
     * @param key   ContextKey Enum
     * @param value Object to be stored
     */
    public static void set(ContextKey key, Object value) {
        set(key.name(), value);
    }

    /**
     * Adds data to Context with a String key. (For dynamic situations)
     * 
     * @param key   String key
     * @param value Object to be stored
     */
    public static void set(String key, Object value) {
        // You can be careful when logging sensitive data, but it's valuable for
        // debugging.
        logger.info(String.format("Context SET -> Key: [%s] | Value: [%s]", key, value));
        threadLocalContext.get().put(key, value);
    }

    // ========================================================================
    // 2. GET METHODS (DATA READING)
    // ========================================================================

    /**
     * Returns data as Object. Casting operation is left to the user.
     */
    public static Object get(ContextKey key) {
        return threadLocalContext.get().get(key.name());
    }

    /**
     * Returns data as String. Returns null if null.
     */
    public static String getString(ContextKey key) {
        Object value = get(key);
        return value != null ? value.toString() : null;
    }

    /**
     * Returns data as Integer.
     * Parses even if both Integer object and String ("123") are received.
     */
    public static Integer getInt(ContextKey key) {
        Object value = get(key);
        if (value == null)
            return null;

        try {
            if (value instanceof Integer)
                return (Integer) value;
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            String errorMsg = String.format(
                    "Context Cast Error: Key [%s] value could not be converted to Integer. Value: [%s]", key, value);
            logger.error(errorMsg);
            throw new RuntimeException(errorMsg);
        }
    }

    /**
     * Returns data as Boolean ("true", true).
     */
    public static Boolean getBoolean(ContextKey key) {
        Object value = get(key);
        if (value == null)
            return null;

        if (value instanceof Boolean)
            return (Boolean) value;
        return Boolean.parseBoolean(value.toString());
    }

    /**
     * GENERIC GET: Returns data by casting it to the desired Class type.
     * This is the safest method.
     *
     * Example: UserData user = ScenarioContext.get(ContextKey.CURRENT_USER,
     * UserData.class);
     */
    public static <T> T get(ContextKey key, Class<T> type) {
        Object value = get(key);
        if (value == null)
            return null;

        if (type.isInstance(value)) {
            return type.cast(value);
        }

        String errorMsg = String.format("Context Cast Error: Key [%s] beklenen tip [%s] değil. Bulunan tip: [%s]",
                key, type.getSimpleName(), value.getClass().getSimpleName());
        logger.error(errorMsg);
        throw new ClassCastException(errorMsg);
    }

    // ========================================================================
    // 3. UTILITY METHODS
    // ========================================================================

    public static boolean contains(ContextKey key) {
        return threadLocalContext.get().containsKey(key.name());
    }

    public static void remove(ContextKey key) {
        logger.info("Context REMOVE -> Key: [" + key + "]");
        threadLocalContext.get().remove(key.name());
    }

    /**
     * CRITICAL METHOD: Clears memory at the end of the test.
     * Should be called in BaseWebTest @AfterMethod.
     */
    public static void clear() {
        threadLocalContext.get().clear();
        threadLocalContext.remove(); // Memory Leak önlemi
    }

    // ========================================================================
    // 4. CONTEXT KEYS (ENUM)
    // ========================================================================

    /**
     * Standard keys in the project are defined here.
     * Used to prevent typos.
     */
    public enum ContextKey {
        // --- AUTH ---
        ACCESS_TOKEN,
        REFRESH_TOKEN,
        CURRENT_USER_EMAIL,
        OTP_CODE,

        // --- ORDER & CHECKOUT ---
        ORDER_ID,
        ORDER_TOTAL_AMOUNT,
        SELECTED_PRODUCT_NAME,

        // --- SYSTEM ---
        LAST_API_RESPONSE,
        ERROR_MESSAGE
    }
}