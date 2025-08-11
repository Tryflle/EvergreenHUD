package org.polyfrost.evergreenhud.mixins.client;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import org.objectweb.asm.Opcodes;
import org.polyfrost.evergreenhud.client.SelectedItemChangedEvent;
import org.polyfrost.oneconfig.api.event.v1.EventManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryPlayer.class)
public abstract class Mixin_InventoryPlayer_SelectedItemChangedEvent {

    @Shadow public abstract ItemStack getCurrentItem();

    @Inject(
            method = {
                    //#if MC >= 1.12.2
                    //$$ "setPickedItemStack", "pickItem",
                    //#else
                    "setCurrentItem",
                    //#endif
                    "changeCurrentItem", "copyInventory"
            },
            at = @At(
                    value = "FIELD",
                    opcode = Opcodes.PUTFIELD,
                    target = "Lnet/minecraft/entity/player/InventoryPlayer;currentItem:I",
                    shift = At.Shift.AFTER
            )
    )
    private void selectedItemChangeCallback(CallbackInfo ci) {
        EventManager.INSTANCE.post(new SelectedItemChangedEvent(this.getCurrentItem()));
    }

}
