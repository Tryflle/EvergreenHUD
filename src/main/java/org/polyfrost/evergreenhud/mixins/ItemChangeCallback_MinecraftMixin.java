package org.polyfrost.evergreenhud.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import org.objectweb.asm.Opcodes;
import org.polyfrost.evergreenhud.SelectedItemChangedEvent;
import org.polyfrost.oneconfig.api.event.v1.EventManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class ItemChangeCallback_MinecraftMixin {
    @Shadow public EntityPlayerSP thePlayer;

    @Inject(method =
            //#if MC > 1.8.9
            //$$ "processKeyBinds"
            //#else
            "runTick"
            //#endif
            , at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/InventoryPlayer;currentItem:I", shift = At.Shift.AFTER, opcode = Opcodes.PUTFIELD))
    private void selectedItemChangeCallback(CallbackInfo ci) {
        EventManager.INSTANCE.post(new SelectedItemChangedEvent(this.thePlayer.getHeldItem(
                //#if MC > 1.8.9
                //$$ net.minecraft.util.EnumHand.MAIN_HAND
                //#endif
        )));
    }
}
