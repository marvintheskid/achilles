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

/*
 * Copyright (c) 2019 marvintheskid (Kovács Márton)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 * OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

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
