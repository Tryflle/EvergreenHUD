package org.polyfrost.evergreenhud.mixins;

import net.minecraft.util.FoodStats;
import org.polyfrost.evergreenhud.client.SaturationChangedEvent;
import org.polyfrost.oneconfig.api.event.v1.EventManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FoodStats.class)
public class Mixin_FoodStats_SaturationChangedEvent {

    @Shadow private float foodSaturationLevel;

    @Inject(method = "addStats(IF)V", at = @At("RETURN"))
    private void onAddStats(CallbackInfo ci) {
        EventManager.INSTANCE.post(new SaturationChangedEvent(foodSaturationLevel));
    }

    @Inject(method = "onUpdate", at = @At("RETURN"))
    private void onUpdate(CallbackInfo ci) {
        EventManager.INSTANCE.post(new SaturationChangedEvent(foodSaturationLevel));
    }

    @Inject(method = "readNBT", at = @At("RETURN"))
    private void onReadNBT(CallbackInfo ci) {
        EventManager.INSTANCE.post(new SaturationChangedEvent(foodSaturationLevel));
    }

    @Inject(method = "setFoodSaturationLevel", at = @At("RETURN"))
    private void onSet(CallbackInfo ci) {
        EventManager.INSTANCE.post(new SaturationChangedEvent(foodSaturationLevel));
    }

}
