package org.polyfrost.evergreenhud.mixins.client;

//#if FORGE
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import org.polyfrost.evergreenhud.client.ClientPlaceBlockEvent;
import org.polyfrost.oneconfig.api.event.v1.EventManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//#if MC >= 1.16.5
//$$ import net.minecraft.world.InteractionResult;
//$$ import net.minecraft.world.item.context.UseOnContext;
//#else
//#if MC >= 1.12.2
//$$ import net.minecraft.util.math.BlockPos;
//#else
import net.minecraft.util.BlockPos;
//#endif

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
//#endif

@Mixin(value = ForgeHooks.class, remap = false)
public class Mixin_ForgeHooks_ClientPlaceBlockEvent {

    @Inject(
            method = "onPlaceItemIntoWorld",
            at = @At(
                    value = "INVOKE",
                    //#if MC >= 1.12.2
                    //$$ target = "Lnet/minecraft/entity/player/EntityPlayer;addStat(Lnet/minecraft/stats/StatBase;)V",
                    //#else
                    target = "Lnet/minecraft/entity/player/EntityPlayer;addStat(Lnet/minecraft/stats/StatBase;I)V",
                    //#endif
                    shift = At.Shift.AFTER,
                    remap = true
            )
    )
    private static void onPlaceBlock(
            //#if MC >= 1.16.5
            //$$ UseOnContext ctx,
            //$$ CallbackInfoReturnable<InteractionResult> cir
            //#else
            ItemStack itemstack,
            EntityPlayer player,
            World world,
            BlockPos pos,
            EnumFacing side,
            float hitX,
            float hitY,
            float hitZ,
            //#if MC >= 1.12.2
            //$$ net.minecraft.util.EnumHand hand,
            //#endif
            CallbackInfoReturnable<Boolean> cir
            //#endif
    ) {
        //#if MC >= 1.16.5
        //$$ Player player = ctx.getPlayer();
        //$$ if (player == null) {
        //$$     return;
        //$$ }
        //$$
        //$$ Level world = ctx.getLevel();
        //#endif
        EventManager.INSTANCE.post(new ClientPlaceBlockEvent(player, world));
    }

}
//#endif