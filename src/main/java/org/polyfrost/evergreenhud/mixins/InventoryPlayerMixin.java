package org.polyfrost.evergreenhud.mixins;

import net.minecraft.entity.player.InventoryPlayer;
import org.polyfrost.evergreenhud.hud.player.Armour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryPlayer.class)
public class InventoryPlayerMixin {
    @Inject(method = "damageArmor", at = @At("RETURN"))
    private void evergreen$readArmorDamage(float amount, CallbackInfo ci) {
        Armour.damaged(amount);
    }
}
