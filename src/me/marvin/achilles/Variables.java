package me.marvin.achilles;

import me.marvin.achilles.utils.config.ConfigPath;
import me.marvin.achilles.utils.config.InitializeAfterConfig;

import java.text.SimpleDateFormat;
import java.util.Locale;

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

public class Variables {
    public static class Database {
        public static class Credentials {
            @ConfigPath(path = "database.host", config = "config")
            public static String HOST = "";
            @ConfigPath(path = "database.port", config = "config")
            public static int PORT = 3306;
            @ConfigPath(path = "database.user", config = "config")
            public static String USER = "";
            @ConfigPath(path = "database.password", config = "config")
            public static String PASSWORD = "";
        }

        @ConfigPath(path = "database.async", config = "config")
        public static boolean ASYNC = false;
        @ConfigPath(path = "database.pool-size", config = "config")
        public static int POOL_SIZE = 1;
        @ConfigPath(path = "database.database", config = "config")
        public static String DATABASE_NAME = "";
        @ConfigPath(path = "database.tables.bans", config = "config")
        public static String BAN_TABLE_NAME = "";
        @ConfigPath(path = "database.tables.kicks", config = "config")
        public static String KICK_TABLE_NAME = "";
        @ConfigPath(path = "database.tables.mutes", config = "config")
        public static String MUTE_TABLE_NAME = "";
        @ConfigPath(path = "database.tables.blacklists", config = "config")
        public static String BLACKLIST_TABLE_NAME = "";
        @ConfigPath(path = "database.tables.alts", config = "config")
        public static String ALTS_TABLE_NAME = "";
        @ConfigPath(path = "server-name", config = "config")
        public static String SERVER_NAME = "";
    }

    public static class Messenger {
        @ConfigPath(path = "messenger.type", config = "config")
        public static String TYPE = "";

        public static class SQL {
            @ConfigPath(path = "messenger.sql.poll-rate", config = "config")
            public static long POLL_RATE = 0;
            @ConfigPath(path = "messenger.sql.housekeep-threshold", config = "config")
            public static long HOUSEKEEP_TRESHOLD = 0;
            @ConfigPath(path = "messenger.sql.message-timestamp-limit", config = "config")
            public static long MESSAGE_TIMESTAMP_LIMIT = 0;
            @ConfigPath(path = "messenger.sql.table-name", config = "config")
            public static String TABLE_NAME = "";
        }

        public static class Redis {
            @ConfigPath(path = "messenger.redis.host", config = "config")
            public static String HOST = "";
            @ConfigPath(path = "messenger.redis.port", config = "config")
            public static int PORT = 6379;
            @ConfigPath(path = "messenger.redis.auth.authenticate", config = "config")
            public static boolean NEED_AUTH = false;
            @ConfigPath(path = "messenger.redis.auth.password", config = "config")
            public static String PASSWORD = "";
        }
    }

    public static class Punishment {
        @ConfigPath(path = "punishment.active.true", config = "config")
        public static String PUNISHMENT_ACTIVE_TRUE = "";
        @ConfigPath(path = "punishment.active.false", config = "config")
        public static String PUNISHMENT_ACTIVE_FALSE = "";
    }

    public static class Alts {
        @ConfigPath(path = "alts.separator", config = "config")
        public static String SEPARATOR = "";
        @ConfigPath(path = "alts.online", config = "config")
        public static String ONLINE_ALT = "";
        @ConfigPath(path = "alts.banned", config = "config")
        public static String BANNED_ALT = "";
        @ConfigPath(path = "alts.offline", config = "config")
        public static String OFFLINE_ALT = "";
    }

    public static class Date {
        @ConfigPath(path = "date-formatting.pattern-settings.locale", config = "config")
        private static String LOCALE_STRING = "";
        @ConfigPath(path = "date-formatting.pattern-settings.pattern", config = "config")
        private static String DATE_STRING = "";

        @ConfigPath(path = "date-formatting.year", config = "config")
        public static String YEAR = "";
        @ConfigPath(path = "date-formatting.years", config = "config")
        public static String YEARS = "";
        @ConfigPath(path = "date-formatting.month", config = "config")
        public static String MONTH = "";
        @ConfigPath(path = "date-formatting.months", config = "config")
        public static String MONTHS = "";
        @ConfigPath(path = "date-formatting.week", config = "config")
        public static String WEEK = "";
        @ConfigPath(path = "date-formatting.weeks", config = "config")
        public static String WEEKS = "";
        @ConfigPath(path = "date-formatting.day", config = "config")
        public static String DAY = "";
        @ConfigPath(path = "date-formatting.days", config = "config")
        public static String DAYS = "";
        @ConfigPath(path = "date-formatting.hour", config = "config")
        public static String HOUR = "";
        @ConfigPath(path = "date-formatting.hours", config = "config")
        public static String HOURS = "";
        @ConfigPath(path = "date-formatting.minute", config = "config")
        public static String MINUTE = "";
        @ConfigPath(path = "date-formatting.minutes", config = "config")
        public static String MINUTES = "";
        @ConfigPath(path = "date-formatting.second", config = "config")
        public static String SECOND = "";
        @ConfigPath(path = "date-formatting.seconds", config = "config")
        public static String SECONDS = "";
        @ConfigPath(path = "date-formatting.permanent", config = "config")
        public static String PERMANENT = "";
        @ConfigPath(path = "date-formatting.expired", config = "config")
        public static String EXPIRED = "";

        public static Locale LOCALE;
        public static SimpleDateFormat DATE_FORMAT;

        @InitializeAfterConfig(config = "config")
        private static void loadLocale() {
            LOCALE = new Locale.Builder().setLanguageTag(LOCALE_STRING).build();
            DATE_FORMAT = new SimpleDateFormat(DATE_STRING, LOCALE);
        }
    }
}
