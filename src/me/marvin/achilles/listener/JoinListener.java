package me.marvin.achilles.listener;

import me.marvin.achilles.Achilles;
import me.marvin.achilles.profile.impl.FullProfile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

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

public class JoinListener implements Listener {
    @EventHandler
    void onLogin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        FullProfile prof = Achilles.getProfileHandler().getOrCreateProfile(p.getUniqueId());
        if (prof != null) {
            prof.updateIp();
            prof.scanAlts();
            System.out.println(prof.getPunishments().size());
        }
        Bukkit.getOnlinePlayers().stream()
            .filter(other -> other.hasPermission("achilles.*") || other.hasPermission("achilles.checkAlts"))
            .forEach(other -> {
                //TODO /alts parancs lefuttatása
            }
        );
    }
}
