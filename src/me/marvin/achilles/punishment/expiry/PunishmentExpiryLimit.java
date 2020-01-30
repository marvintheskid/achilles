package me.marvin.achilles.punishment.expiry;

import lombok.Data;
import me.marvin.achilles.utils.PeriodMatcher;
import org.bukkit.configuration.ConfigurationSection;

@Data
public class PunishmentExpiryLimit {
    private String permission;
    private long maxBanLength;
    private long maxMuteLength;

    public PunishmentExpiryLimit() {}

    public PunishmentExpiryLimit fromConfig(String key, ConfigurationSection section) {
        this.permission = "achilles.temporary." + key;
        this.maxBanLength = PeriodMatcher.parsePeriod(section.getString(key + ".ban"));
        this.maxMuteLength = PeriodMatcher.parsePeriod(section.getString(key + ".mute"));
        return this;
    }
}
