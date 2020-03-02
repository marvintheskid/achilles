package me.marvin.achilles.punishment;

import lombok.Getter;
import lombok.Setter;
import me.marvin.achilles.utils.UUIDConverter;

import java.sql.ResultSet;
import java.sql.SQLException;

@Getter
@Setter
public abstract class ExpirablePunishment extends LiftablePunishment {
    public static int PERMANENT_PUNISHMENT = -1;

    protected long until;

    protected abstract void expirePunishment();

    @Override
    public void fromResultSet(ResultSet rs) throws SQLException {
        super.fromResultSet(rs);
        this.until = rs.getLong("until");
    }

    public final void expire() {
        this.active = false;
        expirePunishment();
    }

    public final boolean isPermanent() {
        return until == PERMANENT_PUNISHMENT;
    }

    public final boolean isTemporary() {
        return !isPermanent();
    }

    public final boolean isExpired() {
        return !isPermanent() && getRemaining() == 0;
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
