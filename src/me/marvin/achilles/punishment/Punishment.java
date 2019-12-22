package me.marvin.achilles.punishment;

import com.google.common.base.Preconditions;
import lombok.Data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@Data
public abstract class Punishment {
    protected UUID issuer, target;
    protected String server, issueReason;
    protected long issuedOn, id;

    public abstract void fromResultSet(ResultSet rs) throws SQLException;
    protected abstract void issuePunishment();

    public void issue() {
        Preconditions.checkNotNull(issuer, "issuer was null");
        Preconditions.checkNotNull(target, "target was null");
        Preconditions.checkNotNull(issueReason, "issuereason was null");
        issuedOn = System.currentTimeMillis();
        issuePunishment();
    }
}
