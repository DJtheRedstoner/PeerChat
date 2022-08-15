package me.djtheredstoner.peerchat;

import com.mojang.brigadier.CommandDispatcher;
import me.djtheredstoner.peerchat.chatdatagrams.ChatDatagram;
import me.djtheredstoner.peerchat.chatdatagrams.ChatDatagrams;
import me.djtheredstoner.peerchat.chatdatagrams.handlers.ChatDatagramHandler;
import me.djtheredstoner.peerchat.command.PeerChatCommand;
import me.djtheredstoner.peerchat.event.ChatReceivedCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.network.message.SignedMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

public class PeerChatMod {

    public static final Logger LOGGER = LogManager.getLogger("PeerChat");

    private static final Pattern CHAT_DATAGRAM = Pattern.compile("\\$PeerChat\\$(.+)\\$");

    private static PeerChatMod INSTANCE;

    private final Map<String, Connection> connectionMap = new HashMap<>();

    private Connection lastConnection;

    public static void init() {
        INSTANCE = new PeerChatMod();
    }

    public static PeerChatMod getInstance() {
        return INSTANCE;
    }

    private PeerChatMod() {
        ClientCommandRegistrationCallback.EVENT.register(this::registerCommands);
        ChatReceivedCallback.EVENT.register(this::chatReceived);
    }

    private void registerCommands(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        PeerChatCommand.register(dispatcher);
    }

    private void chatReceived(SignedMessage message) {
        var content = message.getSignedContent().plain();

        var matcher = CHAT_DATAGRAM.matcher(content);
        if (!matcher.find()) return;

        UUID sender = message.signedHeader().sender();
        if (sender.equals(MinecraftClient.getInstance().getSession().getUuidOrNull())) return;

        String data = matcher.group(1);
        String datagramTitle = data.split(" ", 2)[0];

        ChatDatagramHandler handler = ChatDatagrams.getHandler(datagramTitle);

        String[] parts = data.split(" ");
        var datagram = new ChatDatagram(message, data, datagramTitle, parts, sender);

        handler.apply(datagram);
    }

    public void start(AbstractClientPlayerEntity target) {
        var id = Util.generateId();

        var connection = new Connection(target.getGameProfile().getName(), target.getGameProfile().getId(), id);
        connectionMap.put(id, connection);

        ChatDatagrams.send(ChatDatagrams.writeHello(target.getGameProfile().getName(), id, "1.0.0"));
    }

    public Map<String, Connection> getConnections() {
        return connectionMap;
    }

    public Connection getConnection(String id) {
        return connectionMap.get(id);
    }

    public Connection getLastConnection() {
        return lastConnection;
    }

    public void setLastConnection(Connection connection) {
        lastConnection = connection;
    }
}
