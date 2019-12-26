package me.marvin.achilles.punishment.impl;

import me.marvin.achilles.Achilles;
import me.marvin.achilles.Variables;
import me.marvin.achilles.punishment.LiftablePunishment;
import me.marvin.achilles.punishment.Punishment;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class Kick extends Punishment {
    public Kick() {
        this.server = Variables.Database.SERVER_NAME;
        this.issuedOn = System.currentTimeMillis();
    }

    public Kick(UUID issuer, UUID target, String reason) {
        this.issuer = issuer;
        this.target = target;
        this.issueReason = reason;
        this.server = Variables.Database.SERVER_NAME;
        this.issuedOn = System.currentTimeMillis();
    }

    @Override
    protected void issuePunishment() {
        Achilles.getConnection().update("INSERT INTO `" + Variables.Database.KICK_TABLE_NAME + "` (" +
            "`server`, " +
            "`issuer`, " +
            "`target`, " +
            "`issueReason`, " +
            "`issuedOn`) VALUES (?, ?, ?, ?, ?);",
            (result) -> {}, server, issuer.toString(), target.toString(), issueReason, issuedOn);
    }

    @Override
    public void fromResultSet(ResultSet rs) throws SQLException {
        this.server = rs.getString("server");
        this.issuer = UUID.fromString(rs.getString("issuer"));
        this.target = UUID.fromString(rs.getString("target"));
        this.issueReason = rs.getString("issueReason");
        this.issuedOn = rs.getLong("issuedOn");
    }
}
