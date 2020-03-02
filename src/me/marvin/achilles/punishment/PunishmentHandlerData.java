package me.marvin.achilles.punishment;

import lombok.Data;

import java.util.function.Supplier;

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
