package me.marvin.achilles.utils.etc;

import me.marvin.achilles.utils.Pair;
import org.bukkit.ChatColor;

import java.util.*;
import java.util.stream.Collectors;

public class StringUtils {
    public static String colorize(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }

    public static String build(String[] arr, int index) {
        return build(arr, index, true);
    }

    public static String build(String[] arr, int index, boolean trim) {
        StringBuilder builder = new StringBuilder();
        for (int i = index; i < arr.length; i++) {
            if (!builder.toString().trim().equals("")) builder.append(" ");
            builder.append(arr[i]);
        }
        String result = builder.toString();
        if (trim) result = result.trim();
        return result;
    }

    public static boolean isSilent(String reason) {
        String[] split = reason.split(" ");
        boolean silent = false;
        String flags = "-p";

        if (split[split.length - 1].startsWith("-")) {
            flags = split[split.length - 1];
        }

        for (char c : flags.replace("-", "").toCharArray()) {
            if (c == 's') {
                silent = true;
            } else if (c == 'p') {
                silent = false;
            }
        }
        return silent;
    }

    public static String formatReason(String reason) {
        String[] split = reason.split(" ");
        int length = " -s".length();

        if (split[split.length - 1].startsWith("-")) {
            reason = reason.substring(0, reason.length() - length);
        }
        return reason;
    }

    public static Pair<String, Boolean> formatFully(String[] args, int startIndex, String defaultReason) {
        boolean silent;
        if (args.length == startIndex) {
            silent = isSilent(defaultReason);
            return new Pair<>(formatReason(defaultReason), silent);
        } else {
            String reason = build(args, startIndex);
            if (reason.equalsIgnoreCase("-s")) {
                silent = true;
                if (isSilent(defaultReason)) {
                    reason = formatReason(defaultReason);
                } else {
                    reason = formatReason(formatReason(defaultReason) + " -s");
                }
            } else if (reason.equalsIgnoreCase("-p")) {
                silent = false;
                if (!isSilent(defaultReason)) {
                    reason = formatReason(defaultReason);
                } else {
                    reason = formatReason(formatReason(defaultReason) + " -p");
                }
            } else {
                silent = isSilent(reason);
                reason = formatReason(reason);
            }
            return new Pair<>(reason, silent);
        }
    }
}
