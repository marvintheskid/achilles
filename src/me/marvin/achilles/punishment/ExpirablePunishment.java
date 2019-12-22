package me.marvin.achilles.punishment;

import lombok.Getter;

@Getter
public abstract class ExpirablePunishment extends LiftablePunishment {
    protected long until;

    public boolean isPermanent() {
        return until == -1 && active;
    }
}
