package org.polyfrost.evergreenhud.mixins;

import net.minecraft.network.play.client.C03PacketPlayer;
import org.polyfrost.evergreenhud.PlayerPosEvent;
import org.polyfrost.oneconfig.api.event.v1.EventManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(C03PacketPlayer.C05PacketPlayerLook.class)
public class PositionCallback_C05PacketPlayerLookMixin {
    @Inject(method = "<init>(FFZ)V", at = @At("RETURN"))
    private void evergreen$readPosition(float yaw, float pitch, boolean onGround, CallbackInfo ci) {
        EventManager.INSTANCE.post(new PlayerPosEvent(0.0, 0.0, 0.0, yaw, pitch));
    }
}
