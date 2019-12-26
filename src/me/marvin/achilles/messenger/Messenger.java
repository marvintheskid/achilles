package me.marvin.achilles.messenger;

import lombok.Getter;
import me.marvin.achilles.Achilles;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Getter
public abstract class Messenger {
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
