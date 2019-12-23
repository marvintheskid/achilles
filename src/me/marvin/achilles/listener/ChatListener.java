package me.marvin.achilles.listener;

import me.marvin.achilles.Achilles;
import me.marvin.achilles.profile.Profile;
import me.marvin.achilles.punishment.impl.Mute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;


//TODO: Listener
public class ChatListener implements Listener {
    @EventHandler
    void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        Profile prof = Achilles.getProfileHandler().getProfile(p.getUniqueId());
        if (prof == null) {
            return;
        }
        prof.getActive(Mute.class).ifPresent((mute) -> {
            e.setCancelled(true);

        });
    }
}
