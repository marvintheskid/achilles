package me.marvin.achilles.listener;

import me.marvin.achilles.Achilles;
import me.marvin.achilles.Language;
import me.marvin.achilles.profile.Profile;
import me.marvin.achilles.profile.impl.FullProfile;
import me.marvin.achilles.punishment.ExpirablePunishment;
import me.marvin.achilles.punishment.impl.Ban;
import me.marvin.achilles.punishment.impl.Blacklist;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static me.marvin.achilles.utils.etc.StringUtils.colorize;

//TODO Punishmentek checkelése
public class LoginListener implements Listener {
    @EventHandler
    void onLogin(AsyncPlayerPreLoginEvent e) {
        UUID uuid = e.getUniqueId();
        FullProfile profile = Achilles.getProfileHandler().getProfile(uuid, false);

        if (profile == null) {
            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, colorize(Language.Other.FAILED_TO_LOAD));
            return;
        }

        profile.setUsername(e.getName());
        profile.getPunishments(ExpirablePunishment.class).forEach(punishment -> {
            if (punishment.isExpired()) {
                punishment.expire();
            }
        });

        Optional<Blacklist> optionalBlacklist = profile.getActive(Blacklist.class);
        if (optionalBlacklist.isPresent()) {
            Blacklist blacklist = optionalBlacklist.get();
            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, colorize(Language.Blacklist.PUNISHMENT_MESSAGE
                .replace("", "")
                .replace("", "")
            ));
            return;
        }

        Optional<Ban> optionalBan = profile.getActive(Ban.class);
        if (optionalBan.isPresent()) {
            Ban ban = optionalBan.get();
            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, colorize(Language.Ban.PUNISHMENT_MESSAGE
                .replace("", "")
                .replace("", "")
            ));
            return;
        }
    }
}
