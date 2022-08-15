package me.djtheredstoner.peerchat.mixin;

import net.minecraft.network.message.DecoratedContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(DecoratedContents.class)
public class DecoratedContentsMixin_IncreaseMaxChatLength {

    @ModifyConstant(method = { "read", "write" }, constant = @Constant(intValue = 256))
    private static int peerchat$increaseMaxChatLength(int constant) {
        return 10000;
    }

}
