package me.marvin.achilles.punishment;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import lombok.Data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@Data
public abstract class Punishment {
    public static final UUID CONSOLE_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    protected UUID issuer, target;
    protected String server, issueReason;
    protected long issuedOn, id;

    public abstract void fromResultSet(ResultSet rs) throws SQLException;
    public abstract boolean isInstanceOf(Punishment punishment);
    protected abstract void issuePunishment();

    public final void issue() {
        Preconditions.checkNotNull(issuer, "issuer was null");
        Preconditions.checkNotNull(target, "target was null");
        Preconditions.checkNotNull(issueReason, "issuereason was null");
        issuedOn = System.currentTimeMillis();
        this.issuePunishment();
    }
}
