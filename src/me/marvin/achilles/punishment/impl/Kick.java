package me.marvin.achilles.punishment.impl;

import me.marvin.achilles.Achilles;
import me.marvin.achilles.Variables;
import me.marvin.achilles.punishment.LiftablePunishment;
import me.marvin.achilles.punishment.Punishment;
import me.marvin.achilles.punishment.PunishmentHandler;
import me.marvin.achilles.utils.UUIDConverter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.function.Supplier;

import static me.marvin.achilles.Variables.Database.KICK_TABLE_NAME;

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
            (result) -> {}, server, UUIDConverter.to(issuer), UUIDConverter.to(target), issueReason, issuedOn);
    }

    @Override
    public void fromResultSet(ResultSet rs) throws SQLException {
        this.server = rs.getString("server");
        this.issuer = UUIDConverter.from(rs.getBytes("issuer"));
        this.target = UUIDConverter.from(rs.getBytes("target"));
        this.issueReason = rs.getString("issueReason");
        this.issuedOn = rs.getLong("issuedOn");
    }

    @Override
    public boolean isInstanceOf(Punishment punishment) {
        return punishment instanceof Kick;
    }

    public static class Handler implements PunishmentHandler<Kick> {
        @Override
        public String getTable() {
            return KICK_TABLE_NAME;
        }

        @Override
        public void createTable() {
            Achilles.getConnection().update("CREATE TABLE IF NOT EXISTS `" + KICK_TABLE_NAME + "` ("
                    + "`id` int PRIMARY KEY NOT NULL AUTO_INCREMENT,"
                    + "`server` varchar(200) NOT NULL,"
                    + "`issuer` binary(16) NOT NULL,"
                    + "`target` binary(16) NOT NULL,"
                    + "`issueReason` varchar(200) NOT NULL,"
                    + "`issuedOn` bigint NOT NULL) DEFAULT CHARSET=utf8;",
                (result) -> {}
            );
        }

        @Override
        public Supplier<Kick> getSupplier() {
            return Kick::new;
        }

        @Override
        public String getIssueQuery() {
            return "INSERT INTO `" + KICK_TABLE_NAME + "` (" +
                "`server`, " +
                "`issuer`, " +
                "`target`, " +
                "`issueReason`, " +
                "`issuedOn`) VALUES (?, ?, ?, ?, ?);";
        }

        @Override
        public String getLiftQuery() {
            return "";
        }
    }
}
