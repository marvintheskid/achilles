package me.marvin.achilles.punishment;

import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.Setter;
import me.marvin.achilles.utils.UUIDConverter;
import me.marvin.achilles.utils.sql.BatchContainer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@Getter
@Setter
public abstract class LiftablePunishment extends Punishment {
    protected UUID liftedBy;
    protected String liftReason;
    protected long liftedOn;
    protected boolean active;

    protected abstract void liftPunishment();
    public abstract BatchContainer createLiftBatch();

    @Override
    public void fromResultSet(ResultSet rs) throws SQLException {
        this.server = rs.getString("server");
        this.issuer = UUIDConverter.from(rs.getBytes("issuer"));
        this.target = UUIDConverter.from(rs.getBytes("target"));
        this.issueReason = rs.getString("issueReason");
        this.issuedOn = rs.getLong("issuedOn");
        this.active = rs.getBoolean("active");
        this.id = rs.getInt("id");
        if (!this.active) {
            this.liftedBy = UUIDConverter.from(rs.getBytes("liftedBy"));
            this.liftReason = rs.getString("liftReason");
            this.liftedOn = rs.getLong("liftedOn");
        }
    }

    public final void lift() {
        Preconditions.checkNotNull(liftedBy, "liftedby was null");
        Preconditions.checkNotNull(liftReason, "liftreason was null");
        this.liftedOn = System.currentTimeMillis();
        this.active = false;
        this.liftPunishment();
    }
}
