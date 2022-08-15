package me.djtheredstoner.peerchat.chatdatagrams.handlers;

import me.djtheredstoner.peerchat.PeerChatMod;
import me.djtheredstoner.peerchat.chatdatagrams.ChatDatagram;

// not a "real" handler - don't register with ServiceLoader
public class DummyHandler implements ChatDatagramHandler {

    public static final DummyHandler INSTANCE = new DummyHandler();

    @Override
    public String getTitle() {
        return "Dummy";
    }

    @Override
    public void apply(ChatDatagram datagram) {
        PeerChatMod.LOGGER.error("Encountered unknown datagram title {}", datagram.title());
    }
}
