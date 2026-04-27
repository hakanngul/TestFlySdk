package com.testfly.sdk.utils;

import com.testfly.sdk.core.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class StringUtils {

    private static final Logger logger = LogManager.getLogger(StringUtils.class);

    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    public static String trim(String str) {
        return str != null ? str.trim() : null;
    }

    public static String toLowerCase(String str) {
        return str != null ? str.toLowerCase() : null;
    }

    public static String toUpperCase(String str) {
        return str != null ? str.toUpperCase() : null;
    }

    public static String capitalize(String str) {
        if (isEmpty(str)) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + (str.length() > 1 ? str.substring(1).toLowerCase() : "");
    }

    public static String reverse(String str) {
        return new StringBuilder(str).reverse().toString();
    }

    public static String repeat(String str, int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(str);
        }
        return sb.toString();
    }

    public static String remove(String str, String toRemove) {
        return str.replace(toRemove, "");
    }

    public static String removeAll(String str, String... toRemove) {
        String result = str;
        for (String s : toRemove) {
            result = result.replace(s, "");
        }
        return result;
    }

    public static String replace(String str, String target, String replacement) {
        return str.replace(target, replacement);
    }

    public static String replaceAll(String str, String target, String replacement) {
        return str.replaceAll(target, replacement);
    }

    public static List<String> split(String str, String delimiter) {
        List<String> parts = new ArrayList<>();
        if (str == null) {
            return parts;
        }
        String[] split = str.split(delimiter);
        parts.addAll(Arrays.asList(split));
        return parts;
    }

    public static String join(String delimiter, String... parts) {
        return String.join(delimiter, parts);
    }

    public static String join(String delimiter, List<String> parts) {
        return String.join(delimiter, parts);
    }

    public static String left(String str, int length) {
        if (str == null || str.length() <= length) {
            return str;
        }
        return str.substring(0, length);
    }

    public static String right(String str, int length) {
        if (str == null || str.length() <= length) {
            return str;
        }
        return str.substring(str.length() - length);
    }

    public static String substring(String str, int startIndex, int endIndex) {
        if (str == null) {
            return null;
        }
        return str.substring(startIndex, endIndex);
    }

    public static int indexOf(String str, String substring) {
        return str != null ? str.indexOf(substring) : -1;
    }

    public static int lastIndexOf(String str, String substring) {
        return str != null ? str.lastIndexOf(substring) : -1;
    }

    public static boolean contains(String str, String substring) {
        return str != null && str.contains(substring);
    }

    public static boolean containsIgnoreCase(String str, String substring) {
        return str != null && str.toLowerCase().contains(substring.toLowerCase());
    }

    public static boolean startsWith(String str, String prefix) {
        return str != null && str.startsWith(prefix);
    }

    public static boolean endsWith(String str, String suffix) {
        return str != null && str.endsWith(suffix);
    }

    public static String substringAfter(String str, String delimiter) {
        if (!contains(str, delimiter)) {
            return "";
        }
        int index = str.indexOf(delimiter);
        return str.substring(index + delimiter.length());
    }

    public static String substringBefore(String str, String delimiter) {
        if (!contains(str, delimiter)) {
            return str;
        }
        int index = str.indexOf(delimiter);
        return str.substring(0, index);
    }

    public static String substringBetween(String str, String start, String end) {
        String afterStart = substringAfter(str, start);
        if (contains(afterStart, end)) {
            return substringBefore(afterStart, end);
        }
        return "";
    }

    public static String strip(String str, String... chars) {
        String result = str;
        for (String c : chars) {
            result = result.replace(c, "");
        }
        return result;
    }

    public static String stripStart(String str, String... chars) {
        String result = str;
        for (String c : chars) {
            while (result.startsWith(c)) {
                result = result.substring(c.length());
            }
        }
        return result;
    }

    public static String stripEnd(String str, String... chars) {
        String result = str;
        for (String c : chars) {
            while (result.endsWith(c)) {
                result = result.substring(0, result.length() - c.length());
            }
        }
        return result;
    }

    public static String padLeft(String str, int length, char padChar) {
        if (str.length() >= length) {
            return str;
        }
        StringBuilder sb = new StringBuilder();
        int padLength = length - str.length();
        for (int i = 0; i < padLength; i++) {
            sb.append(padChar);
        }
        sb.append(str);
        return sb.toString();
    }

    public static String padRight(String str, int length, char padChar) {
        if (str.length() >= length) {
            return str;
        }
        StringBuilder sb = new StringBuilder(str);
        int padLength = length - str.length();
        sb.append(String.valueOf(padChar).repeat(padLength));
        return sb.toString();
    }

    public static String center(String str, int length, char padChar) {
        if (str.length() >= length) {
            return str;
        }
        int totalPad = length - str.length();
        int leftPad = totalPad / 2;
        int rightPad = totalPad - leftPad;
        return padRight(padLeft(str, str.length() + leftPad, padChar), str.length() + leftPad + rightPad, padChar);
    }

    public static String mask(String str, int visibleChars) {
        if (isEmpty(str)) {
            return str;
        }
        if (str.length() <= visibleChars) {
            return str;
        }
        return str.substring(0, visibleChars) + repeat("*", str.length() - visibleChars);
    }

    public static String maskEmail(String email) {
        if (isEmpty(email)) {
            return email;
        }
        int atIndex = email.indexOf('@');
        if (atIndex == -1) {
            return email;
        }
        String username = email.substring(0, atIndex);
        String domain = email.substring(atIndex);
        int usernameVisible = Math.max(1, username.length() / 2);
        int domainVisible = Math.max(3, domain.length() / 2);
        return mask(username, usernameVisible) + "@" + mask(domain, domainVisible);
    }

    public static String generateRandomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }

    public static String formatCamelCase(String str) {
        if (isEmpty(str)) {
            return str;
        }
        String[] words = str.split("[\\s_]+");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            if (i == 0) {
                sb.append(word.toLowerCase());
            } else {
                sb.append(Character.toUpperCase(word.charAt(0)));
                sb.append(word.substring(1).toLowerCase());
            }
        }
        return sb.toString();
    }

    public static String formatSnakeCase(String str) {
        if (isEmpty(str)) {
            return str;
        }
        return str.trim().replaceAll("\\s+", "_").toLowerCase();
    }

    public static String formatKebabCase(String str) {
        if (isEmpty(str)) {
            return str;
        }
        return str.trim().replaceAll("\\s+", "-").toLowerCase();
    }

    public static String formatPascalCase(String str) {
        if (isEmpty(str)) {
            return str;
        }
        String[] words = str.split("[\\s_-]+");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            sb.append(Character.toUpperCase(word.charAt(0)));
            sb.append(word.substring(1).toLowerCase());
        }
        return sb.toString();
    }

    public static String formatSentenceCase(String str) {
        if (isEmpty(str)) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + (str.length() > 1 ? str.substring(1).toLowerCase() : "");
    }

    public static String formatTitleCase(String str) {
        if (isEmpty(str)) {
            return str;
        }
        String[] words = str.split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (word.length() > 0) {
                sb.append(Character.toUpperCase(word.charAt(0)));
                if (word.length() > 1) {
                    sb.append(word.substring(1).toLowerCase());
                }
            }
            if (words.length > 0) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    public static String cleanWhitespace(String str) {
        if (str == null) {
            return null;
        }
        return str.replaceAll("\\s+", " ").trim();
    }

    public static String removeExtraSpaces(String str) {
        if (str == null) {
            return null;
        }
        return str.replaceAll(" +", " ");
    }

    public static boolean isNumeric(String str) {
        if (isEmpty(str)) {
            return false;
        }
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isAlpha(String str) {
        if (isEmpty(str)) {
            return false;
        }
        return str.matches("^[a-zA-Z]+$");
    }

    public static boolean isAlphanumeric(String str) {
        if (isEmpty(str)) {
            return false;
        }
        return str.matches("^[a-zA-Z0-9]+$");
    }

    public static boolean isEmail(String str) {
        if (isEmpty(str)) {
            return false;
        }
        return str.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    public static boolean isUrl(String str) {
        if (isEmpty(str)) {
            return false;
        }
        try {
            new java.net.URL(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static int countMatches(String str, String regex) {
        if (str == null) {
            return 0;
        }
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);
        java.util.regex.Matcher matcher = pattern.matcher(str);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }

    public static String extractNumbers(String str) {
        if (isEmpty(str)) {
            return "";
        }
        return str.replaceAll("[^0-9]", "");
    }

    public static String extractLetters(String str) {
        if (isEmpty(str)) {
            return "";
        }
        return str.replaceAll("[^a-zA-Z]", "");
    }

    public static String truncate(String str, int maxLength) {
        if (str == null || str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength) + "...";
    }

    public static String encodeBase64(String str) {
        return java.util.Base64.getEncoder().encodeToString(str.getBytes());
    }

    public static String decodeBase64(String str) {
        return new String(java.util.Base64.getDecoder().decode(str));
    }

    public static String escapeHtml(String str) {
        if (str == null) {
            return null;
        }
        return str.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

    public static String unescapeHtml(String str) {
        if (str == null) {
            return null;
        }
        return str.replace("&amp;", "&")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&quot;", "\"")
                .replace("&apos;", "'");
    }

    public static String truncateWords(String str, int maxWords) {
        if (isEmpty(str)) {
            return str;
        }
        String[] words = str.split("\\s+");
        int wordCount = Math.min(maxWords, words.length);
        return join(" ", Arrays.copyOf(words, wordCount));
    }
}
