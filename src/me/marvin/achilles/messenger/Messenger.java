package me.marvin.achilles.messenger;

import com.google.gson.JsonParser;
import lombok.Getter;
import me.marvin.achilles.Achilles;
import me.marvin.achilles.Variables;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static me.marvin.achilles.utils.etc.StringUtils.colorize;

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

@Getter
public abstract class Messenger {
    protected static final JsonParser PARSER = new JsonParser();
    private List<Consumer<Message>> consumers;

    public Messenger() {
        Achilles.getInstance().getLogger().info("[Messenger] Initializing "  + this.getClass().getSimpleName() + "...");
        this.consumers = new ArrayList<>();
        this.initialize();

        this.consumers.add((message -> {
            if (message.getType() == MessageType.MESSAGE) {
                Bukkit.broadcast(colorize(message.getData().get("message").getAsString()), "achilles.alerts");
            }
        }));

        this.consumers.add((message -> {
            if (message.getType() == MessageType.DATA_UPDATE) {
                Player p = Bukkit.getPlayer(UUID.fromString(message.getData().get("uuid").getAsString()));
                if (p != null) Achilles.getProfileHandler().getOrCreateProfile(p.getUniqueId()).load(true);
            }
        }));

        this.consumers.add((message -> {
            if (message.getType() == MessageType.KICK_REQUEST) {
                Player p = Bukkit.getPlayer(UUID.fromString(message.getData().get("uuid").getAsString()));
                if (p != null) p.kickPlayer(colorize(message.getData().get("message").getAsString()));
            }
        }));
    }

    public abstract void initialize();
    public abstract void shutdown();
    public abstract void sendMessage(Message message);

    protected final void handleIncoming(Message message) {
        Bukkit.getScheduler().runTask(Achilles.getInstance(), () ->
            consumers.forEach(consumer -> consumer.accept(message)));
    }
}
