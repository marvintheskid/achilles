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

/*
 * Copyright (c) 2019 marvintheskid (Kovács Márton)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 * OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

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
