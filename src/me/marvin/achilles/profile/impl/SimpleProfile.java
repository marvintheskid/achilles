package me.marvin.achilles.profile.impl;

import lombok.Getter;
import me.marvin.achilles.Achilles;
import me.marvin.achilles.Variables;
import me.marvin.achilles.profile.Profile;
import me.marvin.achilles.punishment.LiftablePunishment;
import me.marvin.achilles.punishment.Punishment;
import me.marvin.achilles.utils.UUIDConverter;

import java.sql.SQLException;
import java.util.*;

@Getter
public class SimpleProfile extends Profile {
    private Map<Class<? extends Punishment>, List<Punishment>> cache;
    private String name;

    public SimpleProfile(UUID uuid) {
        super(uuid);
        this.cache = new HashMap<>();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends LiftablePunishment> Optional<T> getActive(Class<T> type) {
        return (Optional<T>) getPunishments(type)
            .stream().map(punishment -> (LiftablePunishment) punishment)
            .filter(LiftablePunishment::isActive)
            .min(Comparator.comparingLong(Punishment::getId));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Punishment> List<T> getPunishments(Class<T> type) {
        return (List<T>) cache.computeIfAbsent(type, (ignored) -> new ArrayList<>(getPunishmentsFromTable(type, false)));
    }

    public String getName() {
        if (name != null) return name;
        Achilles.getConnection().query(false, "SELECT `username` FROM `" + Variables.Database.ALTS_TABLE_NAME + "` WHERE `uuid` = ? LIMIT 1;",
            (result) -> {
                try {
                    if (result.next() && name == null) name = result.getString("username");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }, UUIDConverter.to(uuid)
        );
        return name;
    }
}
