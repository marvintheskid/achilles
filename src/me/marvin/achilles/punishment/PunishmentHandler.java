package me.marvin.achilles.punishment;

import me.marvin.achilles.punishment.impl.Ban;
import me.marvin.achilles.punishment.impl.Blacklist;
import me.marvin.achilles.punishment.impl.Kick;
import me.marvin.achilles.punishment.impl.Mute;

import java.util.function.Supplier;

public interface PunishmentHandler<T extends Punishment> {
    PunishmentHandler<Blacklist> BLACKLIST_HANDLER = new Blacklist.Handler();
    PunishmentHandler<Kick> KICK_HANDLER = new Kick.Handler();
    PunishmentHandler<Mute> MUTE_HANDLER = new Mute.Handler();
    PunishmentHandler<Ban> BAN_HANDLER = new Ban.Handler();

    String getTable();
    void createTable();
    Supplier<T> getSupplier();
    String getIssueQuery();
    String getLiftQuery();
}
