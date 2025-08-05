package org.polyfrost.evergreenhud.mixins;

import net.minecraft.client.renderer.RenderGlobal;
import org.polyfrost.evergreenhud.client.EntityCounterEvent;
import org.polyfrost.oneconfig.api.event.v1.EventManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderGlobal.class)
public abstract class Mixin_RenderGlobal_EntityCounterEvent {

    @Shadow private int countEntitiesRendered;

    @Shadow private int countEntitiesTotal;

    @Inject(method = "renderEntities", at = @At("TAIL"))
    private void evergreen$readEntityRenderCount(CallbackInfo ci) {
        EntityCounterEvent.setRendered(this.countEntitiesRendered);
        EntityCounterEvent.setTotal(this.countEntitiesTotal);
        EventManager.INSTANCE.post(EntityCounterEvent.INSTANCE);
    }

}
