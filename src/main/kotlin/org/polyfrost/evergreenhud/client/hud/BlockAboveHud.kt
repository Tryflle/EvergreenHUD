package org.polyfrost.evergreenhud.client.hud

import dev.deftu.omnicore.api.client.sound.OmniClientSound
import dev.deftu.omnicore.api.client.world
import dev.deftu.omnicore.api.data.pos.OmniBlockPos
import dev.deftu.omnicore.api.sound.OmniSounds
import dev.deftu.omnicore.api.world.getBlockTypeAt
import dev.deftu.omnicore.api.world.maxWorldHeight
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
            val world = world ?: return@eventHandler

            var above = 0
            for (i in 1..checkHeight) {
                val pos = OmniBlockPos(x, y + 1 + i, z)
                if (pos.y > world.maxWorldHeight) {
                    break
                }

                val block = world.getBlockTypeAt(pos.vanilla) ?: continue
                if (block.isIgnored) {
                    continue
                }

                above = i - 1
                if (above <= notifyHeight && notify) {
                    if (!notified) {
                        OmniClientSound.play(OmniSounds.ENTITY.experienceOrb, 0.25f, 1f)
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
