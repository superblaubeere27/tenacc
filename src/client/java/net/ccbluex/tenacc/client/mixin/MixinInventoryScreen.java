package net.ccbluex.tenacc.client.mixin;

import net.ccbluex.tenacc.ui.SaveInventoryContents;
import net.ccbluex.tenacc.ui.TenaccUIConstantsKt;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.ccbluex.tenacc.ui.TenaccUIConstantsKt.*;

@Mixin(InventoryScreen.class)
public abstract class MixinInventoryScreen extends MixinScreen {

    @Inject(method = "init", at = @At("RETURN"))
    private void injectInit(CallbackInfo ci) {

        var savePlayerInventoryButton = ButtonWidget
                .builder(Text.literal("Save player inventory"), (w) -> SaveInventoryContents.INSTANCE.savePlayerInventoryContents())
                .dimensions(
                        this.width - BUTTON_WIDTH - BUTTON_PADDING,
                        this.height - BUTTON_HEIGHT - BUTTON_PADDING,
                        BUTTON_WIDTH,
                        BUTTON_HEIGHT
                )
                .build();

        this.addDrawableChild(savePlayerInventoryButton);
    }

}
