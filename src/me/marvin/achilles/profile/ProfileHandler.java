package me.marvin.achilles.profile;

import lombok.Getter;
import me.marvin.achilles.Achilles;
import me.marvin.achilles.profile.impl.FullProfile;
import org.bukkit.Bukkit;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/*
 * Copyright (c) 2019-Present marvintheskid (Kovács Márton)
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

@Getter
public class ProfileHandler {
    private Map<UUID, FullProfile> profiles;

    public ProfileHandler() {
        this.profiles = new ConcurrentHashMap<>();
        Bukkit.getScheduler().runTaskTimerAsynchronously(Achilles.getInstance(), new CleanupTask(), 20L * 60 * 5, 20L * 60 * 5);
    }

    public FullProfile getOrCreateProfile(UUID uuid) {
        return this.getOrCreateProfile(uuid, true);
    }

    public FullProfile getOrCreateProfile(UUID uuid, boolean async) {
        return profiles.computeIfAbsent(uuid, (ignored) -> new FullProfile(uuid).load(async));
    }

    private class CleanupTask implements Runnable {
        @Override
        public void run() {
           profiles.forEach(((uuid, profile) -> {
               if (Bukkit.getPlayer(uuid) == null) {
                   profiles.remove(uuid, profile);
               }
           }));
        }
    }
}
