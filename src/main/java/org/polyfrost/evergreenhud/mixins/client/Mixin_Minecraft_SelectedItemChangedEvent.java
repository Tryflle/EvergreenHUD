package org.polyfrost.evergreenhud.mixins.client;

import dev.deftu.omnicore.api.client.OmniClient;
import dev.deftu.omnicore.api.equipment.EquipmentType;
import dev.deftu.omnicore.api.equipment.OmniEquipment;
import net.minecraft.client.Minecraft;
import org.objectweb.asm.Opcodes;
import org.polyfrost.evergreenhud.client.SelectedItemChangedEvent;
import org.polyfrost.oneconfig.api.event.v1.EventManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class Mixin_Minecraft_SelectedItemChangedEvent {
    @Inject(
            //#if MC >= 1.12.2
            //$$ method = "processKeyBinds",
            //#else
            method = "runTick",
            //#endif
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/entity/player/InventoryPlayer;currentItem:I",
                    shift = At.Shift.AFTER,
                    opcode = Opcodes.PUTFIELD
            )
    )
    private void selectedItemChangeCallback(CallbackInfo ci) {
        EventManager.INSTANCE.post(new SelectedItemChangedEvent(OmniEquipment.get(OmniClient.getPlayer(), EquipmentType.MainHand.INSTANCE)));
    }
}
