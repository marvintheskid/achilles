package me.marvin.achilles.utils;

import me.marvin.achilles.Variables;

public class TimeFormatter {
    public static String formatTime(long millis) {
        if (millis == -1) return Variables.Date.PERMANENT;
        long seconds = millis / 1000L;
        long minutes = seconds / 60L;
        long hours = minutes / 60L;
        long days = hours / 24L;
        long weeks = days / 7L;
        long months = weeks / 4L;
        long years = months / 12L;

        StringBuilder sb = new StringBuilder();

        if (years > 0L) {
            sb.append(years).append(" ").append((years == 1L) ? Variables.Date.YEAR : Variables.Date.YEARS).append(" ");
        }
        if (months > 0L) {
            sb.append(months).append(" ").append((months == 1L) ? Variables.Date.MONTH : Variables.Date.MONTHS).append(" ");
        }
        if (weeks > 0L) {
            sb.append(weeks).append(" ").append((weeks == 1L) ? Variables.Date.WEEK : Variables.Date.WEEKS).append(" ");
        }
        if (days > 0L) {
            sb.append(days).append(" ").append((days == 1L) ? Variables.Date.DAY : Variables.Date.DAYS).append(" ");
        }
        if (hours > 0L) {
            sb.append(hours).append(" ").append((hours == 1L) ? Variables.Date.HOUR : Variables.Date.HOURS).append(" ");
        }
        if (minutes > 0L) {
            sb.append(minutes).append(" ").append((minutes == 1L) ? Variables.Date.MINUTE : Variables.Date.MINUTES).append(" ");
        }

        if (seconds == 0 && sb.toString().trim().equals("")) {
            return Variables.Date.EXPIRED;
        }

        sb.append(seconds).append(" ").append((seconds == 1L) ? Variables.Date.SECOND : Variables.Date.SECONDS).append(" ");
        return sb.toString().trim();
    }
}
