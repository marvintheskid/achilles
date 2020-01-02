package me.marvin.achilles.utils.etc;

import me.marvin.achilles.Achilles;
import me.marvin.achilles.profile.impl.SimpleProfile;
import me.marvin.achilles.punishment.expiry.PunishmentExpiryLimit;
import me.marvin.achilles.utils.Pair;
import me.marvin.achilles.utils.PeriodMatcher;
import me.marvin.achilles.utils.TimeFormatter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public static Pair<Boolean, Long> canIssue(Player player, String type, long issued) {
        if (issued == -1) return new Pair<>(false, -1L);
        long highest = -1;
        for (Map.Entry<String, PunishmentExpiryLimit> entry : Achilles.getExpiryData().entrySet()) {
            if (!player.hasPermission(entry.getValue().getPermission())) continue;
            switch (type) {
                case "ban": {
                    highest = Math.max(highest, entry.getValue().getMaxBanLength());
                    break;
                }
                case "mute": {
                    highest = Math.max(highest, entry.getValue().getMaxMuteLength());
                    break;
                }
            }
        }
        return new Pair<>(issued <= highest, highest);
    }
}
