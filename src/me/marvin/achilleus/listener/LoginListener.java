package me.marvin.achilleus.listener;

import me.marvin.achilleus.Achilles;
import me.marvin.achilleus.Language;
import me.marvin.achilleus.profile.Profile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.UUID;

import static me.marvin.achilleus.utils.etc.StringUtils.colorize;

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
