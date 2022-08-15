package me.djtheredstoner.peerchat.chatdatagrams.handlers;

import com.google.auto.service.AutoService;
import me.djtheredstoner.peerchat.PeerChatMod;
import me.djtheredstoner.peerchat.chatdatagrams.ChatDatagram;

@AutoService(ChatDatagramHandler.class)
public class CandidatesHandler implements ChatDatagramHandler {

    @Override
    public String getTitle() {
        return "Candidates";
    }

    @Override
    public void apply(ChatDatagram datagram) {
        String id = datagram.parts()[1];
        String uFrag = datagram.parts()[2];
        String password = datagram.parts()[3];
        String candidates = datagram.parts()[4];

        PeerChatMod.getInstance().getConnection(id).startIce(uFrag, password, candidates);
    }
}
