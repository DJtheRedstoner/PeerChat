package me.djtheredstoner.peerchat.chatdatagrams;

import me.djtheredstoner.peerchat.chatdatagrams.handlers.ChatDatagramHandler;
import me.djtheredstoner.peerchat.chatdatagrams.handlers.DummyHandler;
import net.minecraft.client.MinecraftClient;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

public class ChatDatagrams {

    private static final Map<String, ChatDatagramHandler> HANDLER_MAP = new HashMap<>();

    static {
        for (ChatDatagramHandler handler : ServiceLoader.load(ChatDatagramHandler.class)) {
            HANDLER_MAP.put(handler.getTitle(), handler);
        }
    }

    public static ChatDatagramHandler getHandler(String title) {
        return HANDLER_MAP.getOrDefault(title, DummyHandler.INSTANCE);
    }

    public static String writeHello(String name, String id, String version) {
        return writeStrings("Hello", name, id, version);
    }

    public static String writeHi(String name, String id, String version) {
        return writeStrings("Hi", name, id, version);
    }

    public static String writeCandidates(String id, String uFrag, String password, String candidates) {
        return writeStrings("Candidates", id, uFrag, password, candidates);
    }

    public static String writeStrings(Object... objects) {
        String[] strings = new String[objects.length];
        for (int i = 0; i < objects.length; i++) {
            strings[i] = objects[i].toString();
        }
        return String.join(" ", strings);
    }

    public static void send(String data) {
        MinecraftClient.getInstance().player.sendChatMessage("$PeerChat$" + data + "$", null);
    }
}
