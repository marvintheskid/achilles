package me.marvin.achilles.punishment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class ExpirablePunishment extends LiftablePunishment {
    public static int PERMANENT_PUNISHMENT = -1;

    protected long until;

    protected abstract void expirePunishment();

    public final void expire() {
        active = false;
        expirePunishment();
    }

    public final boolean isPermanent() {
        return until == PERMANENT_PUNISHMENT;
    }

    public final boolean isExpired() {
        return !isActive() && !isPermanent() && getRemaining() == 0;
    }

    public final long getRemaining() {
        if (isPermanent()) {
            return PERMANENT_PUNISHMENT;
        } else {
            if (until <= System.currentTimeMillis()) {
                return 0;
            } else {
                return until - System.currentTimeMillis();
            }
        }
    }
}
