package me.djtheredstoner.peerchat.chatdatagrams.handlers;

import com.google.auto.service.AutoService;
import me.djtheredstoner.peerchat.Connection;
import me.djtheredstoner.peerchat.PeerChatMod;
import me.djtheredstoner.peerchat.chatdatagrams.ChatDatagram;
import me.djtheredstoner.peerchat.chatdatagrams.ChatDatagrams;
import net.minecraft.client.MinecraftClient;

@AutoService(ChatDatagramHandler.class)
public class HelloHandler implements ChatDatagramHandler {

    public HelloHandler() {}

    @Override
    public String getTitle() {
        return "Hello";
    }

    @Override
    public void apply(ChatDatagram datagram) {
        String name = datagram.parts()[1];
        if (name.equals(MinecraftClient.getInstance().getSession().getUsername())) {
            String id = datagram.parts()[2];

            var player = MinecraftClient.getInstance().player;
            String senderName = player.networkHandler.getPlayerListEntry(datagram.sender()).getProfile().getName();

            var connection = new Connection(senderName, datagram.sender(), id);
            PeerChatMod.getInstance().getConnections().put(id, connection);

            ChatDatagrams.send(ChatDatagrams.writeHi(senderName, id, "1.0.0"));

            connection.setupIce(false);
        }
    }
}
