package me.marvin.achilles;

import me.marvin.achilles.utils.config.ConfigPath;

public class Language {
    public static class Other {
        @ConfigPath(path = "failed-to-load-player-profile", config = "config")
        public static String FAILED_TO_LOAD = "";
        @ConfigPath(path = "target-is-offline", config = "config")
        public static String TARGET_IS_OFFLINE = "";
        @ConfigPath(path = "no-permission", config = "config")
        public static String NO_PERMISSION = "";
        @ConfigPath(path = "no-permission-to-override", config = "config")
        public static String NO_PERMISSION_TO_OVERRIDE = "";
        @ConfigPath(path = "not-punished", config = "config")
        public static String NOT_PUNISHED = "";
        @ConfigPath(path = "override-reason", config = "config")
        public static String OVERRIDE_REASON = "";
        @ConfigPath(path = "already-punished", config = "config")
        public static String ALREADY_PUNISHED = "";
        @ConfigPath(path = "plugin-start-kick-message", config = "config")
        public static String PLUGIN_START_KICK_MESSAGE = "";
        @ConfigPath(path = "console-name", config = "config")
        public static String CONSOLE_NAME = "";
    }

    public static class Alts {
        @ConfigPath(path = "alts.check-message", config = "config")
        public static String CONSOLE_NAME = "";
    }

    public static class Kick {
        @ConfigPath(path = "punishments.kick.usage", config = "config")
        public static String USAGE = "";
        @ConfigPath(path = "punishments.kick.message", config = "config")
        public static String MESSAGE = "";
        @ConfigPath(path = "punishments.kick.alert-message", config = "config")
        public static String ALERT_MESSAGE = "";
        @ConfigPath(path = "punishments.kick.silent", config = "config")
        public static String SILENT = "";
        @ConfigPath(path = "punishments.kick.default-reason", config = "config")
        public static String DEFAULT_REASON = "";
        @ConfigPath(path = "punishments.kick.punishment-message", config = "config")
        public static String PUNISHMENT_MESSAGE = "";
    }

    public static class Ban {
        @ConfigPath(path = "punishments.ban.usage", config = "config")
        public static String USAGE = "";
        @ConfigPath(path = "punishments.ban.message", config = "config")
        public static String MESSAGE = "";
        @ConfigPath(path = "punishments.ban.alert-message", config = "config")
        public static String ALERT_MESSAGE = "";
        @ConfigPath(path = "punishments.ban.silent", config = "config")
        public static String SILENT = "";
        @ConfigPath(path = "punishments.ban.default-reason", config = "config")
        public static String DEFAULT_REASON = "";
        @ConfigPath(path = "punishments.ban.punishment-message", config = "config")
        public static String PUNISHMENT_MESSAGE = "";
    }

    public static class Mute {
        @ConfigPath(path = "punishments.mute.usage", config = "config")
        public static String USAGE = "";
        @ConfigPath(path = "punishments.mute.message", config = "config")
        public static String MESSAGE = "";
        @ConfigPath(path = "punishments.mute.alert-message", config = "config")
        public static String ALERT_MESSAGE = "";
        @ConfigPath(path = "punishments.mute.silent", config = "config")
        public static String SILENT = "";
        @ConfigPath(path = "punishments.mute.default-reason", config = "config")
        public static String DEFAULT_REASON = "";
        @ConfigPath(path = "punishments.mute.punishment-message", config = "config")
        public static String PUNISHMENT_MESSAGE = "";
    }

    public static class Blacklist {
        @ConfigPath(path = "punishments.blacklist.usage", config = "config")
        public static String USAGE = "";
        @ConfigPath(path = "punishments.blacklist.message", config = "config")
        public static String MESSAGE = "";
        @ConfigPath(path = "punishments.blacklist.alert-message", config = "config")
        public static String ALERT_MESSAGE = "";
        @ConfigPath(path = "punishments.blacklist.silent", config = "config")
        public static String SILENT = "";
        @ConfigPath(path = "punishments.blacklist.default-reason", config = "config")
        public static String DEFAULT_REASON = "";
        @ConfigPath(path = "punishments.blacklist.punishment-message", config = "config")
        public static String PUNISHMENT_MESSAGE = "";
    }

    public static class Tempban {
        @ConfigPath(path = "punishments.tempban.usage", config = "config")
        public static String USAGE = "";
        @ConfigPath(path = "punishments.tempban.message", config = "config")
        public static String MESSAGE = "";
        @ConfigPath(path = "punishments.tempban.alert-message", config = "config")
        public static String ALERT_MESSAGE = "";
        @ConfigPath(path = "punishments.tempban.silent", config = "config")
        public static String SILENT = "";
        @ConfigPath(path = "punishments.tempban.default-reason", config = "config")
        public static String DEFAULT_REASON = "";
        @ConfigPath(path = "punishments.tempban.punishment-message", config = "config")
        public static String PUNISHMENT_MESSAGE = "";
    }

    public static class Tempmute {
        @ConfigPath(path = "punishments.tempmute.usage", config = "config")
        public static String USAGE = "";
        @ConfigPath(path = "punishments.tempmute.message", config = "config")
        public static String MESSAGE = "";
        @ConfigPath(path = "punishments.tempmute.alert-message", config = "config")
        public static String ALERT_MESSAGE = "";
        @ConfigPath(path = "punishments.tempmute.silent", config = "config")
        public static String SILENT = "";
        @ConfigPath(path = "punishments.tempmute.default-reason", config = "config")
        public static String DEFAULT_REASON = "";
        @ConfigPath(path = "punishments.tempmute.punishment-message", config = "config")
        public static String PUNISHMENT_MESSAGE = "";
    }

    public static class Unban {
        @ConfigPath(path = "punishments.unban.usage", config = "config")
        public static String USAGE = "";
        @ConfigPath(path = "punishments.unban.message", config = "config")
        public static String MESSAGE = "";
        @ConfigPath(path = "punishments.unban.alert-message", config = "config")
        public static String ALERT_MESSAGE = "";
        @ConfigPath(path = "punishments.unban.silent", config = "config")
        public static String SILENT = "";
        @ConfigPath(path = "punishments.unban.default-reason", config = "config")
        public static String DEFAULT_REASON = "";
    }

    public static class Unmute {
        @ConfigPath(path = "punishments.unmute.usage", config = "config")
        public static String USAGE = "";
        @ConfigPath(path = "punishments.unmute.message", config = "config")
        public static String MESSAGE = "";
        @ConfigPath(path = "punishments.unmute.alert-message", config = "config")
        public static String ALERT_MESSAGE = "";
        @ConfigPath(path = "punishments.unmute.silent", config = "config")
        public static String SILENT = "";
        @ConfigPath(path = "punishments.unmute.default-reason", config = "config")
        public static String DEFAULT_REASON = "";
    }

    public static class Unblacklist {
        @ConfigPath(path = "punishments.unblacklist.usage", config = "config")
        public static String USAGE = "";
        @ConfigPath(path = "punishments.unblacklist.message", config = "config")
        public static String MESSAGE = "";
        @ConfigPath(path = "punishments.unblacklist.alert-message", config = "config")
        public static String ALERT_MESSAGE = "";
        @ConfigPath(path = "punishments.unblacklist.silent", config = "config")
        public static String SILENT = "";
        @ConfigPath(path = "punishments.unblacklist.default-reason", config = "config")
        public static String DEFAULT_REASON = "";
    }
}
