package me.marvin.achilles.utils.etc;

import org.bukkit.ChatColor;

public class StringUtils {
    public static String colorize(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }
}
