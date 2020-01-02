package me.marvin.achilles;

import lombok.Getter;
import me.marvin.achilles.commands.BanCommand;
import me.marvin.achilles.commands.MuteCommand;
import me.marvin.achilles.commands.TempbanCommand;
import me.marvin.achilles.listener.ChatListener;
import me.marvin.achilles.listener.JoinListener;
import me.marvin.achilles.listener.LoginListener;
import me.marvin.achilles.listener.QuitListener;
import me.marvin.achilles.messenger.Messenger;
import me.marvin.achilles.messenger.impl.RedisMessenger;
import me.marvin.achilles.messenger.impl.SQLMessenger;
import me.marvin.achilles.profile.ProfileHandler;
import me.marvin.achilles.punishment.Punishment;
import me.marvin.achilles.punishment.PunishmentHandlerData;
import me.marvin.achilles.punishment.expiry.PunishmentExpiryLimit;
import me.marvin.achilles.punishment.impl.Ban;
import me.marvin.achilles.punishment.impl.Blacklist;
import me.marvin.achilles.punishment.impl.Kick;
import me.marvin.achilles.punishment.impl.Mute;
import me.marvin.achilles.utils.DependencyManager;
import me.marvin.achilles.utils.config.Config;
import me.marvin.achilles.utils.sql.HikariConnection;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import static me.marvin.achilles.Variables.Database.*;
import java.util.HashMap;
import java.util.Map;

//TODO: cleanup handlers
public class Achilles extends JavaPlugin {
    @Getter private static Map<Class<? extends Punishment>, PunishmentHandlerData> handlers;
    @Getter private static Map<String, PunishmentExpiryLimit> expiryData;
    @Getter private static ProfileHandler profileHandler;
    @Getter private static HikariConnection connection;
    @Getter private static Messenger messenger;
    @Getter private static Achilles instance;
    @Getter private static Config configuration;

    @Override
    public void onEnable() {
        instance = this;
        DependencyManager.loadDependencies();
        configuration = new Config("config", this);
        configuration.saveDefaultConfig();
        configuration.loadAnnotatedValues(Language.Other.class);
        configuration.loadAnnotatedValues(Language.Ban.class);
        configuration.loadAnnotatedValues(Language.Tempban.class);
        configuration.loadAnnotatedValues(Language.Unban.class);
        configuration.loadAnnotatedValues(Language.Kick.class);
        configuration.loadAnnotatedValues(Language.Blacklist.class);
        configuration.loadAnnotatedValues(Language.Unblacklist.class);
        configuration.loadAnnotatedValues(Language.Mute.class);
        configuration.loadAnnotatedValues(Language.Tempmute.class);
        configuration.loadAnnotatedValues(Language.Unmute.class);

        configuration.loadAnnotatedValues(Variables.Database.class);
        configuration.loadAnnotatedValues(Variables.Database.Credentials.class);

        configuration.loadAnnotatedValues(Variables.Messenger.class);
        configuration.loadAnnotatedValues(Variables.Messenger.SQL.class);
        configuration.loadAnnotatedValues(Variables.Messenger.Redis.class);

        configuration.loadAnnotatedValues(Variables.Alts.class);
        configuration.loadAnnotatedValues(Variables.Date.class);
        configuration.loadAnnotatedValues(Variables.Punishment.class);
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
        connection.connect();

        if (connection.getConnection() == null) {
            getLogger().severe("[Database] Failed to connect to the database, shutting down...");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        handlers = new HashMap<Class<? extends Punishment>, PunishmentHandlerData>() {
            @Override
            public PunishmentHandlerData put(Class<? extends Punishment> key, PunishmentHandlerData value) {
                value.getHandler().createTable();
                return super.put(key, value);
            }
        };

        expiryData = new HashMap<>();

        switch (Variables.Messenger.TYPE.toUpperCase()) {
            case "REDIS": {
                messenger = new RedisMessenger();
                break;
            }
            default:
            case "SQL": {
                messenger = new SQLMessenger();
                break;
            }
        }

        initHandlers();
        Bukkit.getPluginManager().registerEvents(new LoginListener(), this);
        Bukkit.getPluginManager().registerEvents(new JoinListener(), this);
        Bukkit.getPluginManager().registerEvents(new QuitListener(), this);
        Bukkit.getPluginManager().registerEvents(new ChatListener(), this);

        ConfigurationSection temporarySection = configuration.getFileConfiguration().getConfigurationSection("temporary-limits");
        temporarySection.getKeys(false).forEach(key -> expiryData.put(key, new PunishmentExpiryLimit().fromConfig(key, temporarySection)));

        new BanCommand().setExecutor(this);
        new TempbanCommand().setExecutor(this);
        new MuteCommand().setExecutor(this);
    }

    @Override
    public void onDisable() {
        if (connection != null) connection.disconnect();
        messenger.shutdown();
        messenger = null;
        connection = null;
        instance = null;
        profileHandler = null;
        configuration = null;
        handlers = null;
    }

    private void initHandlers() {
        handlers.put(Ban.class, new PunishmentHandlerData(BAN_TABLE_NAME, Ban::new, () -> Achilles.getConnection().update("CREATE TABLE IF NOT EXISTS `" + BAN_TABLE_NAME + "` ("
                + "`id` int PRIMARY KEY NOT NULL AUTO_INCREMENT,"
                + "`server` varchar(200) NOT NULL,"
                + "`issuer` binary(16) NOT NULL,"
                + "`target` binary(16) NOT NULL,"
                + "`issueReason` varchar(200) NOT NULL,"
                + "`issuedOn` bigint NOT NULL,"
                + "`until` bigint NOT NULL,"
                + "`liftedBy` binary(16),"
                + "`liftedOn` bigint,"
                + "`liftReason` varchar(200),"
                + "`active` tinyint(1) NOT NULL) DEFAULT CHARSET=utf8;",
            (result) -> {})
        ));
        handlers.put(Kick.class, new PunishmentHandlerData(KICK_TABLE_NAME, Kick::new, () -> Achilles.getConnection().update("CREATE TABLE IF NOT EXISTS `" + KICK_TABLE_NAME + "` ("
                + "`id` int PRIMARY KEY NOT NULL AUTO_INCREMENT,"
                + "`server` varchar(200) NOT NULL,"
                + "`issuer` binary(16) NOT NULL,"
                + "`target` binary(16) NOT NULL,"
                + "`issueReason` varchar(200) NOT NULL,"
                + "`issuedOn` bigint NOT NULL) DEFAULT CHARSET=utf8;",
            (result) -> {})
        ));
        handlers.put(Mute.class, new PunishmentHandlerData(MUTE_TABLE_NAME, Mute::new, () -> Achilles.getConnection().update("CREATE TABLE IF NOT EXISTS `" + MUTE_TABLE_NAME + "` ("
                + "`id` int PRIMARY KEY NOT NULL AUTO_INCREMENT,"
                + "`server` varchar(200) NOT NULL,"
                + "`issuer` binary(16) NOT NULL,"
                + "`target` binary(16) NOT NULL,"
                + "`issueReason` varchar(200) NOT NULL,"
                + "`issuedOn` bigint NOT NULL,"
                + "`until` bigint NOT NULL,"
                + "`liftedBy` binary(16),"
                + "`liftedOn` bigint,"
                + "`liftReason` varchar(200),"
                + "`active` tinyint(1) NOT NULL) DEFAULT CHARSET=utf8;",
            (result) -> {})
        ));
        handlers.put(Blacklist.class, new PunishmentHandlerData(BLACKLIST_TABLE_NAME, Blacklist::new, () -> Achilles.getConnection().update("CREATE TABLE IF NOT EXISTS `" + BLACKLIST_TABLE_NAME + "` ("
                + "`id` int PRIMARY KEY NOT NULL AUTO_INCREMENT,"
                + "`server` varchar(200) NOT NULL,"
                + "`issuer` binary(16) NOT NULL,"
                + "`target` binary(16) NOT NULL,"
                + "`issueReason` varchar(200) NOT NULL,"
                + "`issuedOn` bigint NOT NULL,"
                + "`liftedBy` binary(16),"
                + "`liftedOn` bigint,"
                + "`liftReason` varchar(200),"
                + "`active` tinyint(1) NOT NULL) DEFAULT CHARSET=utf8;",
            (result) -> {})
        ));
        Achilles.getConnection().update("CREATE TABLE IF NOT EXISTS `" + ALTS_TABLE_NAME + "` ("
                + "`uuid` binary(16) UNIQUE KEY NOT NULL,"
                + "`username` varchar(16) NOT NULL,"
                + "`ip` varchar(15) NOT NULL,"
                + "`lastLogin` bigint NOT NULL) DEFAULT CHARSET=utf8;",
            (result) -> {}
        );
    }
}
