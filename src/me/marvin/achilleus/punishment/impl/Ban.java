package me.marvin.achilleus.punishment.impl;

import me.marvin.achilleus.Achilles;
import me.marvin.achilleus.Variables;
import me.marvin.achilleus.punishment.LiftablePunishment;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class Ban extends LiftablePunishment {
    public Ban() {}

    public Ban(UUID issuer, UUID target, long until, String reason) {
        this.issuer = issuer;
        this.target = target;
        this.until = until;
        this.issueReason = reason;
        this.active = true;
        this.server = Variables.Database.SERVER_NAME;
        this.issuedOn = System.currentTimeMillis();
    }

    @Override
    protected void issuePunishment() {
        Achilles.getConnection().update("INSERT INTO `" + Variables.Database.BAN_TABLE_NAME + "` (" +
                "`server`," +
                "`issuer`," +
                "`target`," +
                "`issueReason`," +
                "`until`," +
                "`issuedOn`, " +
                "`active`) VALUES (?, ?, ?, ?, ?, ?, ?)",
                (result) -> {}, server, issuer.toString(), target.toString(), issueReason, until, issuedOn, active);
    }

    @Override
    protected void liftPunishment() {
        Achilles.getConnection().update("UPDATE `minecore_bans` SET" +
            "active = ?," +
            "liftedOn = ?," +
            "liftReason = ?," +
            "liftedBy = ?" +
            "WHERE target = ?" +
            "AND id = ?",
            (result) -> {}, active, liftedOn, liftReason, liftedBy.toString(), target.toString(), id);
    }

    @Override
    public void fromResultSet(ResultSet rs) throws SQLException {
        this.server = rs.getString("server");
        this.issuer = UUID.fromString(rs.getString("issuer"));
        this.target = UUID.fromString(rs.getString("target"));
        this.issueReason = rs.getString("issueReason");
        this.until = rs.getLong("until");
        this.issuedOn = rs.getLong("issuedOn");
        this.active = rs.getBoolean("active");
        if (!this.active) {
            this.liftedBy = UUID.fromString(rs.getString("liftedBy"));
            this.liftReason = rs.getString("liftReason");
            this.liftedOn = rs.getLong("liftedOn");
        }
    }
}
