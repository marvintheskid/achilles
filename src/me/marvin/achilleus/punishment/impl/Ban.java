package me.marvin.achilleus.punishment.impl;

import me.marvin.achilleus.Achilles;
import me.marvin.achilleus.Variables;
import me.marvin.achilleus.punishment.LiftablePunishment;

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
            "AND id = ?", (result) -> {}, active, liftedOn, liftReason, liftedBy.toString(), target.toString(), id);
    }
}
