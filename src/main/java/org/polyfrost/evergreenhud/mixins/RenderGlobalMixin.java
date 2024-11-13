package org.polyfrost.evergreenhud.mixins;

import net.minecraft.client.renderer.RenderGlobal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderGlobal.class)
public abstract class RenderGlobalMixin {
    @Shadow private int countEntitiesRendered;

    @Shadow private int countEntitiesTotal;

    @Inject(method = "renderEntities", at = @At("TAIL"))
    private void evergreen$readEntityRenderCount(CallbackInfo ci) {
        ECounter.changed(countEntitiesRendered, countEntitiesTotal);
    }
}
