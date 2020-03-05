package me.marvin.achilles.utils.etc;

import me.marvin.achilles.Achilles;
import me.marvin.achilles.profile.impl.SimpleProfile;
import me.marvin.achilles.punishment.expiry.PunishmentExpiryLimit;
import me.marvin.achilles.utils.Pair;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

/*
 * Copyright (c) 2019-Present marvintheskid (Kovács Márton)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 * OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

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
