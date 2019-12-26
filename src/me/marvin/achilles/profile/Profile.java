package me.marvin.achilles.profile;

import lombok.Data;
import me.marvin.achilles.punishment.LiftablePunishment;

import java.util.*;

@Data
public abstract class Profile {
    protected UUID uuid;

    public Profile(UUID uuid) {
        this.uuid = uuid;
    }

    public abstract <T extends LiftablePunishment> Optional<T> getActive(Class<? extends T> clazz);
}