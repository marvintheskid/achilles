package me.marvin.achilles.utils;

import me.marvin.achilles.Variables;

public class TimeFormatter {
    public static String formatTime(long millis) {
        if (millis == -1) return Variables.Date.PERMANENT;
        long temp = (millis / 1000);
        long years = temp / 31536000; temp %= 31536000;
        long months = temp / (31536000 / 12); temp %= (31536000 / 12);
        long weeks = temp / 604800; temp %= 604800;
        long days = temp / 86400; temp %= 86400;
        long hours = temp / 3600; temp %= 3600;
        long minutes = temp / 60; temp %= 60;
        long seconds = temp;

        StringBuilder sb = new StringBuilder();

        if (years > 0L) sb.append(years).append(" ").append((years == 1L) ? Variables.Date.YEAR : Variables.Date.YEARS).append(" ");
        if (months > 0L) sb.append(months).append(" ").append((months == 1L) ? Variables.Date.MONTH : Variables.Date.MONTHS).append(" ");
        if (weeks > 0L) sb.append(weeks).append(" ").append((weeks == 1L) ? Variables.Date.WEEK : Variables.Date.WEEKS).append(" ");
        if (days > 0L) sb.append(days).append(" ").append((days == 1L) ? Variables.Date.DAY : Variables.Date.DAYS).append(" ");
        if (hours > 0L) sb.append(hours).append(" ").append((hours == 1L) ? Variables.Date.HOUR : Variables.Date.HOURS).append(" ");
        if (minutes > 0L) sb.append(minutes).append(" ").append((minutes == 1L) ? Variables.Date.MINUTE : Variables.Date.MINUTES).append(" ");
        if (seconds == 0 && sb.toString().trim().equals("")) return Variables.Date.EXPIRED;
        if (seconds > 0) sb.append(seconds).append(" ").append((seconds == 1L) ? Variables.Date.SECOND : Variables.Date.SECONDS).append(" ");
        return sb.toString().trim();
    }
}
