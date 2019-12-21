package me.marvin.achilleus.punishment;

import com.google.common.base.Preconditions;

import java.util.UUID;

public abstract class LiftablePunishment extends Punishment {
    protected UUID liftedBy;
    protected String liftReason;
    protected long until, liftedOn;
    protected boolean active;

    public boolean isPermanent() {
        return until == -1 && active;
    }

    protected abstract void liftPunishment();

    public void lift() {
        Preconditions.checkNotNull(liftedBy, "liftedby was null");
        Preconditions.checkNotNull(liftReason, "liftreason was null");
        liftedOn = System.currentTimeMillis();
        active = false;
    }
}
