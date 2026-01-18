package org.polyfrost.evergreenhud.mixins.client;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.polyfrost.evergreenhud.client.hooks.SmuggledHudDrawContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class Mixin_InGameHud_SmuggleDrawContext {

    @Inject(
        method = "render",
        at = @At("HEAD")
    )
    private void evergreenhud$smuggleDrawContext(
            GuiGraphics graphics,
            DeltaTracker deltaTracker,
            CallbackInfo ci
    ) {
        SmuggledHudDrawContext.setSmuggledHudDrawContext(graphics);
    }

}
