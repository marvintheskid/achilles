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

/*
 * Copyright (c) 2019 marvintheskid (Kovács Márton)
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