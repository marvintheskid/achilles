package me.marvin.achilles.messenger.impl;

import me.marvin.achilles.Achilles;
import me.marvin.achilles.Variables;
import me.marvin.achilles.messenger.Message;
import me.marvin.achilles.messenger.MessageType;
import me.marvin.achilles.messenger.Messenger;
import org.bukkit.Bukkit;

import java.sql.SQLException;

public class SQLMessenger extends Messenger {
    private long lastMessage = -1337;

    @Override
    public void initialize() {
        Achilles.getConnection().update(true, "CREATE TABLE IF NOT EXISTS `" + Variables.Messenger.SQL.TABLE_NAME + "` ("
                + "`id` bigint PRIMARY KEY NOT NULL AUTO_INCREMENT,"
                + "`type` smallint(32) NOT NULL,"
                + "`data` text NOT NULL,"
                + "`timestamp` timestamp NOT NULL) DEFAULT CHARSET=utf8;",
            (result) -> {}
        );

        Achilles.getConnection().query(true, "SELECT MAX(`id`) AS `last` FROM `" + Variables.Messenger.SQL.TABLE_NAME + "`", (result) -> {
            try {
                if (result.next()) this.lastMessage = result.getLong("last");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        Bukkit.getScheduler().runTaskTimerAsynchronously(Achilles.getInstance(), () -> {
            Achilles.getConnection().update(true, "DELETE FROM `" + Variables.Messenger.SQL.TABLE_NAME + "` WHERE (NOW() - `timestamp` > " + Variables.Messenger.SQL.HOUSEKEEP_TRESHOLD + ")",
                (result) -> {}
            );
        }, Variables.Messenger.SQL.HOUSEKEEP_TRESHOLD * 20L, Variables.Messenger.SQL.HOUSEKEEP_TRESHOLD * 20L);

        Bukkit.getScheduler().runTaskTimerAsynchronously(Achilles.getInstance(), () -> {
            Achilles.getConnection().query(true, "SELECT * FROM `" + Variables.Messenger.SQL.TABLE_NAME + "` WHERE `id` > ? AND (NOW() - `timestamp` < " + Variables.Messenger.SQL.MESSAGE_TIMESTAMP_LIMIT + ")",
                (result) -> {
                    try {
                        while (result.next()) {
                            long id = result.getLong("id");
                            this.lastMessage = Math.max(this.lastMessage, id);
                            MessageType type = MessageType.fromId(result.getInt("type"));
                            if (type == null) {
                                Achilles.getInstance().getLogger().warning("[Messenger-SQL] Got message with a bad type (id: " + id + "), deleting entry...");
                                Achilles.getConnection().update(true, "DELETE FROM `" + Variables.Messenger.SQL.TABLE_NAME + "` WHERE `id` = ?", (response) -> {}, id);
                                return;
                            }
                            String data = result.getString("data");
                            this.handleIncoming(new Message(type, data));
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }, lastMessage
            );
        }, Variables.Messenger.SQL.POLL_RATE, Variables.Messenger.SQL.POLL_RATE);
    }

    @Override
    public void sendMessage(Message message) {
        Achilles.getConnection().update(true, "INSERT INTO `" + Variables.Messenger.SQL.TABLE_NAME + "` (`timestamp`, `type`, `data`) VALUES (NOW(), ?, ?)",
            (result) -> {},
            message.getType().ordinal(), message.getData()
        );
    }

    @Override
    public void shutdown() {
        return;
    }
}
