package org.polyfrost.evergreenhud.mixins;

import net.minecraft.network.play.client.C03PacketPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(C03PacketPlayer.C06PacketPlayerPosLook.class)
public class C06PacketPlayerPosLookMixin {

    @Inject(method = "<init>(DDDFFZ)V", at = @At("RETURN"))
    private void evergreen$readPositionAndLook(double x, double y, double z, float yaw, float pitch, boolean onGround, CallbackInfo ci) {
        Coordinates.update(x, y, z);
        LookVecHud.update(yaw, pitch);
        Facing.update(yaw);
    }
}
