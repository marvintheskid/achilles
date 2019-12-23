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
import org.bukkit.OfflinePlayer;
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
    public <T extends Punishment> List<T> getPunishments(Class<? extends Punishment> type) {
        return (List<T>) Collections.unmodifiableList(punishments.stream().filter(punishment -> punishment.getClass() == type).collect(Collectors.toList()));
    }

    @SuppressWarnings("unchecked")
    public boolean hasActive(Class<? extends LiftablePunishment> type) {
        return ((List<? extends LiftablePunishment>) getPunishments(type)).stream().anyMatch(LiftablePunishment::isActive);
    }

    @SuppressWarnings("unchecked")
    public <T extends LiftablePunishment> Optional<T> getActive(Class<? extends T> type) {
        return (Optional<T>) ((List<? extends LiftablePunishment>) getPunishments(type)).stream().filter(LiftablePunishment::isActive).min(Comparator.comparingLong(Punishment::getId));
    }

    public void updateIp() {
        Player player = Bukkit.getPlayer(uuid);

        Achilles.getConnection().update(true, "INSERT INTO `" + Variables.Database.ALTS_TABLE_NAME + "` " +
            "(`uuid`, `username`, `ip`, `lastlogin`)" +
            " VALUES (?, ?, ?, ?)" +
            " ON DUPLICATE KEY UPDATE " +
            "`username` = ?, " +
            "`ip` = ?, " +
            "`lastlogin` = ?;",
            (result) -> {}, uuid.toString(), player.getName(), player.getAddress().getHostName(), System.currentTimeMillis(), player.getName(), player.getAddress().getHostName(), System.currentTimeMillis());
    }

    public Profile load() {
        return this.load(true);
    }

    public Profile load(boolean async) {
        getPunishmentsFromTable(Variables.Database.BAN_TABLE_NAME, Ban::new, async);
        getPunishmentsFromTable(Variables.Database.KICK_TABLE_NAME, Kick::new, async);
        getPunishmentsFromTable(Variables.Database.BLACKLIST_TABLE_NAME, Blacklist::new, async);
        getPunishmentsFromTable(Variables.Database.MUTE_TABLE_NAME, Mute::new, async);
        Achilles.getConnection().query("SELECT `username` FROM `" + Variables.Database.ALTS_TABLE_NAME + "` WHERE `uuid` = ? LIMIT 1;", (result -> {
            try {
                if (result.next()) username = result.getString("username");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }), uuid.toString());

        return this;
    }

    private void getPunishmentsFromTable(String table, Supplier<? extends Punishment> supplier, boolean async) {
        Achilles.getConnection().query(async, "SELECT * FROM `" + table + "` WHERE `uuid` = ?", (result) -> {
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
}
