package org.polyfrost.evergreenhud.client.hud

import dev.deftu.omnicore.client.OmniClient
import dev.deftu.omnicore.common.OmniBlockPos
import dev.deftu.omnicore.common.OmniSounds
import net.minecraft.block.Block
import net.minecraft.block.BlockBanner
import net.minecraft.block.BlockSign
import net.minecraft.block.BlockVine
import net.minecraft.init.Blocks
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
            Blocks.air,
            Blocks.water,
        )

        private val Block.isIgnored: Boolean
            get() {
                return ignoredBlocks.contains(this) || this is BlockSign || this is BlockVine || this is BlockBanner
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
            val world = OmniClient.currentWorld ?: return@eventHandler

            var above = 0
            for (i in 1..checkHeight) {
                val pos = OmniBlockPos.from(x, y + 1 + i, z)
                if (pos.y > world.height) {
                    break
                }

                val block = world.getBlockTypeAt(pos) ?: continue
                if (block.isIgnored) {
                    continue
                }

                above = i - 1
                if (above <= notifyHeight && notify) {
                    if (!notified) {
                        OmniSounds.EXPERIENCE_ORB_PICKUP.playForClient(0.25f, 1f)
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
