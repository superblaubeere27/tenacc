package net.ccbluex.tenacc.client.mixin;

import net.ccbluex.tenacc.ClientTestManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(DeathScreen.class)
public class MixinDeathScreen {

    @Shadow @Final private List<ButtonWidget> buttons;

    @Inject(method = "tick", at = @At("RETURN"))
    private void injectAutoRespawn(CallbackInfo ci) {
        if (!ClientTestManager.INSTANCE.getTestProvider().getHeadlessMode())
            return;

        var button = this.buttons.get(0);

        if (button.active) {
            button.onPress();
        }
    }

}
