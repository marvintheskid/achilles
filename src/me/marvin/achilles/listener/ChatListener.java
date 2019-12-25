package me.marvin.achilles.listener;

import me.marvin.achilles.Achilles;
import me.marvin.achilles.Language;
import me.marvin.achilles.profile.Profile;
import me.marvin.achilles.punishment.Punishment;
import me.marvin.achilles.punishment.impl.Mute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import static me.marvin.achilles.utils.etc.StringUtils.colorize;

public class ChatListener implements Listener {
    @EventHandler
    void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        Profile prof = Achilles.getProfileHandler().getProfile(p.getUniqueId());
        if (prof == null) {
            Achilles.getInstance().getLogger().warning("[Chat] Tried to get profile for player " + p.getUniqueId() + " (" + p.getName() + "), but it failed.");
            return;
        }
        prof.getActive(Mute.class).ifPresent((mute) -> {
            e.setCancelled(true);
            String username = mute.getIssuer() == Punishment.CONSOLE_UUID ? Language.Other.CONSOLE_NAME : Achilles.getProfileHandler().getProfile(mute.getIssuer()).getUsername();
            p.sendMessage(colorize(mute.isPermanent() ? Language.Mute.PUNISHMENT_MESSAGE : Language.Tempmute.PUNISHMENT_MESSAGE
                .replace("{issuer}", "")
                .replace("{target}", "")
                .replace("{remaining}", "")
            ));
        });
    }
}
