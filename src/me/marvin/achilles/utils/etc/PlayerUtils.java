package me.marvin.achilles.utils.etc;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class PlayerUtils {
    public static String getPlayerName(String from) {
        OfflinePlayer onlineTarget = Bukkit.getPlayer(from);
        String name;

        if (onlineTarget == null) {
            onlineTarget = Bukkit.getOfflinePlayer(from);
            if (onlineTarget.getName() == null) {
                name = from;
            } else {
                name = onlineTarget.getName();
            }
        } else {
            name = onlineTarget.getName();
        }
        return name;
    }
}
