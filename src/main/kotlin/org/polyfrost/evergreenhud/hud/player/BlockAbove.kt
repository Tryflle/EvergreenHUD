package org.polyfrost.evergreenhud.hud.player

import dev.deftu.omnicore.common.OmniSound
import net.minecraft.block.BlockBanner
import net.minecraft.block.BlockSign
import net.minecraft.block.BlockVine
import net.minecraft.client.Minecraft
import net.minecraft.init.Blocks
import net.minecraft.util.BlockPos
import net.minecraft.util.ResourceLocation
import org.polyfrost.evergreenhud.BlockPositionChangedEvent
import org.polyfrost.oneconfig.api.config.v1.annotations.Slider
import org.polyfrost.oneconfig.api.config.v1.annotations.Switch
import org.polyfrost.oneconfig.api.event.v1.eventHandler
import org.polyfrost.oneconfig.api.event.v1.events.TickEvent
import org.polyfrost.oneconfig.api.hud.v1.TextHud

// CHECK OK
class BlockAbove : TextHud("Block Above: ", " remaining") {
    private var notified = false

    @Switch(title = "Notify With Sound")
    var notify = false

    val EXP_SOUND = OmniSound(ResourceLocation("random.orb"))

    @Slider(title = "Notify Height", min = 1F, max = 10F, step = 1F)
    var notifyHeight = 3

    @Slider(title = "Check Height", min = 1F, max = 30F, step = 1F)
    var checkHeight = 10


    override fun initialize() {
        eventHandler { (x, y, z): BlockPositionChangedEvent ->
            check(x, y, z)
        }
        super.initialize()
        if (isReal) {
            updateWhenChanged("checkHeight")
        }
    }

    fun check(x: Int, y: Int, z: Int) {
        val world = Minecraft.getMinecraft().theWorld ?: return

        var above = 0
        for (i in 1..checkHeight) {
            val pos = BlockPos(x, y + 1 + i, z)
            if (pos.y > world.height) break

            val state = world.getBlockState(pos) ?: continue
            if (state.block == Blocks.air
                || state.block == Blocks.water
                || state.block is BlockSign
                || state.block is BlockVine
                || state.block is BlockBanner
            ) continue

            above = i - 1

            if (above <= notifyHeight && notify) {
                if (!notified) {
                    EXP_SOUND.playForClient(0.25f, 1f)
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

    override fun getText() = null

    override fun id() = "blockabove.json"

    override fun title() = "Block Above"

    override fun category() = Category.PLAYER
}