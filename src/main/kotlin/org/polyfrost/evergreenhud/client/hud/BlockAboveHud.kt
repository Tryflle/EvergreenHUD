package org.polyfrost.evergreenhud.client.hud

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.level.block.BannerBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.SignBlock
import net.minecraft.world.level.block.VineBlock
import org.polyfrost.evergreenhud.client.BlockChangeEvent
import org.polyfrost.evergreenhud.client.BlockPositionChangedEvent
import org.polyfrost.evergreenhud.client.utils.CachedTextHud
import org.polyfrost.oneconfig.api.config.v1.annotations.Slider
import org.polyfrost.oneconfig.api.config.v1.annotations.Switch
import org.polyfrost.oneconfig.api.event.v1.eventHandler
import org.polyfrost.oneconfig.api.hud.v1.TextHud
import org.polyfrost.oneconfig.utils.v1.dsl.mc

class BlockAboveHud : CachedTextHud(
    title = "Block Above",
    category = Category.PLAYER,
    suffix = "remaining",
    defaultText = "0",
) {

    private var notified = false

    @Switch(title = "Notify With Sound")
    var notify = false

    @Slider(title = "Notify Height", min = 1F, max = 10F, step = 1F)
    var notifyHeight = 3

    @Slider(title = "Check Height", min = 1F, max = 30F, step = 1F)
    var checkHeight = 10

    override fun setup() {
        super.setup()
        eventHandler { (pos): BlockPositionChangedEvent ->
            update(pos)
        }
        eventHandler { (pos): BlockChangeEvent ->
            val player = mc.player?.blockPosition() ?: return@eventHandler
            if (pos.y > player.y && pos.x == player.x && pos.z == player.z) {
                update(player)
            }
        }

        if (isReal) {
            updateWhenChanged("checkHeight")
        }
    }

    private fun update(currentPos: BlockPos) {
        val level = mc.level ?: return
        val pos = currentPos.mutable().move(Direction.UP)

        var above = 0
        for (i in 1..checkHeight) {
            pos.move(Direction.UP)
            //? if > 1.21.1
            if (pos.y > level.maxY) {
            //? if <= 1.21.1
            //if (pos.y > level.maxBuildHeight) {
                break
            }

            val block = level.getBlockState(pos).block ?: continue
            if (block.isIgnored) {
                continue
            }

            above = i - 1
            if (above <= notifyHeight && notify) {
                if (!notified) {
                    mc.player?.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 0.25f, 1f)
                    notified = true
                }
            } else {
                notified = false
            }

            break
        }

        updateWithText(above)
    }

    private companion object {
        private val ignoredBlocks = setOf(
            Blocks.AIR,
            Blocks.WATER,
        )

        private val Block.isIgnored: Boolean
            get() {
                return ignoredBlocks.contains(this) || this is SignBlock || this is VineBlock || this is BannerBlock
            }
    }
}
