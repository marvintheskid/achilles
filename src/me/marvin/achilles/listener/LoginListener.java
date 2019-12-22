package me.marvin.achilles.listener;

import me.marvin.achilles.Achilles;
import me.marvin.achilles.Language;
import me.marvin.achilles.profile.Profile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.UUID;

import static me.marvin.achilles.utils.etc.StringUtils.colorize;

public class LoginListener implements Listener {
    @EventHandler
    void onLogin(AsyncPlayerPreLoginEvent e) {
        UUID uuid = e.getUniqueId();
        Profile profile = Achilles.getProfileHandler().getProfile(uuid, false);
        if (profile == null) {
            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, colorize(Language.Other.FAILED_TO_LOAD));
        }
    }
}
