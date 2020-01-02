package me.marvin.achilles.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PeriodMatcher {
    private static final Pattern PERIOD_PATTERN = Pattern.compile("\\d+\\D+");
    private static final String DATE_REGEX = "(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)";

    public static long parsePeriod(String input) {
        if (input == null) return -1;
        long totalTime = 0L;
        boolean found = false;
        Matcher matcher = PERIOD_PATTERN.matcher(input);
        while (matcher.find()) {
            found = true;
            long value = Long.parseLong(matcher.group().split(DATE_REGEX)[0]);
            String type = matcher.group().split(DATE_REGEX)[1];
            switch (type) {
                case "y": {
                    totalTime += value * 31536000;
                    break;
                }
                case "M": {
                    totalTime += value * (31536000 / 12);
                    break;
                }
                case "w": {
                    totalTime += value * 604800;
                    break;
                }
                case "d": {
                    totalTime += value * 86400;
                    break;
                }
                case "h": {
                    totalTime += value * 3600;
                    break;
                }
                case "m": {
                    totalTime += value * 60;
                    break;
                }
                case "s": {
                    totalTime += value;
                    break;
                }
                default: {
                    return -1;
                }
            }
        }
        return !found ? -1 : totalTime * 1000;
    }

    public static String parsePeriod(long millis) {
        if (millis == -1) return "-";
        long temp = (millis / 1000);
        long years = temp / 31536000; temp %= 31536000;
        long months = temp / (31536000 / 12); temp %= (31536000 / 12);
        long weeks = temp / 604800; temp %= 604800;
        long days = temp / 86400; temp %= 86400;
        long hours = temp / 3600; temp %= 3600;
        long minutes = temp / 60; temp %= 60;
        long seconds = temp;

        StringBuilder sb = new StringBuilder();

        if (years > 0L) sb.append(years).append("y");
        if (months > 0L) sb.append(months).append("M");
        if (weeks > 0L) sb.append(weeks).append("w");
        if (days > 0L) sb.append(days).append("d");
        if (hours > 0L) sb.append(hours).append("h");
        if (minutes > 0L) sb.append(minutes).append("m");
        if (seconds == 0 && sb.toString().trim().equals("")) return "-";
        if (seconds > 0L) sb.append(seconds).append("s");
        return sb.toString().trim();
    }
}
