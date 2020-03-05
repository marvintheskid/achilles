package me.marvin.achilles.punishment.expiry;

import lombok.Data;
import me.marvin.achilles.utils.PeriodMatcher;
import org.bukkit.configuration.ConfigurationSection;

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
