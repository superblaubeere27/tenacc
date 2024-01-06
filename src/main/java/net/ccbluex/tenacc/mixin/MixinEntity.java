package net.ccbluex.tenacc.mixin;

import net.ccbluex.tenacc.impl.server.ClientIntegrationTestServerExtensionsKt;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class MixinEntity {

    @Inject(method = "remove", at = @At("RETURN"))
    private void injectEntityRemapping(Entity.RemovalReason reason, CallbackInfo ci) {
//        // When the player dies, changes dimension, etc., it is removed from the world.
//        var entity = (Entity) (Object) this;
//
//        var testManager = ClientIntegrationTestServerExtensionsKt.getTestManager(entity.getServer());
//
//        switch (reason) {
//            case KILLED, DISCARDED -> {
//                if (testManager.getRunningTest().getPlayer() == entity) {
//                    testManager.getRunningTest().setPlayer()
//                }
//            }
//            else ->
//        }
    }

}
