package me.marvin.achilles.listener;

import me.marvin.achilles.Achilles;
import me.marvin.achilles.Language;
import me.marvin.achilles.Variables;
import me.marvin.achilles.profile.impl.FullProfile;
import me.marvin.achilles.punishment.ExpirablePunishment;
import me.marvin.achilles.punishment.Punishment;
import me.marvin.achilles.punishment.impl.Ban;
import me.marvin.achilles.punishment.impl.Blacklist;
import me.marvin.achilles.utils.TimeFormatter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static me.marvin.achilles.utils.etc.PlayerUtils.getPlayerName;
import static me.marvin.achilles.utils.etc.StringUtils.colorize;

/*
 * Copyright (c) 2019 marvintheskid (Kovács Márton)
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

public class LoginListener implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    void onLogin(AsyncPlayerPreLoginEvent e) {
        UUID uuid = e.getUniqueId();
        FullProfile profile = Achilles.getProfileHandler().getOrCreateProfile(uuid, false);

        if (profile == null) {
            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, colorize(Language.Other.FAILED_TO_LOAD));
            return;
        }

        profile.setUsername(e.getName());
        profile.getPunishments(ExpirablePunishment.class).forEach(punishment -> {
            if (punishment.isExpired() && punishment.isActive()) {
                punishment.expire();
            }
        });

        Optional<Blacklist> optionalBlacklist = profile.getActive(Blacklist.class);
        if (optionalBlacklist.isPresent()) {
            Blacklist blacklist = optionalBlacklist.get();
            String issuerName;

            if (blacklist.getIssuer() == Punishment.CONSOLE_UUID) {
                issuerName = Language.Other.CONSOLE_NAME;
            } else {
                issuerName = getPlayerName(blacklist.getIssuer());
            }

            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, colorize(Language.Blacklist.PUNISHMENT_MESSAGE
                .replace("{issuer}", issuerName)
                .replace("{target}", e.getName())
                .replace("{reason}", blacklist.getIssueReason())
                .replace("{server}", blacklist.getServer())
            ));
            Achilles.getProfileHandler().getProfiles().remove(uuid);
            return;
        }

        Optional<Ban> optionalBan = profile.getActive(Ban.class);
        if (optionalBan.isPresent()) {
            Ban ban = optionalBan.get();
            String issuerName;

            if (ban.getIssuer() == Punishment.CONSOLE_UUID) {
                issuerName = Language.Other.CONSOLE_NAME;
            } else {
                issuerName = getPlayerName(ban.getIssuer());
            }

            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, colorize(ban.isPermanent() ? Language.Ban.PUNISHMENT_MESSAGE : Language.Tempban.PUNISHMENT_MESSAGE
                .replace("{issuer}", issuerName)
                .replace("{target}", e.getName())
                .replace("{reason}", ban.getIssueReason())
                .replace("{server}", ban.getServer())
                .replace("{remaining}", TimeFormatter.formatTime(ban.getRemaining()))
                .replace("{until}", Variables.Date.DATE_FORMAT.format(new Date(ban.getUntil())))
            ));
            Achilles.getProfileHandler().getProfiles().remove(uuid);
            return;
        }
    }
}
