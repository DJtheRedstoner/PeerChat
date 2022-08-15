package me.djtheredstoner.peerchat.chatdatagrams.handlers;

import com.google.auto.service.AutoService;
import me.djtheredstoner.peerchat.PeerChatMod;
import me.djtheredstoner.peerchat.chatdatagrams.ChatDatagram;
import net.minecraft.client.MinecraftClient;

@AutoService(ChatDatagramHandler.class)
public class HiHandler implements ChatDatagramHandler {

    @Override
    public String getTitle() {
        return "Hi";
    }

    @Override
    public void apply(ChatDatagram datagram) {
        String name = datagram.parts()[1];
        if (name.equals(MinecraftClient.getInstance().getSession().getUsername())) {
            String id = datagram.parts()[2];

            PeerChatMod.getInstance().getConnection(id).setupIce(true);
        }
    }
}
