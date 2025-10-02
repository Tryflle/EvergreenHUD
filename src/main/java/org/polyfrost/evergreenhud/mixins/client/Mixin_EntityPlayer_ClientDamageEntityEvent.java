package org.polyfrost.evergreenhud.mixins.client;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.polyfrost.evergreenhud.client.ClientDamageEntityEvent;
import org.polyfrost.oneconfig.api.event.v1.EventManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayer.class)
public abstract class Mixin_EntityPlayer_ClientDamageEntityEvent {
    @Inject(method = "attackTargetEntityWithCurrentItem", at = @At("HEAD"))
    private void onAttackTargetEntityWithCurrentItem(Entity target, CallbackInfo ci) {
        EventManager.INSTANCE.post(new ClientDamageEntityEvent((EntityPlayer) (Object) this, target));
    }
}
