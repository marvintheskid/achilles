package me.marvin.achilles.punishment;

import lombok.Data;

import java.util.function.Supplier;

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
public class PunishmentHandlerData {
    private String table;
    private Supplier<? extends Punishment> supplier;
    private PunishmentHandler handler;

    public PunishmentHandlerData(String table, Supplier<? extends Punishment> supplier, PunishmentHandler handler) {
        this.table = table;
        this.supplier = supplier;
        this.handler = handler;
    }

    @Data
    public static class Liftable extends PunishmentHandlerData {
        private String batchQuery;

        public Liftable(String table, Supplier<? extends Punishment> supplier, PunishmentHandler handler, String batchQuery) {
            super(table, supplier, handler);
            this.batchQuery = batchQuery;
        }
    }
}
