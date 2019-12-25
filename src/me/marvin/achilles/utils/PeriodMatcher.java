package me.marvin.achilles.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PeriodMatcher {
    private static final Pattern PERIOD_PATTERN = Pattern.compile("\\d+\\D+");
    private static final String DATE_REGEX = "(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)";

    public static long parsePeriod(String input) {
        long totalTime = 0L;
        boolean found = false;
        Matcher matcher = PERIOD_PATTERN.matcher(input);
        while (matcher.find()) {
            found = true;
            long value = Long.parseLong(matcher.group().split(DATE_REGEX)[0]);
            String type = matcher.group().split(DATE_REGEX)[1];
            switch (type) {
                case "s": {
                    totalTime += value;
                    break;
                }
                case "m": {
                    totalTime += value * 60;
                    break;
                }
                case "h": {
                    totalTime += value * 60 * 60;
                    break;
                }
                case "d": {
                    totalTime += value * 60 * 60 * 24;
                    break;
                }
                case "w": {
                    totalTime += value * 60 * 60 * 24 * 7;
                    break;
                }
                case "M": {
                    totalTime += value * 60 * 60 * 24 * 30;
                    break;
                }
                case "y": {
                    totalTime += value * 60 * 60 * 24 * 365;
                    break;
                }
            }
        }
        return !found ? -1 : totalTime * 1000;
    }
}
