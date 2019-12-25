package me.marvin.achilles.punishment;

import lombok.Getter;

@Getter
public abstract class ExpirablePunishment extends LiftablePunishment {
    public static int PERMANENT_PUNISHMENT = -1;

    protected long until;

    public final boolean isPermanent() {
        return until == PERMANENT_PUNISHMENT && active;
    }

    public final long getRemaining() {
        if (until == -1) {
            return until;
        } else {
            if (until <= System.currentTimeMillis()) {
                return 0;
            } else {
                return until - System.currentTimeMillis();
            }
        }
    }
}
