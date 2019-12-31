package me.marvin.achilles.utils.etc;

import me.marvin.achilles.Achilles;
import me.marvin.achilles.profile.impl.SimpleProfile;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

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
        if (name == null) name = "-";
        return name;
    }

    public static String getPlayerName(UUID from) {
        OfflinePlayer onlineTarget = Bukkit.getPlayer(from);
        String name;

        if (onlineTarget == null) {
            onlineTarget = Bukkit.getOfflinePlayer(from);
            if (onlineTarget.getName() == null) {
                name = new SimpleProfile(from).getName();
            } else {
                name = onlineTarget.getName();
            }
        } else {
            name = onlineTarget.getName();
        }
        if (name == null) name = "-";
        return name;
    }
}
