package me.marvin.achilles.profile;

import lombok.Getter;
import me.marvin.achilles.Achilles;
import org.bukkit.Bukkit;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class ProfileHandler {
    private Map<UUID, Profile> profiles;

    public ProfileHandler() {
        this.profiles = new ConcurrentHashMap<>();
        Bukkit.getScheduler().runTaskTimerAsynchronously(Achilles.getInstance(), new CleanupTask(), 20L * 60 * 5, 20L * 60 * 5);
    }

    public Profile getProfile(UUID uuid) {
        return this.getProfile(uuid, true);
    }

    public Profile getProfile(UUID uuid, boolean async) {
        return profiles.computeIfAbsent(uuid, (ignored) -> new Profile().load(async));
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
