package me.marvin.achilles.profile.impl;

import lombok.Getter;
import lombok.Setter;
import me.marvin.achilles.Achilles;
import me.marvin.achilles.Variables;
import me.marvin.achilles.profile.Profile;
import me.marvin.achilles.profile.alts.AltAccount;
import me.marvin.achilles.punishment.LiftablePunishment;
import me.marvin.achilles.punishment.Punishment;
import me.marvin.achilles.punishment.PunishmentHandler;
import me.marvin.achilles.punishment.PunishmentHandlerData;
import me.marvin.achilles.punishment.impl.Ban;
import me.marvin.achilles.punishment.impl.Blacklist;
import me.marvin.achilles.punishment.impl.Kick;
import me.marvin.achilles.punishment.impl.Mute;
import me.marvin.achilles.utils.UUIDConverter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static me.marvin.achilles.Variables.Database.ALTS_TABLE_NAME;

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

@Getter
public class FullProfile extends Profile {
    @Setter private String username;
    private List<Punishment> punishments;
    private List<AltAccount> alts;

    public FullProfile(UUID uuid) {
        super(uuid);
        this.username = Bukkit.getOfflinePlayer(uuid).getName();
        this.punishments = new ArrayList<>();
        this.alts = new ArrayList<>();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Punishment> List<T> getPunishments(Class<T> type) {
        return (List<T>) Collections.unmodifiableList(punishments.stream().filter(punishment -> type.isAssignableFrom(punishment.getClass())).collect(Collectors.toList()));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends LiftablePunishment> Optional<T> getActive(Class<T> type) {
        return (Optional<T>) ((List<? extends LiftablePunishment>) getPunishments(type)).stream().filter(LiftablePunishment::isActive).min(Comparator.comparingLong(Punishment::getId));
    }

    public void updateIp() {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            Achilles.getInstance().getLogger().warning("Tried to call Profile#updateIp on profile " + uuid.toString() + " while the associated player is null.");
            return;
        }

        Achilles.getConnection().update(true, "INSERT INTO `" + ALTS_TABLE_NAME + "` " +
            "(`uuid`, `username`, `ip`, `lastlogin`)" +
            " VALUES (?, ?, ?, ?)" +
            " ON DUPLICATE KEY UPDATE " +
            "`username` = ?, " +
            "`ip` = ?, " +
            "`lastlogin` = ?;",
            (result) -> {}, UUIDConverter.to(uuid), player.getName(), player.getAddress().getAddress().getHostAddress(), System.currentTimeMillis(), player.getName(), player.getAddress().getAddress().getHostAddress(), System.currentTimeMillis());
    }

    public void scanAlts() {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            Achilles.getInstance().getLogger().warning("Tried to call Profile#scanAlts on profile " + uuid.toString() + " while the associated player was null.");
            return;
        }

        Achilles.getConnection().query(true, "SELECT * FROM `" + ALTS_TABLE_NAME + "` WHERE `ip` = ?;",
            (result) -> {
                try {
                    while (result.next()) {
                        AltAccount account = new AltAccount(UUIDConverter.from(result.getBytes("uuid")), result.getString("username"));
                        checkPunishmentsFor(account, Blacklist.class);
                        checkPunishmentsFor(account, Ban.class);
                        alts.add(account);
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }, player.getAddress().getHostName()
        );
    }

    public FullProfile load() {
        return this.load(true);
    }

    public FullProfile load(boolean async) {
        punishments.addAll(getPunishmentsFromTable(Blacklist.class, async));
        punishments.addAll(getPunishmentsFromTable(Ban.class, async));
        punishments.addAll(getPunishmentsFromTable(Kick.class, async));
        punishments.addAll(getPunishmentsFromTable(Mute.class, async));

        Achilles.getConnection().query(async, "SELECT `username` FROM `" + ALTS_TABLE_NAME + "` WHERE `uuid` = ? LIMIT 1;",
            (result) -> {
                try {
                    if (result.next() && username == null) username = result.getString("username");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }, UUIDConverter.to(uuid)
        );

        return this;
    }

    @SuppressWarnings("unchecked")
    private <T extends Punishment> void checkPunishmentsFor(AltAccount account, Class<T> type) {
        PunishmentHandler<T> data = (PunishmentHandler<T>) Achilles.getHandlers().get(type);
        if (data == null) {
            throw new RuntimeException("found no handlers for punishment " + type.getSimpleName());
        }
        Achilles.getConnection().query(true, "SELECT * FROM `" + data.getTable() + "` WHERE `target` = ? AND `active` = ?", (result) -> {
            try {
                if (result.next()) {
                    account.setPunished(true);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }, UUIDConverter.to(account.getUuid()), true);
    }
}
