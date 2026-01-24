package com.testfly.sdk.utils;

import com.testfly.sdk.manager.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Random;

public class DateUtils {

    private static final Logger logger = LogManager.getLogger(DateUtils.class);

    public static String getCurrentDate() {
        return LocalDate.now().toString();
    }

    public static String getCurrentDateTime() {
        return LocalDateTime.now().toString();
    }

    public static String getCurrentDate(String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return LocalDate.now().format(formatter);
    }

    public static String getCurrentDateTime(String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return LocalDateTime.now().format(formatter);
    }

    public static String formatDate(LocalDate date, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return date.format(formatter);
    }

    public static String formatDateTime(LocalDateTime dateTime, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return dateTime.format(formatter);
    }

    public static String formatDateTime(java.util.Date date, String pattern) {
        java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat(pattern);
        return formatter.format(date);
    }

    public static LocalDate parseDate(String dateStr, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return LocalDate.parse(dateStr, formatter);
    }

    public static LocalDateTime parseDateTime(String dateTimeStr, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return LocalDateTime.parse(dateTimeStr, formatter);
    }

    public static java.util.Date parseDateTimeToUtilDate(String dateTimeStr, String pattern) {
        java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat(pattern);
        try {
            return formatter.parse(dateTimeStr);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse date: " + dateTimeStr, e);
        }
    }

    public static LocalDate addDays(LocalDate date, int days) {
        return date.plusDays(days);
    }

    public static LocalDate addWeeks(LocalDate date, int weeks) {
        return date.plusWeeks(weeks);
    }

    public static LocalDate addMonths(LocalDate date, int months) {
        return date.plusMonths(months);
    }

    public static LocalDate addYears(LocalDate date, int years) {
        return date.plusYears(years);
    }

    public static LocalDate subtractDays(LocalDate date, int days) {
        return date.minusDays(days);
    }

    public static LocalDate subtractWeeks(LocalDate date, int weeks) {
        return date.minusWeeks(weeks);
    }

    public static LocalDate subtractMonths(LocalDate date, int months) {
        return date.minusMonths(months);
    }

    public static LocalDate subtractYears(LocalDate date, int years) {
        return date.minusYears(years);
    }

    public static long daysBetween(LocalDate startDate, LocalDate endDate) {
        return ChronoUnit.DAYS.between(startDate, endDate);
    }

    public static long weeksBetween(LocalDate startDate, LocalDate endDate) {
        return ChronoUnit.WEEKS.between(startDate, endDate);
    }

    public static long monthsBetween(LocalDate startDate, LocalDate endDate) {
        return ChronoUnit.MONTHS.between(startDate, endDate);
    }

    public static long yearsBetween(LocalDate startDate, LocalDate endDate) {
        return ChronoUnit.YEARS.between(startDate, endDate);
    }

    public static boolean isToday(LocalDate date) {
        return date.equals(LocalDate.now());
    }

    public static boolean isYesterday(LocalDate date) {
        return date.equals(LocalDate.now().minusDays(1));
    }

    public static boolean isTomorrow(LocalDate date) {
        return date.equals(LocalDate.now().plusDays(1));
    }

    public static boolean isWeekend(LocalDate date) {
        DayOfWeek day = date.getDayOfWeek();
        return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
    }

    public static boolean isWeekday(LocalDate date) {
        return !isWeekend(date);
    }

    public static boolean isBefore(LocalDate date, LocalDate otherDate) {
        return date.isBefore(otherDate);
    }

    public static boolean isAfter(LocalDate date, LocalDate otherDate) {
        return date.isAfter(otherDate);
    }

    public static boolean isBetween(LocalDate date, LocalDate startDate, LocalDate endDate) {
        return (date.isEqual(startDate) || date.isAfter(startDate)) && 
               (date.isEqual(endDate) || date.isBefore(endDate));
    }

    public static LocalDate getFirstDayOfMonth(LocalDate date) {
        return date.with(TemporalAdjusters.firstDayOfMonth());
    }

    public static LocalDate getLastDayOfMonth(LocalDate date) {
        return date.with(TemporalAdjusters.lastDayOfMonth());
    }

    public static LocalDate getFirstDayOfNextMonth(LocalDate date) {
        return date.plusMonths(1).with(TemporalAdjusters.firstDayOfMonth());
    }

    public static LocalDate getLastDayOfPreviousMonth(LocalDate date) {
        return date.minusMonths(1).with(TemporalAdjusters.lastDayOfMonth());
    }

    public static String getDayName(LocalDate date) {
        DayOfWeek day = date.getDayOfWeek();
        return day.toString();
    }

    public static String getMonthName(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM");
        return date.format(formatter);
    }

    public static int getDayOfMonth(LocalDate date) {
        return date.getDayOfMonth();
    }

    public static int getMonthValue(LocalDate date) {
        return date.getMonthValue();
    }

    public static int getYear(LocalDate date) {
        return date.getYear();
    }

    public static String getTimeAgo(LocalDateTime dateTime) {
        LocalDateTime now = LocalDateTime.now();
        long seconds = ChronoUnit.SECONDS.between(dateTime, now);
        
        if (seconds < 60) {
            return seconds + " seconds ago";
        } else if (seconds < 3600) {
            long minutes = seconds / 60;
            return minutes + " minutes ago";
        } else if (seconds < 86400) {
            long hours = seconds / 3600;
            return hours + " hours ago";
        } else if (seconds < 2592000) {
            long days = seconds / 86400;
            return days + " days ago";
        } else if (seconds < 31536000) {
            long months = seconds / 2592000;
            return months + " months ago";
        } else {
            long years = seconds / 31536000;
            return years + " years ago";
        }
    }

    public static List<LocalDate> getLastNDays(int n) {
        List<LocalDate> dates = new java.util.ArrayList<>();
        LocalDate today = LocalDate.now();
        for (int i = 0; i < n; i++) {
            dates.add(today.minusDays(i));
        }
        return dates;
    }

    public static List<LocalDate> getNextNDays(int n) {
        List<LocalDate> dates = new java.util.ArrayList<>();
        LocalDate today = LocalDate.now();
        for (int i = 1; i <= n; i++) {
            dates.add(today.plusDays(i));
        }
        return dates;
    }

    public static List<LocalDate> getDaysBetween(LocalDate startDate, LocalDate endDate) {
        List<LocalDate> dates = new java.util.ArrayList<>();
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            dates.add(current);
            current = current.plusDays(1);
        }
        return dates;
    }

    public static boolean isLeapYear(int year) {
        return java.time.Year.of(year).isLeap();
    }

    public static int getDaysInMonth(int year, int month) {
        return java.time.YearMonth.of(year, month).lengthOfMonth();
    }

    public static String getAge(java.util.Date birthDate) {
        LocalDate birth = LocalDate.ofInstant(birthDate.toInstant(), java.time.ZoneId.systemDefault());
        LocalDate today = LocalDate.now();
        int years = java.time.Period.between(birth, today).getYears();
        int months = java.time.Period.between(birth, today).getMonths();
        return years + " years, " + months + " months";
    }

    public static java.util.Date convertToDate(LocalDate localDate) {
        return java.sql.Date.valueOf(localDate);
    }

    public static java.util.Date convertToDateTime(LocalDateTime localDateTime) {
        return java.sql.Timestamp.valueOf(localDateTime);
    }

    public static LocalDate convertToLocalDate(java.util.Date date) {
        return date.toInstant()
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDate();
    }

    public static LocalDateTime convertToLocalDateTime(java.util.Date date) {
        return date.toInstant()
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public static java.time.Instant toInstant(LocalDateTime localDateTime) {
        return localDateTime.atZone(java.time.ZoneId.systemDefault()).toInstant();
    }

    public static long toTimestamp(LocalDateTime localDateTime) {
        return localDateTime.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static LocalDate getRandomDate(LocalDate start, LocalDate end) {
        long daysBetween = ChronoUnit.DAYS.between(start, end);
        Random random = new Random();
        long randomDays = random.nextLong(daysBetween + 1);
        return start.plusDays(randomDays);
    }

    public static LocalDateTime getRandomDateTime(LocalDateTime start, LocalDateTime end) {
        long secondsBetween = ChronoUnit.SECONDS.between(start, end);
        Random random = new Random();
        long randomSeconds = random.nextLong(secondsBetween + 1);
        return start.plusSeconds(randomSeconds);
    }

    public static boolean isValidDate(String dateStr, String pattern) {
        try {
            parseDate(dateStr, pattern);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String getQuarter(LocalDate date) {
        int month = date.getMonthValue();
        return "Q" + ((month - 1) / 3 + 1);
    }

    public static int getWeekOfYear(LocalDate date) {
        java.time.temporal.WeekFields weekFields = java.time.temporal.WeekFields.of(java.util.Locale.getDefault());
        return date.get(weekFields.weekOfWeekBasedYear());
    }
}
