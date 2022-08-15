package me.djtheredstoner.peerchat;

import me.djtheredstoner.peerchat.chatdatagrams.ChatDatagrams;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.ice4j.Transport;
import org.ice4j.TransportAddress;
import org.ice4j.ice.*;
import org.ice4j.ice.harvest.StunCandidateHarvester;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class
Connection {

    private final String remoteName;
    private final UUID remoteUuid;
    private final String id;

    private Agent agent;
    private IceMediaStream stream;
    private Component component;

    public Connection(String remoteName, UUID remoteUuid, String id) {
        this.remoteName = remoteName;
        this.remoteUuid = remoteUuid;
        this.id = id;
    }

    public void setupIce(boolean initiating) {
        agent = new Agent();

        agent.addCandidateHarvester(new StunCandidateHarvester(
            new TransportAddress("stun.l.google.com", 19302, Transport.UDP)));

        stream = agent.createMediaStream("data");
        try {
            component = agent.createComponent(stream, KeepAliveStrategy.SELECTED_ONLY, true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        agent.setControlling(initiating);
        agent.setNominationStrategy(NominationStrategy.NOMINATE_HIGHEST_PRIO);

        agent.addStateChangeListener(new Listener());

        List<String> candidates = new ArrayList<>();

        for (LocalCandidate candidate : component.getLocalCandidates()) {
            List<String> parts = new ArrayList<>();
            parts.add(candidate.getTransportAddress().getHostAddress());
            parts.add(candidate.getTransportAddress().getPort() + "");
            parts.add(candidate.getFoundation());
            parts.add(candidate.getPriority() + "");
            parts.add(candidate.getType().toString());
            candidates.add(String.join("|", parts));
        }

        ChatDatagrams.send(ChatDatagrams.writeCandidates(id, agent.getLocalUfrag(), agent.getLocalPassword(), String.join("%", candidates)));
    }

    public void startIce(String uFrag, String password, String candidates) {
        PeerChatMod.getInstance().setLastConnection(this);

        stream.setRemoteUfrag(uFrag);
        stream.setRemotePassword(password);

        for (String candidate : candidates.split("%")) {
            String[] parts = candidate.split("\\|");
            component.addRemoteCandidate(new RemoteCandidate(
                new TransportAddress(
                    parts[0],
                    Integer.parseInt(parts[1]),
                    Transport.UDP
                ),
                component,
                CandidateType.parse(parts[4]),
                parts[2],
                Long.parseLong(parts[3]),
                null
            ));
        }

        agent.startConnectivityEstablishment();
    }

    public void send(String message) {
        byte[] data = message.getBytes(StandardCharsets.UTF_8);
        DatagramPacket packet = new DatagramPacket(data, data.length);
        try {
            component.getSocket().send(packet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private class Listener implements PropertyChangeListener {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getNewValue() == IceProcessingState.COMPLETED) {
                //if (agent.isControlling()) {
                //    byte[] data = new byte[]{69, 42, 0};
                //    DatagramPacket packet = new DatagramPacket(data, data.length);
                //    try {
                //        component.getSocket().send(packet);
                //    } catch (IOException e) {
                //        throw new RuntimeException(e);
                //    }
                //    System.out.println("sent packet");
                //} else {
                //    new Thread(() -> {
                //        byte[] buf = new byte[3];
                //        DatagramPacket packet = new DatagramPacket(buf, 0);
                //        try {
                //            component.getSocket().receive(packet);
                //        } catch (IOException e) {
                //            throw new RuntimeException(e);
                //        }
                //        System.out.println(Arrays.toString(buf));
                //    }).start();
                //}
                new Thread(() -> {
                    while (true) {
                        byte[] buf = new byte[1024];
                        DatagramPacket packet = new DatagramPacket(buf, 0);
                        try {
                            component.getSocket().receive(packet);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        MinecraftClient.getInstance().send(() -> {
                            MinecraftClient.getInstance().player.sendMessage(Text.literal("[PeerChat] " + remoteName + ": " + new String(buf).trim()));
                        });
                    }
                }).start();
            }
        }
    }

}
