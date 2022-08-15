package me.djtheredstoner.peerchat.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.network.message.SignedMessage;

public interface ChatReceivedCallback {

    Event<ChatReceivedCallback> EVENT = EventFactory.createArrayBacked(ChatReceivedCallback.class, (callbacks) -> (message) -> {
        for (ChatReceivedCallback callback : callbacks) {
            callback.chatReceived(message);
        }
    });

    void chatReceived(SignedMessage message);

}
