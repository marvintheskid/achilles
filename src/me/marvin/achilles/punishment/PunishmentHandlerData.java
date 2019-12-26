package me.marvin.achilles.punishment;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.function.Supplier;

@Data
@AllArgsConstructor
public class PunishmentHandlerData {
    private String table;
    private Supplier<? extends Punishment> supplier;
    private PunishmentHandler handler;
}
