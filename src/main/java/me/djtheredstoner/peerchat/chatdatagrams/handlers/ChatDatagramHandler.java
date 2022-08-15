package me.djtheredstoner.peerchat.chatdatagrams.handlers;

import me.djtheredstoner.peerchat.chatdatagrams.ChatDatagram;

public interface ChatDatagramHandler {

    String getTitle();

    void apply(ChatDatagram datagram);

}
