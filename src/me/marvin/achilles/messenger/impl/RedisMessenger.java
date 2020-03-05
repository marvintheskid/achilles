package me.marvin.achilles.messenger.impl;

import com.google.gson.JsonObject;
import me.marvin.achilles.Achilles;
import me.marvin.achilles.Variables;
import me.marvin.achilles.messenger.Message;
import me.marvin.achilles.messenger.MessageType;
import me.marvin.achilles.messenger.Messenger;
import org.bukkit.Bukkit;
import redis.clients.jedis.*;

import static me.marvin.achilles.Variables.Messenger.Redis.*;

/*
 * Copyright (c) 2019-Present marvintheskid (Kovács Márton)
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

public class RedisMessenger extends Messenger {
    private JedisPubSub listener;
    private JedisPool pool;

    @Override
    public void initialize() {
        Bukkit.getScheduler().runTaskAsynchronously(Achilles.getInstance(), new InitializeRunnable());
        Bukkit.getScheduler().runTaskTimer(Achilles.getInstance(), new SubscribeRunnable(), 2L, 2L);
    }

    @Override
    public void sendMessage(Message message) {
        Bukkit.getScheduler().runTaskAsynchronously(Achilles.getInstance(), () -> {
            try (Jedis jedis = pool.getResource()) {
                JsonObject object = new JsonObject();
                object.addProperty("type", message.getType().ordinal());
                object.addProperty("origin", Variables.Database.SERVER_NAME);
                object.add("data", message.getData());

                jedis.publish("achilles-messenger", object.toString());
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

            Bukkit.getScheduler().runTaskAsynchronously(Achilles.getInstance(), new ReconnectRunnable());
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
            JsonObject object = PARSER.parse(message).getAsJsonObject();

            if (object == null) {
                Achilles.getInstance().getLogger().warning("[Messenger-Redis] Got message with a bad type, ignoring...");
                return;
            }

            if (!object.has("type") || !object.has("origin") || !object.has("data")) {
                Achilles.getInstance().getLogger().warning("[Messenger-Redis] Got a malformed message, ignoring...");
                return;
            }

            if (object.get("origin").getAsString().equals(Variables.Database.SERVER_NAME)) {
                return;
            }

            MessageType type = MessageType.fromId(object.get("type").getAsInt());
            if (type == null) {
                Achilles.getInstance().getLogger().warning("[Messenger-Redis] Got message with a bad type, ignoring...");
                return;
            }

            JsonObject data = object.get("data").getAsJsonObject();
            handleIncoming(new Message(type, data));
        }
    }
}
