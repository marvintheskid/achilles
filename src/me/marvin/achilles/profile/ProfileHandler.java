package me.marvin.achilles.profile;

import lombok.Getter;
import me.marvin.achilles.Achilles;
import me.marvin.achilles.profile.impl.FullProfile;
import org.bukkit.Bukkit;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

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
