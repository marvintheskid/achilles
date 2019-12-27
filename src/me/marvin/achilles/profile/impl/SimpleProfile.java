package me.marvin.achilles.profile.impl;

import me.marvin.achilles.Achilles;
import me.marvin.achilles.profile.Profile;
import me.marvin.achilles.punishment.LiftablePunishment;
import me.marvin.achilles.punishment.Punishment;
import me.marvin.achilles.punishment.PunishmentHandlerData;

import java.sql.SQLException;
import java.util.*;
import java.util.function.Supplier;

public class SimpleProfile extends Profile {
    private Map<Class<? extends Punishment>, List<Punishment>> cache;

    public SimpleProfile(UUID uuid) {
        super(uuid);
        this.cache = new HashMap<>();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends LiftablePunishment> Optional<T> getActive(Class<? extends T> type) {
        return (Optional<T>) ((List<? extends LiftablePunishment>) cache.computeIfAbsent(type, (ignored) -> {
            PunishmentHandlerData data = Achilles.getHandlers().get(type);
            if (data == null) {
                throw new RuntimeException("found no handlers for punishment " + type.getSimpleName());
            }
            return getPunishmentsFromTable(data.getTable(), data.getSupplier(), true);
        })).stream().filter(LiftablePunishment::isActive).min(Comparator.comparingLong(Punishment::getId));
    }

    private List<Punishment> getPunishmentsFromTable(String table, Supplier<? extends Punishment> supplier, boolean async) {
        List<Punishment> list = new ArrayList<>();
        Achilles.getConnection().query(async, "SELECT * FROM `" + table + "` WHERE `target` = ?", (result) -> {
            try {
                while (result.next()) {
                    Punishment punishment = supplier.get();
                    punishment.fromResultSet(result);
                    list.add(punishment);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }, uuid.toString());
        return list;
    }
}
