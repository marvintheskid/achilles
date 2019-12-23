package me.marvin.achilles;

import lombok.Getter;
import me.marvin.achilles.profile.ProfileHandler;
import me.marvin.achilles.punishment.Punishment;
import me.marvin.achilles.punishment.PunishmentHandler;
import me.marvin.achilles.punishment.impl.Ban;
import me.marvin.achilles.punishment.impl.Blacklist;
import me.marvin.achilles.punishment.impl.Kick;
import me.marvin.achilles.punishment.impl.Mute;
import me.marvin.achilles.utils.config.Config;
import me.marvin.achilles.utils.sql.HikariConnection;
import org.bukkit.plugin.java.JavaPlugin;

import static me.marvin.achilles.Variables.Database.*;
import java.util.HashMap;
import java.util.Map;

//TODO: cleanup handlers
public class Achilles extends JavaPlugin {
    @Getter private static Map<Class<? extends Punishment>, PunishmentHandler> handlers;
    @Getter private static ProfileHandler profileHandler;
    @Getter private static HikariConnection connection;
    @Getter private static Achilles instance;
    @Getter private static Config config;

    @Override
    public void onEnable() {
        instance = this;
        config = new Config("config", this);
        config.saveDefaultConfig();
        profileHandler = new ProfileHandler();
        connection = new HikariConnection(
            Credentials.HOST,
            Credentials.PORT,
            Credentials.USER,
            Credentials.PASSWORD,
            DATABASE_NAME,
            ASYNC,
            POOL_SIZE
        );
        handlers = new HashMap<>() {
            @Override
            public PunishmentHandler put(Class<? extends Punishment> key, PunishmentHandler value) {
                value.createTable();
                return super.put(key, value);
            }
        };
        handlers.put(Ban.class, () -> Achilles.getConnection().update("CREATE TABLE IF NOT EXISTS `" + BAN_TABLE_NAME + "` ("
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
        handlers.put(Kick.class, () -> Achilles.getConnection().update("CREATE TABLE IF NOT EXISTS `" + KICK_TABLE_NAME + "` ("
                + "`id` int PRIMARY KEY NOT NULL AUTO_INCREMENT,"
                + "`server` varchar(200) NOT NULL,"
                + "`issuer` varchar(36) NOT NULL,"
                + "`target` varchar(36) NOT NULL,"
                + "`issueReason` varchar(200) NOT NULL,"
                + "`issuedOn` bigint NOT NULL) DEFAULT CHARSET=utf8;",
            (result) -> {})
        );
        handlers.put(Mute.class, () -> Achilles.getConnection().update("CREATE TABLE IF NOT EXISTS `" + MUTE_TABLE_NAME + "` ("
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
        handlers.put(Blacklist.class, () -> Achilles.getConnection().update("CREATE TABLE IF NOT EXISTS `" + BLACKLIST_TABLE_NAME + "` ("
                + "`id` int PRIMARY KEY NOT NULL AUTO_INCREMENT,"
                + "`server` varchar(200) NOT NULL,"
                + "`issuer` varchar(36) NOT NULL,"
                + "`target` varchar(36) NOT NULL,"
                + "`issueReason` varchar(200) NOT NULL,"
                + "`issuedOn` bigint NOT NULL,"
                + "`liftedBy` varchar(36),"
                + "`liftedOn` bigint,"
                + "`liftReason` varchar(200),"
                + "`active` tinyint(1) NOT NULL) DEFAULT CHARSET=utf8;",
            (result) -> {})
        );
        Achilles.getConnection().update("CREATE TABLE IF NOT EXISTS `" + ALTS_TABLE_NAME + "` ("
            + "`uuid` varchar(36) UNIQUE KEY NOT NULL,"
            + "`username` varchar(16) NOT NULL,"
            + "`ip` varchar(15) NOT NULL,"
            + "`lastLogin` bigint NOT NULL) DEFAULT CHARSET=utf8;",
            (result) -> {});
    }

    @Override
    public void onDisable() {
        connection.disconnect();
        connection = null;
        instance = null;
        profileHandler = null;
        config = null;
        handlers = null;
    }
}
