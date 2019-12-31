package me.marvin.achilles.profile.impl;

import lombok.Getter;
import lombok.Setter;
import me.marvin.achilles.Achilles;
import me.marvin.achilles.Variables;
import me.marvin.achilles.profile.Profile;
import me.marvin.achilles.profile.alts.AltAccount;
import me.marvin.achilles.punishment.LiftablePunishment;
import me.marvin.achilles.punishment.Punishment;
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

    @SuppressWarnings("unchecked")
    public <T extends Punishment> List<T> getPunishments(Class<? extends T> type) {
        return (List<T>) Collections.unmodifiableList(punishments.stream().filter(punishment -> type.isAssignableFrom(punishment.getClass())).collect(Collectors.toList()));
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
            (result) -> {}, UUIDConverter.to(uuid), player.getName(), player.getAddress().getHostName(), System.currentTimeMillis(), player.getName(), player.getAddress().getHostName(), System.currentTimeMillis());
    }

    public void scanAlts() {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            Achilles.getInstance().getLogger().warning("Tried to call Profile#scanAlts on profile " + uuid.toString() + " while the associated player was null.");
            return;
        }

        Achilles.getConnection().query(true, "SELECT * FROM `" + Variables.Database.ALTS_TABLE_NAME + "` WHERE `ip` = ?;",
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
        getPunishmentsFromTable(Ban.class, async);
        getPunishmentsFromTable(Blacklist.class, async);
        getPunishmentsFromTable(Kick.class, async);
        getPunishmentsFromTable(Mute.class, async);

        Achilles.getConnection().query(async, "SELECT `username` FROM `" + Variables.Database.ALTS_TABLE_NAME + "` WHERE `uuid` = ? LIMIT 1;",
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

    private void getPunishmentsFromTable(Class<? extends Punishment> type, boolean async) {
        PunishmentHandlerData data = Achilles.getHandlers().get(type);
        if (data == null) {
            throw new RuntimeException("found no handlers for punishment " + type.getSimpleName());
        }
        Achilles.getConnection().query(async, "SELECT * FROM `" + data.getTable() + "` WHERE `target` = ?", (result) -> {
            try {
                while (result.next()) {
                    Punishment punishment = data.getSupplier().get();
                    punishment.fromResultSet(result);
                    punishments.add(punishment);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }, UUIDConverter.to(uuid));
    }

    private void checkPunishmentsFor(AltAccount account, Class<? extends Punishment> type) {
        PunishmentHandlerData data = Achilles.getHandlers().get(type);
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
