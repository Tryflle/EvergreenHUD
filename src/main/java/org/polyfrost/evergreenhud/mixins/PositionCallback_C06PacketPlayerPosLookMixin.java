package org.polyfrost.evergreenhud.mixins;

import net.minecraft.network.play.client.C03PacketPlayer;
import org.polyfrost.evergreenhud.PlayerPosEvent;
import org.polyfrost.oneconfig.api.event.v1.EventManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(C03PacketPlayer.C06PacketPlayerPosLook.class)
public class PositionCallback_C06PacketPlayerPosLookMixin {

    @Inject(method = "<init>(DDDFFZ)V", at = @At("RETURN"))
    private void evergreen$readPositionAndLook(double x, double y, double z, float yaw, float pitch, boolean onGround, CallbackInfo ci) {
        EventManager.INSTANCE.post(new PlayerPosEvent(x, y, z, yaw, pitch));
    }
}
