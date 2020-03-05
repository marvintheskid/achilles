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

@Getter
public class SimpleProfile extends Profile {
    private Map<Class<? extends Punishment>, List<Punishment>> cache;
    private String name;

    public SimpleProfile(UUID uuid) {
        super(uuid);
        this.cache = new HashMap<>();
    }

    @Override
    public <T extends LiftablePunishment> Optional<T> getActive(Class<T> type) {
        return getPunishments(type)
            .stream()
            .filter(LiftablePunishment::isActive)
            .min(Comparator.comparingLong(Punishment::getId));
    }


    //TODO: Async getpunishments
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
