package me.djtheredstoner.peerchat.chatdatagrams;

import net.minecraft.network.message.SignedMessage;

import java.util.UUID;

public record ChatDatagram(SignedMessage message, String data, String title, String[] parts, UUID sender) {
}
