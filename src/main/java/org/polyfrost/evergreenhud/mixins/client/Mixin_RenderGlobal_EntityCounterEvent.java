package org.polyfrost.evergreenhud.mixins.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import org.polyfrost.evergreenhud.client.EntityCounterEvent;
import org.polyfrost.oneconfig.api.event.v1.EventManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//? if >= 1.21.10
import net.minecraft.client.renderer.state.LevelRenderState;

@Mixin(LevelRenderer.class)
public abstract class Mixin_RenderGlobal_EntityCounterEvent {

    @Shadow private ClientLevel level;

    //? if < 1.21.10 {
    /*@Shadow private int visibleEntityCount;*/
    //?} else
    @Shadow @Final private LevelRenderState levelRenderState;

    @Inject(
            //? if < 1.21.10 {
            /*method = "renderEntities",*/
            //?} else
            method = "extractVisibleEntities",
        at = @At("TAIL")
    )
    private void evergreen$readEntityRenderCount(CallbackInfo ci) {
        EntityCounterEvent.setRendered(
                //? if < 1.21.10 {
                /*this.visibleEntityCount*/
                //?} else
                this.levelRenderState.entityRenderStates.size()
        );
        EntityCounterEvent.setTotal(this.level.getEntityCount());
        EventManager.INSTANCE.post(EntityCounterEvent.INSTANCE);
    }
}
