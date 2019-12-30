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

import static me.marvin.achilles.Language.Ban.PUNISHMENT_MESSAGE;
import static me.marvin.achilles.Language.Ban.SILENT;

@Getter
public abstract class Messenger {
    protected static final JsonParser PARSER = new JsonParser();
    private List<Consumer<Message>> consumers;

    public Messenger() {
        Achilles.getInstance().getLogger().info("[Messenger] Initializing "  + this.getClass().getSimpleName() + "...");
        this.consumers = new ArrayList<>();
        this.initialize();

        //TODO consumerek
        this.consumers.add((message -> {
            if (message.getType() == MessageType.MESSAGE) {
            }
        }));

        this.consumers.add((message -> {
            if (message.getType() == MessageType.KICK_REQUEST) {
                Player p = Bukkit.getPlayer(UUID.fromString(message.getData().get("uuid").getAsString()));
                if (p != null) p.kickPlayer(message.getData().get("message").getAsString());
            }
        }));
    }

    public abstract void initialize();
    public abstract void shutdown();
    public abstract void sendMessage(Message message);

    protected final void handleIncoming(Message message) {
        consumers.forEach(consumer -> consumer.accept(message));
    }
}
