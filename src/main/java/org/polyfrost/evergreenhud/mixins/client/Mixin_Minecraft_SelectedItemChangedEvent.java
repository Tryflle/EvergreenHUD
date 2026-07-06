package org.polyfrost.evergreenhud.mixins.client;

import net.minecraft.client.Minecraft;
import org.polyfrost.evergreenhud.client.SelectedItemChangedEvent;
import org.polyfrost.oneconfig.api.event.v1.EventManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class Mixin_Minecraft_SelectedItemChangedEvent {
    @Inject(
            method = "handleKeybinds",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Inventory;setSelectedSlot(I)V",
                    shift = At.Shift.AFTER
            )
    )
    private void selectedItemChangeCallback(CallbackInfo ci) {
        EventManager.INSTANCE.post(new SelectedItemChangedEvent(Minecraft.getInstance().player.getMainHandItem()));
    }
}
