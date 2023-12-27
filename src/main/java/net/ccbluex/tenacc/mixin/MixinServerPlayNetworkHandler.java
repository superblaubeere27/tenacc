package net.ccbluex.tenacc.mixin;

import net.ccbluex.tenacc.api.runner.TestScheduleRequest;
import net.ccbluex.tenacc.features.templates.MirrorType;
import net.ccbluex.tenacc.features.templates.RotationType;
import net.ccbluex.tenacc.impl.TestIdentifier;
import net.ccbluex.tenacc.impl.server.ClientIntegrationTestServerExtensionsKt;
import net.ccbluex.tenacc.utils.ChatKt;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class MixinServerPlayNetworkHandler {
    @Shadow public ServerPlayerEntity player;

    @Inject(at = @At("HEAD"), method = "onChatMessage", cancellable = true)
    public void onTestMessage(ChatMessageC2SPacket message, CallbackInfo ci) {
        var split = message.chatMessage().split(" ");

        if (split.length >= 3 && split[0].equals("#runtest")) {
            ci.cancel();

            var mirror = split.length >= 4 ? new MirrorType[] {MirrorType.values()[Integer.parseInt(split[3])]} : null;
            var rotationType = split.length >= 5 ? new RotationType[] {RotationType.values()[Integer.parseInt(split[4])]} : null;

            var id = new TestIdentifier(split[1], split[2]);
            var testManager = ClientIntegrationTestServerExtensionsKt.getTestManager(this.player.server);

            var test = testManager.findTestById(id);

            if (test == null) {
                ChatKt.chat(this.player, "Unknown test :/");

                return;
            }

            ChatKt.chat(this.player, "Running test...");

            testManager.enqueueTests(new TestScheduleRequest(id, rotationType, mirror));
        }
    }
}
