package me.marvin.achilles.listener;

import me.marvin.achilles.Achilles;
import me.marvin.achilles.profile.Profile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {
    @EventHandler
    void onLogin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        Profile prof = Achilles.getProfileHandler().getProfile(p.getUniqueId());
        if (prof != null) {
            prof.updateIp();
            prof.scanAlts();
        }
        Bukkit.getOnlinePlayers().stream()
            .filter(other -> other.hasPermission("achilles.*") || other.hasPermission("achilles.checkAlts"))
            .forEach(other -> {
                //TODO /alts parancs lefuttatása
            }
        );
    }
}
