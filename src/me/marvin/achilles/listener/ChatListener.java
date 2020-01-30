package me.marvin.achilles.listener;

import me.marvin.achilles.Achilles;
import me.marvin.achilles.Language;
import me.marvin.achilles.Variables;
import me.marvin.achilles.profile.impl.FullProfile;
import me.marvin.achilles.profile.impl.SimpleProfile;
import me.marvin.achilles.punishment.Punishment;
import me.marvin.achilles.punishment.impl.Mute;
import me.marvin.achilles.utils.TimeFormatter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Date;

import static me.marvin.achilles.utils.etc.StringUtils.colorize;

public class ChatListener implements Listener {
    @EventHandler
    void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        FullProfile prof = Achilles.getProfileHandler().getOrCreateProfile(p.getUniqueId());
        if (prof == null) {
            Achilles.getInstance().getLogger().warning("[Chat] Tried to get profile for player " + p.getUniqueId() + " (" + p.getName() + "), but it failed.");
            return;
        }
        prof.getActive(Mute.class).ifPresent((mute) -> {
            e.setCancelled(true);
            String issuerName = mute.getIssuer() == Punishment.CONSOLE_UUID ? Language.Other.CONSOLE_NAME : new SimpleProfile(mute.getIssuer()).getName();
            p.sendMessage(colorize(mute.isPermanent() ? Language.Mute.PUNISHMENT_MESSAGE : Language.Tempmute.PUNISHMENT_MESSAGE
                .replace("{issuer}", issuerName)
                .replace("{target}", p.getName())
                .replace("{reason}", mute.getIssueReason())
                .replace("{server}", Variables.Database.SERVER_NAME)
                .replace("{remaining}", TimeFormatter.formatTime(mute.getRemaining()))
                .replace("{until}", Variables.Date.DATE_FORMAT.format(new Date(mute.getUntil())))
            ));
        });
    }
}
