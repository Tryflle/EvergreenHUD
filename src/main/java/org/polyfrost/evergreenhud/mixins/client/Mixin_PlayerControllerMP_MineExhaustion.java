// Derived from AppleSkin (https://github.com/squeek502/AppleSkin), made by squeek502.
// AppleSkin is licensed under the Unlicense (public domain).

package org.polyfrost.evergreenhud.mixins.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.polyfrost.evergreenhud.client.utils.SaturationTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiPlayerGameMode.class)
public class Mixin_PlayerControllerMP_MineExhaustion {
    @Unique
    private BlockState evergreenhud$stateBeingDestroyed;

    @Inject(method = "destroyBlock", at = @At("HEAD"))
    private void evergreenhud$captureState(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        Level level = Minecraft.getInstance().level;
        evergreenhud$stateBeingDestroyed = level == null ? null : level.getBlockState(pos);
    }

    @Inject(method = "destroyBlock", at = @At("RETURN"))
    private void evergreenhud$trackMineExhaustion(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        BlockState state = evergreenhud$stateBeingDestroyed;
        evergreenhud$stateBeingDestroyed = null;

        if (!cir.getReturnValueZ() || state == null) return;

        Player player = Minecraft.getInstance().player;
        if (player == null || !player.hasCorrectToolForDrops(state)) return;

        SaturationTracker.INSTANCE.onDestroyBlock();
    }
}
