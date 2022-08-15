package me.djtheredstoner.peerchat.mixin;

import me.djtheredstoner.peerchat.event.ChatReceivedCallback;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.network.message.MessageHandler;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.time.Instant;

@Mixin(MessageHandler.class)
public class MessageHandlerMixin_ChatEvent {

    @Inject(method = "processChatMessageInternal", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/message/MessageHandler;addToChatLog(Lnet/minecraft/network/message/SignedMessage;Lnet/minecraft/network/message/MessageType$Parameters;Lnet/minecraft/client/network/PlayerListEntry;Lnet/minecraft/client/network/message/MessageTrustStatus;)V"))
    private void peerchat$fireChatEvent(
        MessageType.Parameters params,
        SignedMessage message,
        Text decorated,
        @Nullable PlayerListEntry senderEntry,
        boolean onlyShowSecureChat,
        Instant receptionTimestamp,
        CallbackInfoReturnable<Boolean> ci
    ) {
        ChatReceivedCallback.EVENT.invoker().chatReceived(message);
    }

}
