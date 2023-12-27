package net.ccbluex.tenacc.mixin;

import net.ccbluex.tenacc.impl.common.TickEvent;
import net.ccbluex.tenacc.impl.server.ServerTestManager;
import net.ccbluex.tenacc.interfaces.IMixinMinecraftServer;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(MinecraftServer.class)
public class MixinMinecraftServer implements IMixinMinecraftServer {
	private ServerTestManager testManager;

	@Inject(at = @At("HEAD"), method = "loadWorld")
	private void init(CallbackInfo info) {
		this.testManager = new ServerTestManager((MinecraftServer) (Object) this);
	}

	@Inject(at = @At("HEAD"), method = "tick")
	private void tick(CallbackInfo info) {
		ServerTestManager mgr = this.getTestManager();

		mgr.getSequenceManager().onEvent(new TickEvent());

		mgr.tick((MinecraftServer) (Object) this);
	}

	@Override
	public @NotNull ServerTestManager getTestManager() {
		return Objects.requireNonNull(this.testManager, "Server not initialized yet");
	}
}