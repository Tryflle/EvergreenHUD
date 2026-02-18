package org.polyfrost.evergreenhud.mixins.client;

import net.minecraft.world.food.FoodData;
import org.polyfrost.evergreenhud.client.SaturationChangedEvent;
import org.polyfrost.oneconfig.api.event.v1.EventManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FoodData.class)
public class Mixin_FoodStats_SaturationChangedEvent {
    @Shadow private float saturationLevel;

    @Inject(method = "add(IF)V", at = @At("RETURN"))
    private void onAddStats(CallbackInfo ci) {
        EventManager.INSTANCE.post(new SaturationChangedEvent(this.saturationLevel));
    }

    @Inject(method = "readAdditionalSaveData", at = @At("RETURN"))
    private void onReadNBT(CallbackInfo ci) {
        EventManager.INSTANCE.post(new SaturationChangedEvent(this.saturationLevel));
    }

    @Inject(method = "setSaturation", at = @At("RETURN"))
    private void onSet(CallbackInfo ci) {
        EventManager.INSTANCE.post(new SaturationChangedEvent(this.saturationLevel));
    }
}
