package org.polyfrost.evergreenhud.mixins.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import org.polyfrost.evergreenhud.client.hooks.SmuggledHudDrawContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//#if MC >= 1.21.1
//$$ import net.minecraft.client.DeltaTracker;
//#endif

@Mixin(InGameHud.class)
public class Mixin_InGameHud_SmuggleDrawContext {

    @Inject(
        method = "render",
        at = @At("HEAD")
    )
    private void evergreenhud$smuggleDrawContext(
            DrawContext context,
            //#if MC >= 1.21.1
            //$$ DeltaTracker deltaTracker,
            //#else
            float tickDelta,
            //#endif
            CallbackInfo ci
    ) {
        SmuggledHudDrawContext.setSmuggledHudDrawContext(context);
    }

}
