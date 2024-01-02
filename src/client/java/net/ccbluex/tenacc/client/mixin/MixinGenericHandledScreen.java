package net.ccbluex.tenacc.client.mixin;

import net.ccbluex.tenacc.ui.SaveInventoryContents;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.ccbluex.tenacc.ui.TenaccUIConstantsKt.*;
import static net.ccbluex.tenacc.ui.TenaccUIConstantsKt.BUTTON_HEIGHT;

@Mixin(HandledScreen.class)
public abstract class MixinGenericHandledScreen<T extends ScreenHandler> extends MixinScreen {

    @Shadow @Final protected T handler;

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

        if (!((Object) this instanceof GenericContainerScreen))
            return;

        var saveChestContents = ButtonWidget
                .builder(Text.literal("Save chest contents"), (w) -> {
                    SaveInventoryContents.INSTANCE.saveInventoryContents(((GenericContainerScreenHandler) handler).getInventory(), "chest contents");
                })
                .dimensions(
                        this.width - BUTTON_WIDTH - BUTTON_PADDING,
                        this.height - (BUTTON_HEIGHT - BUTTON_PADDING) * 2,
                        BUTTON_WIDTH,
                        BUTTON_HEIGHT
                )
                .build();

        this.addDrawableChild(savePlayerInventoryButton);
        this.addDrawableChild(saveChestContents);
    }

}
