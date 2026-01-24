package com.testfly.sdk.utils;

import java.util.Random;
import java.util.UUID;

public class RandomUtils {

    private static final ThreadLocal<Random> randomThreadLocal = ThreadLocal.withInitial(Random::new);
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final String ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final String NUMERIC = "0123456789";

    private RandomUtils() {
    }

    public static String randomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        Random random = randomThreadLocal.get();
        for (int i = 0; i < length; i++) {
            sb.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }

    public static String randomAlphanumeric(int length) {
        StringBuilder sb = new StringBuilder(length);
        Random random = randomThreadLocal.get();
        for (int i = 0; i < length; i++) {
            sb.append(ALPHANUMERIC.charAt(random.nextInt(ALPHANUMERIC.length())));
        }
        return sb.toString();
    }

    public static String randomNumeric(int length) {
        StringBuilder sb = new StringBuilder(length);
        Random random = randomThreadLocal.get();
        for (int i = 0; i < length; i++) {
            sb.append(NUMERIC.charAt(random.nextInt(NUMERIC.length())));
        }
        return sb.toString();
    }

    public static int randomNumber(int min, int max) {
        return randomThreadLocal.get().nextInt(max - min + 1) + min;
    }

    public static long randomNumber(long min, long max) {
        Random random = randomThreadLocal.get();
        return min + (long) (random.nextDouble() * (max - min + 1));
    }

    public static double randomDouble(double min, double max) {
        return min + randomThreadLocal.get().nextDouble() * (max - min);
    }

    public static boolean randomBoolean() {
        return randomThreadLocal.get().nextBoolean();
    }

    public static String randomEmail() {
        return randomString(8).toLowerCase() + "@" + randomString(5).toLowerCase() + ".com";
    }

    public static String randomPhoneNumber() {
        return "+1" + randomNumeric(10);
    }

    public static String randomPassword(int length) {
        return randomAlphanumeric(length);
    }

    public static String randomUrl() {
        return "https://www." + randomString(8).toLowerCase() + ".com";
    }

    public static String randomUUID() {
        return UUID.randomUUID().toString();
    }

    public static <T> T randomFromList(T[] items) {
        if (items == null || items.length == 0) {
            throw new IllegalArgumentException("List cannot be null or empty");
        }
        return items[randomThreadLocal.get().nextInt(items.length)];
    }

    public static String randomZipCode() {
        return randomNumeric(5) + "-" + randomNumeric(4);
    }

    public static String randomSSN() {
        return randomNumeric(3) + "-" + randomNumeric(2) + "-" + randomNumeric(4);
    }

    public static String randomCreditCard() {
        return randomNumeric(16);
    }

    public static String randomMonth() {
        return String.format("%02d", randomNumber(1, 12));
    }

    public static String randomYear(int minYear, int maxYear) {
        return String.valueOf(randomNumber(minYear, maxYear));
    }

    public static String randomDate(int minYear, int maxYear) {
        int year = randomNumber(minYear, maxYear);
        int month = randomNumber(1, 12);
        int day = randomNumber(1, 28);
        return String.format("%04d-%02d-%02d", year, month, day);
    }
}
