package org.polyfrost.evergreenhud.mixins;

import net.minecraft.network.play.client.C03PacketPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(C03PacketPlayer.C05PacketPlayerLook.class)
public class C05PacketPlayerLookMixin {
    @Inject(method = "<init>(FFZ)V", at = @At("RETURN"))
    private void evergreen$readPosition(float yaw, float pitch, boolean onGround, CallbackInfo ci) {
        LookVecHud.update(yaw, pitch);
        Facing.update(yaw);
    }
}
