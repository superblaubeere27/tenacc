package net.ccbluex.tenacc.client.mixin;

import net.ccbluex.tenacc.ClientTestManager;
import net.ccbluex.tenacc.impl.common.TickEvent;
import net.ccbluex.tenacc.input.InputManager;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {
	@Inject(at = @At("HEAD"), method = "run")
	private void run(CallbackInfo info) {
		ClientTestManager.INSTANCE.init();
	}

	@Inject(at = @At("HEAD"), method = "tick")
	private void tick(CallbackInfo info) {
		InputManager.INSTANCE.tickInput();

		ClientTestManager.INSTANCE.getSequenceManager().onEvent(new TickEvent());
	}
}