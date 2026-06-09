package org.polyfrost.evergreenhud.client.hud

import net.minecraft.core.BlockPos
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.level.block.BannerBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.SignBlock
import net.minecraft.world.level.block.VineBlock
import org.polyfrost.evergreenhud.client.BlockPositionChangedEvent
import org.polyfrost.oneconfig.api.config.v1.annotations.Slider
import org.polyfrost.oneconfig.api.config.v1.annotations.Switch
import org.polyfrost.oneconfig.api.event.v1.eventHandler
import org.polyfrost.oneconfig.api.hud.v1.TextHud
import org.polyfrost.oneconfig.utils.v1.dsl.mc

// CHECK OK
class BlockAboveHud : TextHud(
    id = "blockabove.json",
    title = "Block Above",
    category = Category.PLAYER,
    prefix = "Block Above: ",
    suffix = " remaining"
) {
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

    private var notified = false

    @Switch(title = "Notify With Sound")
    var notify = false

    @Slider(title = "Notify Height", min = 1F, max = 10F, step = 1F)
    var notifyHeight = 3

    @Slider(title = "Check Height", min = 1F, max = 30F, step = 1F)
    var checkHeight = 10


    override fun setup() {
        super.setup()
        eventHandler { (x, y, z): BlockPositionChangedEvent ->
            val level = mc.level ?: return@eventHandler

            var above = 0
            for (i in 1..checkHeight) {
                val pos = BlockPos(x, y + 1 + i, z)
                if (pos.y > level.maxY) {
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

            sb.append(above)
            updateAndRecalculate()
        }

        if (isReal) {
            updateWhenChanged("checkHeight")
        }
    }

    override fun getText(): String? {
        return null
    }
}
