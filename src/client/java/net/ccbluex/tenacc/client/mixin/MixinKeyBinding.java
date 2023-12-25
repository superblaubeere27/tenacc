package net.ccbluex.tenacc.client.mixin;

import net.ccbluex.tenacc.input.InputManager;
import net.ccbluex.tenacc.input.InputMappingKt;
import net.ccbluex.tenacc.client.interfaces.IMixinKeyBinding;
import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(KeyBinding.class)
class MixinKeyBinding implements IMixinKeyBinding {
    @Shadow private int timesPressed;

    @Inject(method = "isPressed", at = @At("HEAD"), cancellable = true)
    public void injectKeyBindings(CallbackInfoReturnable<Boolean> ci) {
        var inputKey = InputMappingKt.getKEYBINDING_TO_INPUT_KEY().get(this);

        if (inputKey == null) {
            return;
        }

        if (InputManager.INSTANCE.isInputPressed(inputKey)) {
            ci.setReturnValue(true);

            ci.cancel();
        }
    }

    @Override
    public void press(int nTimes) {
        this.timesPressed += nTimes;
    }
}