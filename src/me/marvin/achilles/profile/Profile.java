package me.marvin.achilles.profile;

import lombok.Data;
import me.marvin.achilles.Achilles;
import me.marvin.achilles.Variables;
import me.marvin.achilles.profile.alts.AltAccount;
import me.marvin.achilles.punishment.LiftablePunishment;
import me.marvin.achilles.punishment.Punishment;
import me.marvin.achilles.punishment.impl.Ban;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Data
public class Profile {
    private UUID uuid;
    private List<Punishment> punishments;
    private List<AltAccount> alts;

    public Profile(UUID uuid) {
        this.uuid = uuid;
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

    public void updateIp() {
        Achilles.getConnection().update(true, "INSERT INTO `" + Variables.Database.ALTS_TABLE_NAME + "` " +
            "(`uuid`, `username`, `ip`, `lastlogin`)" +
            " VALUES (?, ?, ?, ?)" +
            " ON DUPLICATE KEY UPDATE " +
            "`username` = ?, " +
            "`ip` = ?, " +
            "`lastlogin` = ?;",
            (result) -> {}, uuid.toString());
    }

    public Profile load() {
        return this.load(true);
    }

    public Profile load(boolean async) {
        getPunishmentsFromTable(Variables.Database.BAN_TABLE_NAME, Ban::new, async);
        getPunishmentsFromTable(Variables.Database.KICK_TABLE_NAME, Ban::new, async);
        getPunishmentsFromTable(Variables.Database.BLACKLIST_TABLE_NAME, Ban::new, async);
        getPunishmentsFromTable(Variables.Database.MUTE_TABLE_NAME, Ban::new, async);

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
