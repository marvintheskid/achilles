package me.marvin.achilles.profile;

import lombok.Data;
import me.marvin.achilles.Achilles;
import me.marvin.achilles.punishment.LiftablePunishment;
import me.marvin.achilles.punishment.Punishment;
import me.marvin.achilles.punishment.PunishmentHandler;
import me.marvin.achilles.utils.UUIDConverter;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Data
public abstract class Profile {
    protected UUID uuid;

    public Profile(UUID uuid) {
        this.uuid = uuid;
    }

    public abstract <T extends LiftablePunishment> Optional<T> getActive(Class<T> type);
    public abstract <T extends Punishment> List<T> getPunishments(Class<T> type);

    @SuppressWarnings("unchecked")
    protected final <T extends Punishment> List<T> getPunishmentsFromTable(Class<T> type, boolean async) {
        PunishmentHandler<T> data = (PunishmentHandler<T>) Achilles.getHandlers().get(type);
        if (data == null) {
            throw new RuntimeException("found no handlers for punishment " + type.getSimpleName());
        }
        List<T> list = new ArrayList<>();
        Achilles.getConnection().query(async, "SELECT * FROM `" + data.getTable() + "` WHERE `target` = ?", (result) -> {
            try {
                while (result.next()) {
                    T punishment = data.getSupplier().get();
                    punishment.fromResultSet(result);
                    list.add(punishment);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }, UUIDConverter.to(uuid));
        return list;
    }
}