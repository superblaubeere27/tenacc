package net.ccbluex.tenacc.client.mixin;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Screen.class)
public abstract class MixinScreen {
    @Shadow protected abstract <T extends Element & Drawable> T addDrawableChild(T drawableElement);

    @Shadow public int height;
    @Shadow public int width;
}
