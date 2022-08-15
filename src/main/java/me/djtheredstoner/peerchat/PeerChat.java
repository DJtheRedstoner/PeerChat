package me.djtheredstoner.peerchat;

import org.ice4j.Transport;
import org.ice4j.TransportAddress;
import org.ice4j.ice.*;
import org.ice4j.ice.harvest.HostCandidateHarvester;
import org.ice4j.ice.harvest.StunCandidateHarvester;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.util.Arrays;

public class PeerChat {

    private static Agent createAgent() throws Exception {
        var agent = new Agent();

        agent.addCandidateHarvester(new StunCandidateHarvester(
            new TransportAddress("us.stun.essential.gg", 3478, Transport.UDP)));

        var stream = agent.createMediaStream("data");
        agent.createComponent(stream, KeepAliveStrategy.SELECTED_ONLY, true);

        return agent;
    }

    public static void main(String[] args) throws Exception {
        var localAgent = createAgent();
        var remoteAgent = createAgent();

        localAgent.setControlling(true);
        localAgent.setNominationStrategy(NominationStrategy.NOMINATE_HIGHEST_PRIO);
        remoteAgent.setControlling(false);

        localAgent.addStateChangeListener(new IceProcessingListenerLocal());
        remoteAgent.addStateChangeListener(new IceProcessingListenerRemote());

        for (IceMediaStream stream : localAgent.getStreams()) {
            stream.setRemoteUfrag(remoteAgent.getLocalUfrag());
            stream.setRemotePassword(remoteAgent.getLocalPassword());
        }

        for (IceMediaStream stream : remoteAgent.getStreams()) {
            stream.setRemoteUfrag(localAgent.getLocalUfrag());
            stream.setRemotePassword(localAgent.getLocalPassword());
        }

        transferCandidates(localAgent, remoteAgent);
        transferCandidates(remoteAgent, localAgent);

        localAgent.startConnectivityEstablishment();
        remoteAgent.startConnectivityEstablishment();



        //System.exit(0);
    }

    private static void transferCandidates(Agent localAgent, Agent remoteAgent) {
        for (IceMediaStream stream : localAgent.getStreams()) {
            var remoteStream = remoteAgent.getStream(stream.getName());
            for (Component component : stream.getComponents()) {
                var remoteComponent = remoteStream.getComponent(component.getComponentID());
                component.setDefaultRemoteCandidate(remoteComponent.getDefaultCandidate());
                for (Candidate<?> remoteCandidate : remoteComponent.getLocalCandidates()) {
                    component.addRemoteCandidate(new RemoteCandidate(
                        remoteCandidate.getTransportAddress(),
                        component,
                        remoteCandidate.getType(),
                        remoteCandidate.getFoundation(),
                        remoteCandidate.getPriority(),
                        null
                    ));
                }
            }
        }
    }

    private static class IceProcessingListenerLocal implements PropertyChangeListener {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            var state = evt.getNewValue();

            if (state == IceProcessingState.COMPLETED) {
                System.out.println("Local Success!");
                var agent = (Agent) evt.getSource();

                System.out.println(agent.getStream("data").getComponent(1).getSelectedPair().getRemoteCandidate().getTransportAddress());

                var data = new byte[]{69, 42, 0};
                var packet = new DatagramPacket(data, 3);
                try {
                    agent.getStream("data").getComponent(1).getSocket().send(packet);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private static class IceProcessingListenerRemote implements PropertyChangeListener {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            var state = evt.getNewValue();

            if (state == IceProcessingState.COMPLETED) {
                System.out.println("Remote Success!");
                var agent = (Agent) evt.getSource();

                new Thread(() -> {
                    byte[] buf = new byte[3];
                    var packet = new DatagramPacket(buf, 0);
                    try {
                        agent.getStream("data").getComponent(1).getSocket().receive(packet);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    System.out.println(Arrays.toString(buf));
                    System.exit(0);
                }).start();
            }
        }
    }

}
