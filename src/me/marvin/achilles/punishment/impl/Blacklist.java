package me.marvin.achilles.punishment.impl;

import me.marvin.achilles.Achilles;
import me.marvin.achilles.Variables;
import me.marvin.achilles.punishment.LiftablePunishment;
import me.marvin.achilles.punishment.Punishment;
import me.marvin.achilles.punishment.PunishmentHandler;
import me.marvin.achilles.utils.UUIDConverter;
import me.marvin.achilles.utils.sql.BatchContainer;
import net.minecraft.server.v1_8_R3.ItemPotion;
import org.bukkit.craftbukkit.v1_8_R3.potion.CraftPotionEffectType;
import org.bukkit.potion.PotionEffect;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.UUID;
import java.util.function.Supplier;

import static me.marvin.achilles.Variables.Database.BLACKLIST_TABLE_NAME;

/*
 * Copyright (c) 2019-Present marvintheskid (Kovács Márton)
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

public class Blacklist extends LiftablePunishment {
    public Blacklist() {
        this.active = true;
        this.server = Variables.Database.SERVER_NAME;
        this.issuedOn = System.currentTimeMillis();
    }

    public Blacklist(UUID issuer, UUID target, String reason) {
        this.issuer = issuer;
        this.target = target;
        this.issueReason = reason;
        this.active = true;
        this.server = Variables.Database.SERVER_NAME;
        this.issuedOn = System.currentTimeMillis();
    }

    @Override
    protected void issuePunishment() {
        Achilles.getConnection().update(PunishmentHandler.BLACKLIST_HANDLER.getIssueQuery(),
            (result) -> {}, server, UUIDConverter.to(issuer), UUIDConverter.to(target), issueReason, issuedOn, active);
    }

    @Override
    protected void liftPunishment() {
        Achilles.getConnection().update(PunishmentHandler.BLACKLIST_HANDLER.getLiftQuery(),
            (result) -> {}, createLiftBatch());
    }

    @Override
    public BatchContainer createLiftBatch() {
        this.liftedOn = System.currentTimeMillis();
        this.active = false;
        return new BatchContainer(active, liftedOn, liftReason, UUIDConverter.to(liftedBy), UUIDConverter.to(target), id);
    }

    @Override
    public boolean isInstanceOf(Punishment punishment) {
        return punishment instanceof Blacklist;
    }

    public static class Handler implements PunishmentHandler<Blacklist> {
        @Override
        public String getTable() {
            return BLACKLIST_TABLE_NAME;
        }

        @Override
        public void createTable() {
            Achilles.getConnection().update("CREATE TABLE IF NOT EXISTS `" + BLACKLIST_TABLE_NAME + "` ("
                    + "`id` int PRIMARY KEY NOT NULL AUTO_INCREMENT,"
                    + "`server` varchar(200) NOT NULL,"
                    + "`issuer` binary(16) NOT NULL,"
                    + "`target` binary(16) NOT NULL,"
                    + "`issueReason` varchar(200) NOT NULL,"
                    + "`issuedOn` bigint NOT NULL,"
                    + "`liftedBy` binary(16),"
                    + "`liftedOn` bigint,"
                    + "`liftReason` varchar(200),"
                    + "`active` tinyint(1) NOT NULL) DEFAULT CHARSET=utf8;",
                (result) -> {}
            );
        }

        @Override
        public Supplier<Blacklist> getSupplier() {
            return Blacklist::new;
        }

        @Override
        public String getIssueQuery() {
            return "INSERT INTO `" + BLACKLIST_TABLE_NAME + "` (" +
                "`server`, " +
                "`issuer`, " +
                "`target`, " +
                "`issueReason`, " +
                "`issuedOn`, " +
                "`active`) VALUES (?, ?, ?, ?, ?, ?, ?);";
        }

        @Override
        public String getLiftQuery() {
            return "UPDATE `" + BLACKLIST_TABLE_NAME + "` SET " +
                "active = ?, " +
                "liftedOn = ?, " +
                "liftReason = ?, " +
                "liftedBy = ? " +
                "WHERE target = ? " +
                "AND id = ?;";
        }
    }
}
