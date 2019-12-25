package me.marvin.achilles.profile.alts;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class AltAccount {
    private UUID uuid;
    private String name;
    private boolean punished;

    public AltAccount(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }
}
