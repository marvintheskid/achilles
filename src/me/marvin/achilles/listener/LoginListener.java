package me.marvin.achilles.listener;

import me.marvin.achilles.Achilles;
import me.marvin.achilles.Language;
import me.marvin.achilles.profile.impl.FullProfile;
import me.marvin.achilles.punishment.ExpirablePunishment;
import me.marvin.achilles.punishment.Punishment;
import me.marvin.achilles.punishment.impl.Ban;
import me.marvin.achilles.punishment.impl.Blacklist;
import me.marvin.achilles.utils.TimeFormatter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.Optional;
import java.util.UUID;

import static me.marvin.achilles.utils.etc.StringUtils.colorize;

public class LoginListener implements Listener {
    @EventHandler
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
                issuerName = Language.Other.CONSOLE_NAME;
            }

            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, colorize(Language.Blacklist.PUNISHMENT_MESSAGE
                .replace("{issuer}", issuerName)
                .replace("{target}", e.getName())
                .replace("{reason}", blacklist.getIssueReason())
                .replace("{server}", blacklist.getServer())
            ));
            return;
        }

        Optional<Ban> optionalBan = profile.getActive(Ban.class);
        if (optionalBan.isPresent()) {
            Ban ban = optionalBan.get();
            String issuerName;

            if (ban.getIssuer() == Punishment.CONSOLE_UUID) {
                issuerName = Language.Other.CONSOLE_NAME;
            } else {
                issuerName = Language.Other.CONSOLE_NAME;
            }

            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, colorize(ban.isPermanent() ? Language.Ban.PUNISHMENT_MESSAGE : Language.Tempban.PUNISHMENT_MESSAGE
                .replace("{issuer}", issuerName)
                .replace("{target}", e.getName())
                .replace("{reason}", ban.getIssueReason())
                .replace("{server}", ban.getServer())
                .replace("{remaining}", TimeFormatter.formatTime(ban.getRemaining()))
            ));
            return;
        }
    }
}
