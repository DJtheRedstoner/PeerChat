package me.djtheredstoner.peerchat.mixin;

import net.minecraft.util.StringHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(StringHelper.class)
public class StringHelperMixin_IncreaseChatMaxLength {

    @ModifyConstant(method = "truncateChat", constant = @Constant(intValue = 256))
    private static int peerchat$increaseChatMaxLength(int constant) {
        return 10000;
    }

}
