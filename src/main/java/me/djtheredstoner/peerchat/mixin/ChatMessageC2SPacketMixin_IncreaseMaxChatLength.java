package me.djtheredstoner.peerchat.mixin;

import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ChatMessageC2SPacket.class)
public class ChatMessageC2SPacketMixin_IncreaseMaxChatLength {

    @ModifyConstant(method = { "write", "<init>(Lnet/minecraft/network/PacketByteBuf;)V" }, constant = @Constant(intValue = 256))
    private static int peerchat$increaseMaxChatLength(int constant) {
        return 10000;
    }

}
