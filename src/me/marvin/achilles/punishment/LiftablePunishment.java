package me.marvin.achilles.punishment;

import com.google.common.base.Preconditions;
import lombok.Getter;

import java.util.UUID;

@Getter
public abstract class LiftablePunishment extends Punishment {
    protected UUID liftedBy;
    protected String liftReason;
    protected long liftedOn;
    protected boolean active;

    protected abstract void liftPunishment();

    public final void lift() {
        Preconditions.checkNotNull(liftedBy, "liftedby was null");
        Preconditions.checkNotNull(liftReason, "liftreason was null");
        liftedOn = System.currentTimeMillis();
        active = false;
        this.liftPunishment();
    }
}
