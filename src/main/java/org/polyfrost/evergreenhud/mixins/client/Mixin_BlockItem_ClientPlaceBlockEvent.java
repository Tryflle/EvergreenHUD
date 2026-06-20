package org.polyfrost.evergreenhud.mixins.client;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import org.polyfrost.evergreenhud.client.ClientPlaceBlockEvent;
import org.polyfrost.oneconfig.api.event.v1.EventManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public class Mixin_BlockItem_ClientPlaceBlockEvent {

    @Inject(method = "placeBlock", at = @At("RETURN"))
    private void placeBlock(BlockPlaceContext blockPlaceContext, BlockState blockState, CallbackInfoReturnable<Boolean> cir) {
        var player = blockPlaceContext.getPlayer();
        if (player != null && player == Minecraft.getInstance().player) {
            EventManager.INSTANCE.post(new ClientPlaceBlockEvent(player, blockPlaceContext.getLevel()));
        }
    }

}
