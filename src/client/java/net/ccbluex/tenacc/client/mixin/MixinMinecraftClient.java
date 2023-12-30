package net.ccbluex.tenacc.client.mixin;

import net.ccbluex.tenacc.ClientTestManager;
import net.ccbluex.tenacc.impl.common.TickEvent;
import net.ccbluex.tenacc.input.InputManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.server.integrated.IntegratedServerLoader;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient {
	@Shadow public abstract IntegratedServerLoader createIntegratedServerLoader();

	@Shadow public abstract void setScreen(@Nullable Screen screen);

	@Inject(at = @At("HEAD"), method = "run")
	private void run(CallbackInfo info) {
		ClientTestManager.INSTANCE.init();
	}

	@Inject(at = @At("HEAD"), method = "tick")
	private void tick(CallbackInfo info) {
		InputManager.INSTANCE.tickInput();

		ClientTestManager.INSTANCE.getSequenceManager().onEvent(new TickEvent());
		ClientTestManager.INSTANCE.tick();
	}

	@Inject(at = @At("RETURN"), method="<init>")
	private void injectStartupWorldLoading(RunArgs args, CallbackInfo ci) {
//		if (!ClientTestManager.INSTANCE.getTestProvider().getStartIntoTestWorldOnStartup())
//			return;
//
//		this.createIntegratedServerLoader().start("tenacc_test_world", () -> this.setScreen(new TitleScreen()));
	}
}