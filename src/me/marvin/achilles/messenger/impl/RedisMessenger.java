package me.marvin.achilles.messenger.impl;

import me.marvin.achilles.Achilles;
import me.marvin.achilles.messenger.Message;
import me.marvin.achilles.messenger.MessageType;
import me.marvin.achilles.messenger.Messenger;
import org.bukkit.Bukkit;
import redis.clients.jedis.*;

import static me.marvin.achilles.Variables.Messenger.Redis.*;

public class RedisMessenger extends Messenger {
    private JedisPubSub listener;
    private JedisPool pool;

    @Override
    public void initialize() {
        Bukkit.getScheduler().runTaskAsynchronously(Achilles.getInstance(), new InitializeRunnable());
        Bukkit.getScheduler().runTaskAsynchronously(Achilles.getInstance(), new ReconnectRunnable());
        Bukkit.getScheduler().runTaskTimer(Achilles.getInstance(), new SubscribeRunnable(), 2L, 2L);
    }

    @Override
    public void sendMessage(Message message) {
        Bukkit.getScheduler().runTaskAsynchronously(Achilles.getInstance(), () -> {
            try (Jedis jedis = pool.getResource()) {
                jedis.publish("achilles-messenger", message.getType().ordinal() + ";" + message.getData());
            }
        });
    }

    @Override
    public void shutdown() {
        if (listener != null) listener.unsubscribe();
        if (pool != null) pool.close();
    }

    private class InitializeRunnable implements Runnable {
        @Override
        public void run() {
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(12);
            if (NEED_AUTH) {
                pool = new JedisPool(config, HOST, PORT, 2000, PASSWORD);
            } else {
                pool = new JedisPool(config, HOST, PORT);
            }
        }
    }

    private class SubscribeRunnable implements Runnable {
        @Override
        public void run() {
            if (listener == null || !listener.isSubscribed()) {
                return;
            }

            listener.subscribe("achilles-messenger");
        }
    }

    private class ReconnectRunnable implements Runnable {
        private boolean broken = false;

        @Override
        public void run() {
            if (broken) {
                broken = false;
            }

            try (Jedis jedis = pool.getResource()) {
                try {
                    Achilles.getInstance().getLogger().info("[Messenger-Redis] Successfully subscribed to Redis!");
                    jedis.subscribe(listener = new RedisListener(), "achilles-messenger");
                } catch (Exception e) {
                    e.printStackTrace();
                    listener.unsubscribe();
                    listener = null;
                    broken = true;
                }
            }

            if (broken) {
                Achilles.getInstance().getLogger().info("[Messenger-Redis] Trying to resubscribe with Redis...");
                Bukkit.getScheduler().runTaskLaterAsynchronously(Achilles.getInstance(), this, 20L);
            }
        }
    }

    private class RedisListener extends JedisPubSub {
        @Override
        public void onMessage(String channel, String message) {
            MessageType type = MessageType.fromId(Integer.parseInt(message.split(";", 2)[0]));

            if (type == null) {
                Achilles.getInstance().getLogger().warning("[Messenger-Redis] Got message with a bad type, ignoring...");
                return;
            }

            String data = message.split(";", 2)[1];
            handleIncoming(new Message(type, data));
        }
    }
}
