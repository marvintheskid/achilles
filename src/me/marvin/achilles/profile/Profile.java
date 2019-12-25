package me.marvin.achilles.profile;

import lombok.Data;
import me.marvin.achilles.Achilles;
import me.marvin.achilles.Variables;
import me.marvin.achilles.profile.alts.AltAccount;
import me.marvin.achilles.punishment.LiftablePunishment;
import me.marvin.achilles.punishment.Punishment;
import me.marvin.achilles.punishment.impl.Ban;
import me.marvin.achilles.punishment.impl.Blacklist;
import me.marvin.achilles.punishment.impl.Kick;
import me.marvin.achilles.punishment.impl.Mute;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Data
public class Profile {
    private UUID uuid;
    private String username;
    private List<Punishment> punishments;
    private List<AltAccount> alts;

    public Profile(UUID uuid) {
        this.uuid = uuid;
        this.username = Bukkit.getOfflinePlayer(uuid).getName();
        this.punishments = new ArrayList<>();
        this.alts = new ArrayList<>();
    }

    @SuppressWarnings("unchecked")
    public <T extends Punishment> List<T> getPunishments(Class<? extends T> type) {
        return (List<T>) Collections.unmodifiableList(punishments.stream().filter(punishment -> punishment.getClass() == type).collect(Collectors.toList()));
    }

    @SuppressWarnings("unchecked")
    public <T extends LiftablePunishment> Optional<T> getActive(Class<? extends T> type) {
        return (Optional<T>) ((List<? extends LiftablePunishment>) getPunishments(type)).stream().filter(LiftablePunishment::isActive).min(Comparator.comparingLong(Punishment::getId));
    }

    public void updateIp() {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            Achilles.getInstance().getLogger().warning("Tried to call Profile#updateIp on profile " + uuid.toString() + " while the associated player is null.");
            return;
        }

        Achilles.getConnection().update(true, "INSERT INTO `" + Variables.Database.ALTS_TABLE_NAME + "` " +
            "(`uuid`, `username`, `ip`, `lastlogin`)" +
            " VALUES (?, ?, ?, ?)" +
            " ON DUPLICATE KEY UPDATE " +
            "`username` = ?, " +
            "`ip` = ?, " +
            "`lastlogin` = ?;",
            (result) -> {}, uuid.toString(), player.getName(), player.getAddress().getHostName(), System.currentTimeMillis(), player.getName(), player.getAddress().getHostName(), System.currentTimeMillis());
    }

    public void scanAlts() {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            Achilles.getInstance().getLogger().warning("Tried to call Profile#scanAlts on profile " + uuid.toString() + " while the associated player is null.");
            return;
        }

        Achilles.getConnection().query(true, "SELECT * FROM `" + Variables.Database.ALTS_TABLE_NAME + "` WHERE `ip` = ? LIMIT 1;",
            (result) -> {
                try {
                    while (result.next()) {
                        AltAccount account = new AltAccount(UUID.fromString(result.getString("uuid")), result.getString("username"));
                        checkPunishmentsFor(account, Variables.Database.BAN_TABLE_NAME);
                        checkPunishmentsFor(account, Variables.Database.BLACKLIST_TABLE_NAME);
                        alts.add(account);
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }, player.getAddress().getHostName()
        );
    }

    public Profile load() {
        return this.load(true);
    }

    public Profile load(boolean async) {
        getPunishmentsFromTable(Variables.Database.BAN_TABLE_NAME, Ban::new, async);
        getPunishmentsFromTable(Variables.Database.KICK_TABLE_NAME, Kick::new, async);
        getPunishmentsFromTable(Variables.Database.BLACKLIST_TABLE_NAME, Blacklist::new, async);
        getPunishmentsFromTable(Variables.Database.MUTE_TABLE_NAME, Mute::new, async);

        Achilles.getConnection().query(async, "SELECT `username` FROM `" + Variables.Database.ALTS_TABLE_NAME + "` WHERE `uuid` = ? LIMIT 1;",
            (result) -> {
                try {
                    if (result.next()) username = result.getString("username");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }, uuid.toString()
        );

        return this;
    }

    private void getPunishmentsFromTable(String table, Supplier<? extends Punishment> supplier, boolean async) {
        Achilles.getConnection().query(async, "SELECT * FROM `" + table + "` WHERE `target` = ?", (result) -> {
            try {
                while (result.next()) {
                    Punishment punishment = supplier.get();
                    punishment.fromResultSet(result);
                    getPunishments().add(punishment);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }, uuid.toString());
    }

    private void checkPunishmentsFor(AltAccount account, String table) {
        Achilles.getConnection().query(true, "SELECT * FROM `" + table + "` WHERE `target` = ? AND `active` = ?", (result) -> {
            try {
                if (result.next()) {
                    account.setPunished(true);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }, account.getUuid().toString(), true);
    }
}