package me.marvin.achilles.profile;

import lombok.Getter;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class ProfileHandler {
    private Map<UUID, Profile> profiles;

    public ProfileHandler() {
        this.profiles = new ConcurrentHashMap<>();
    }

    public Profile getProfile(UUID uuid) {
        return this.getProfile(uuid, true);
    }

    public Profile getProfile(UUID uuid, boolean async) {
        return profiles.computeIfAbsent(uuid, (ignored) -> new Profile().load(async));
    }
}
