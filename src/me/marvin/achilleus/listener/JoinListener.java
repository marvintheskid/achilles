package me.marvin.achilleus.listener;

import me.marvin.achilleus.Achilles;
import me.marvin.achilleus.profile.Profile;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {
    @EventHandler
    void onLogin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        Profile prof = Achilles.getProfileHandler().getProfile(p.getUniqueId());
        if (prof != null) {
            prof.updateIp();
        }
    }
}
