package me.marvin.achilles.messenger.impl;

import com.google.gson.JsonObject;
import me.marvin.achilles.Achilles;
import me.marvin.achilles.Variables;
import me.marvin.achilles.messenger.Message;
import me.marvin.achilles.messenger.MessageType;
import me.marvin.achilles.messenger.Messenger;
import org.bukkit.Bukkit;

import java.sql.SQLException;

import static me.marvin.achilles.Variables.Messenger.SQL.*;

public class SQLMessenger extends Messenger {
    private long lastMessage = -1337;

    @Override
    public void initialize() {
        Achilles.getConnection().update(true, "CREATE TABLE IF NOT EXISTS `" + TABLE_NAME + "` ("
                + "`id` bigint PRIMARY KEY NOT NULL AUTO_INCREMENT,"
                + "`type` smallint(32) NOT NULL,"
                + "`origin` varchar(64) NOT NULL,"
                + "`data` text NOT NULL,"
                + "`timestamp` timestamp NOT NULL) DEFAULT CHARSET=utf8;",
            (result) -> {}
        );

        Achilles.getConnection().query(true, "SELECT MAX(`id`) AS `last` FROM `" + TABLE_NAME + "`", (result) -> {
            try {
                if (result.next()) this.lastMessage = result.getLong("last");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        Bukkit.getScheduler().runTaskTimerAsynchronously(Achilles.getInstance(), new HousekeepRunnable(), HOUSEKEEP_TRESHOLD * 20L, HOUSEKEEP_TRESHOLD * 20L);
        Bukkit.getScheduler().runTaskTimerAsynchronously(Achilles.getInstance(), new PollRunnable(), POLL_RATE, POLL_RATE);
    }

    @Override
    public void sendMessage(Message message) {
        Achilles.getConnection().update(true, "INSERT INTO `" + Variables.Messenger.SQL.TABLE_NAME + "` (`type`, `origin`, `data`, `timestamp`) VALUES (?, ?, ?, NOW())",
            (result) -> {},
            message.getType().ordinal(), Variables.Database.SERVER_NAME, message.getData().toString()
        );
    }

    @Override
    public void shutdown() {
        return;
    }

    private class HousekeepRunnable implements Runnable {
        @Override
        public void run() {
            Achilles.getConnection().update(true, "DELETE FROM `" + TABLE_NAME + "` WHERE (NOW() - `timestamp` > " + HOUSEKEEP_TRESHOLD + ")",
                (result) -> {}
            );
        }
    }

    private class PollRunnable implements Runnable {
        @Override
        public void run() {
            Achilles.getConnection().query(true, "SELECT * FROM `" + TABLE_NAME + "` WHERE `id` > ? AND (NOW() - `timestamp` < " + MESSAGE_TIMESTAMP_LIMIT + ")",
                (result) -> {
                    try {
                        while (result.next()) {
                            long id = result.getLong("id");
                            lastMessage = Math.max(lastMessage, id);

                            if (result.getString("origin").equals(Variables.Database.SERVER_NAME)) {
                                continue;
                            }

                            MessageType type = MessageType.fromId(result.getInt("type"));
                            if (type == null) {
                                Achilles.getInstance().getLogger().warning("[Messenger-SQL] Got message with a bad type (id: " + id + "), deleting entry...");
                                Achilles.getConnection().update(true, "DELETE FROM `" + TABLE_NAME + "` WHERE `id` = ?", (response) -> {}, id);
                                return;
                            }

                            JsonObject data = PARSER.parse(result.getString("data")).getAsJsonObject();
                            handleIncoming(new Message(type, data));
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }, lastMessage
            );
        }
    }
}
