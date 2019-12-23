package me.marvin.achilles;

import me.marvin.achilles.utils.config.ConfigPath;

public class Language {
    public static class Other {
        @ConfigPath(path = "failed-to-load-player-profile", config = "language")
        public static String FAILED_TO_LOAD = "";
        @ConfigPath(path = "target-is-offline", config = "language")
        public static String TARGET_IS_OFFLINE = "";
        @ConfigPath(path = "no-permission", config = "language")
        public static String NO_PERMISSION = "";
        @ConfigPath(path = "no-permission-to-override", config = "language")
        public static String NO_PERMISSION_TO_OVERRIDE = "";
        @ConfigPath(path = "not-punished", config = "language")
        public static String NOT_PUNISHED = "";
        @ConfigPath(path = "override-reason", config = "language")
        public static String OVERRIDE_REASON = "";
        @ConfigPath(path = "already-punished", config = "language")
        public static String ALREADY_PUNISHED = "";
        @ConfigPath(path = "plugin-start-kick-message", config = "language")
        public static String PLUGIN_START_KICK_MESSAGE = "";
        @ConfigPath(path = "console-name", config = "language")
        public static String CONSOLE_NAME = "";
    }

    public static class Kick {
        @ConfigPath(path = "kick.usage", config = "language")
        public static String USAGE = "";
        @ConfigPath(path = "kick.message", config = "language")
        public static String MESSAGE = "";
        @ConfigPath(path = "kick.alert-message", config = "language")
        public static String ALERT_MESSAGE = "";
        @ConfigPath(path = "kick.silent", config = "language")
        public static String SILENT = "";
        @ConfigPath(path = "kick.default-reason", config = "language")
        public static String DEFAULT_REASON = "";
        @ConfigPath(path = "kick.punishment-message", config = "language")
        public static String PUNISHMENT_MESSAGE = "";
    }

    public static class Ban {
        @ConfigPath(path = "ban.usage", config = "language")
        public static String USAGE = "";
        @ConfigPath(path = "ban.message", config = "language")
        public static String MESSAGE = "";
        @ConfigPath(path = "ban.alert-message", config = "language")
        public static String ALERT_MESSAGE = "";
        @ConfigPath(path = "ban.silent", config = "language")
        public static String SILENT = "";
        @ConfigPath(path = "ban.default-reason", config = "language")
        public static String DEFAULT_REASON = "";
        @ConfigPath(path = "ban.punishment-message", config = "language")
        public static String PUNISHMENT_MESSAGE = "";
    }

    public static class Mute {
        @ConfigPath(path = "mute.usage", config = "language")
        public static String USAGE = "";
        @ConfigPath(path = "mute.message", config = "language")
        public static String MESSAGE = "";
        @ConfigPath(path = "mute.alert-message", config = "language")
        public static String ALERT_MESSAGE = "";
        @ConfigPath(path = "mute.silent", config = "language")
        public static String SILENT = "";
        @ConfigPath(path = "mute.default-reason", config = "language")
        public static String DEFAULT_REASON = "";
        @ConfigPath(path = "mute.punishment-message", config = "language")
        public static String PUNISHMENT_MESSAGE = "";
    }

    public static class Blacklist {
        @ConfigPath(path = "blacklist.usage", config = "language")
        public static String USAGE = "";
        @ConfigPath(path = "blacklist.message", config = "language")
        public static String MESSAGE = "";
        @ConfigPath(path = "blacklist.alert-message", config = "language")
        public static String ALERT_MESSAGE = "";
        @ConfigPath(path = "blacklist.silent", config = "language")
        public static String SILENT = "";
        @ConfigPath(path = "blacklist.default-reason", config = "language")
        public static String DEFAULT_REASON = "";
        @ConfigPath(path = "blacklist.punishment-message", config = "language")
        public static String PUNISHMENT_MESSAGE = "";
    }

    public static class Tempban {
        @ConfigPath(path = "tempban.usage", config = "language")
        public static String USAGE = "";
        @ConfigPath(path = "tempban.message", config = "language")
        public static String MESSAGE = "";
        @ConfigPath(path = "tempban.alert-message", config = "language")
        public static String ALERT_MESSAGE = "";
        @ConfigPath(path = "tempban.silent", config = "language")
        public static String SILENT = "";
        @ConfigPath(path = "tempban.default-reason", config = "language")
        public static String DEFAULT_REASON = "";
        @ConfigPath(path = "tempban.punishment-message", config = "language")
        public static String PUNISHMENT_MESSAGE = "";
    }

    public static class Tempmute {
        @ConfigPath(path = "tempmute.usage", config = "language")
        public static String USAGE = "";
        @ConfigPath(path = "tempmute.message", config = "language")
        public static String MESSAGE = "";
        @ConfigPath(path = "tempmute.alert-message", config = "language")
        public static String ALERT_MESSAGE = "";
        @ConfigPath(path = "tempmute.silent", config = "language")
        public static String SILENT = "";
        @ConfigPath(path = "tempmute.default-reason", config = "language")
        public static String DEFAULT_REASON = "";
        @ConfigPath(path = "tempmute.punishment-message", config = "language")
        public static String PUNISHMENT_MESSAGE = "";
    }

    public static class Unban {
        @ConfigPath(path = "unban.usage", config = "language")
        public static String USAGE = "";
        @ConfigPath(path = "unban.message", config = "language")
        public static String MESSAGE = "";
        @ConfigPath(path = "unban.alert-message", config = "language")
        public static String ALERT_MESSAGE = "";
        @ConfigPath(path = "unban.silent", config = "language")
        public static String SILENT = "";
        @ConfigPath(path = "unban.default-reason", config = "language")
        public static String DEFAULT_REASON = "";
    }

    public static class Unmute {
        @ConfigPath(path = "unmute.usage", config = "language")
        public static String USAGE = "";
        @ConfigPath(path = "unmute.message", config = "language")
        public static String MESSAGE = "";
        @ConfigPath(path = "unmute.alert-message", config = "language")
        public static String ALERT_MESSAGE = "";
        @ConfigPath(path = "unmute.silent", config = "language")
        public static String SILENT = "";
        @ConfigPath(path = "unmute.default-reason", config = "language")
        public static String DEFAULT_REASON = "";
    }

    public static class Unblacklist {
        @ConfigPath(path = "unblacklist.usage", config = "language")
        public static String USAGE = "";
        @ConfigPath(path = "unblacklist.message", config = "language")
        public static String MESSAGE = "";
        @ConfigPath(path = "unblacklist.alert-message", config = "language")
        public static String ALERT_MESSAGE = "";
        @ConfigPath(path = "unblacklist.silent", config = "language")
        public static String SILENT = "";
        @ConfigPath(path = "unblacklist.default-reason", config = "language")
        public static String DEFAULT_REASON = "";
    }
}
