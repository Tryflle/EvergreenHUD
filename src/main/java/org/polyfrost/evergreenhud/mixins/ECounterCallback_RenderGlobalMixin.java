package org.polyfrost.evergreenhud.mixins;

import net.minecraft.client.renderer.RenderGlobal;
import org.polyfrost.evergreenhud.ECounterEvent;
import org.polyfrost.oneconfig.api.event.v1.EventManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static org.polyfrost.evergreenhud.EvergreenHUDKt.getECounter;

@Mixin(RenderGlobal.class)
public abstract class ECounterCallback_RenderGlobalMixin {
    @Shadow private int countEntitiesRendered;

    @Shadow private int countEntitiesTotal;

    @Inject(method = "renderEntities", at = @At("TAIL"))
    private void evergreen$readEntityRenderCount(CallbackInfo ci) {
        ECounterEvent ev = getECounter();
        ev.setRendered(countEntitiesRendered);
        ev.setTotal(countEntitiesTotal);
        EventManager.INSTANCE.post(ev);
    }
}
