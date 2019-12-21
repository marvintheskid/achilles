package me.marvin.achilleus;

import lombok.Getter;
import me.marvin.achilleus.punishment.Punishment;
import me.marvin.achilleus.punishment.PunishmentHandler;
import me.marvin.achilleus.punishment.impl.Ban;
import me.marvin.achilleus.utils.sql.HikariConnection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class Achilles extends JavaPlugin {
    @Getter private static Map<Class<? extends Punishment>, PunishmentHandler> handlers;
    @Getter private static HikariConnection connection;

    @Override
    public void onEnable() {
        connection = new HikariConnection(null, 1, 1, null, null, null);
        handlers = new HashMap<>() {
            @Override
            public PunishmentHandler put(Class<? extends Punishment> key, PunishmentHandler value) {
                value.createTable();
                return super.put(key, value);
            }
        };
        handlers.put(Ban.class, () -> Achilles.getConnection().update("CREATE TABLE IF NOT EXISTS `" + Variables.Database.BAN_TABLE_NAME + "` ("
             + "`id` int PRIMARY KEY NOT NULL AUTO_INCREMENT,"
             + "`server` varchar(200) NOT NULL,"
             + "`issuer` varchar(36) NOT NULL,"
             + "`target` varchar(36) NOT NULL,"
             + "`issueReason` varchar(200) NOT NULL,"
             + "`issuedOn` bigint NOT NULL,"
             + "`until` bigint NOT NULL,"
             + "`liftedBy` varchar(36),"
             + "`liftedOn` bigint,"
             + "`liftReason` varchar(200),"
             + "`active` tinyint(1) NOT NULL) DEFAULT CHARSET=utf8;",
            (result) -> {})
        );
    }
}
