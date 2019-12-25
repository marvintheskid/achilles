package me.marvin.achilles.listener;

import me.marvin.achilles.Achilles;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitListener implements Listener {
    @EventHandler
    void onQuit(PlayerQuitEvent e) {
        Achilles.getProfileHandler().getProfiles().remove(e.getPlayer().getUniqueId());
    }
}
