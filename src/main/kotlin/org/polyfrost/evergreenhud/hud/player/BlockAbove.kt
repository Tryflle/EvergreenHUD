package org.polyfrost.evergreenhud.hud.player

import net.minecraft.block.BlockBanner
import net.minecraft.block.BlockSign
import net.minecraft.block.BlockVine
import net.minecraft.client.Minecraft
import net.minecraft.init.Blocks
import net.minecraft.util.BlockPos
import org.polyfrost.evergreenhud.PlayerPosEvent
import org.polyfrost.oneconfig.api.config.v1.annotations.Slider
import org.polyfrost.oneconfig.api.config.v1.annotations.Switch
import org.polyfrost.oneconfig.api.event.v1.eventHandler
import org.polyfrost.oneconfig.api.hud.v1.TextHud
import org.polyfrost.universal.USound

class BlockAbove : TextHud("Block Above: ", " remaining") {
    private var above = 0
    private var notified = false

    @Switch(title = "Notify With Sound")
    var notify = false

    @Slider(title = "Notify Height", min = 1F, max = 10F, step = 1F)
    var notifyHeight = 3

    @Slider(title = "Check Height", min = 1F, max = 30F, step = 1F)
    var checkHeight = 10

    private var ppx = 0
    private var ppy = 0
    private var ppz = 0

    override fun initialize() {
        eventHandler(this::check)
        super.initialize()
        if (isReal) {
            updateWhenChanged("checkHeight")
        }
    }

    fun check(event: PlayerPosEvent) {
        val (x, y, z) = event
        if (x.toInt() == ppx && y.toInt() == ppy && z.toInt() == ppz) return
        ppx = x.toInt()
        ppy = y.toInt()
        ppz = z.toInt()
        val world = Minecraft.getMinecraft().theWorld
        if (world == null) {
            above = 0
            return
        }

        var above = 0
        for (i in 1..checkHeight) {
            val pos = BlockPos(x, y + 1.0 + i, z)
            if (pos.y > world.height) break

            val state = world.getBlockState(pos) ?: continue
            if (state.block == Blocks.air
                || state.block == Blocks.water
                || state.block is BlockSign
                || state.block is BlockVine
                || state.block is BlockBanner
            ) continue

            above = i

            if (above <= notifyHeight && notify) {
                if (!notified) {
                    USound.playExpSound()
                    notified = true
                }
            } else {
                notified = false
            }

            break
        }

        this.above = above
        updateAndRecalculate()
    }

    override fun getText(): String? {
        sb.append(above)
        return null
    }

    override fun id() = "blockabove.json"

    override fun title() = "Block Above"

    override fun category() = Category.PLAYER
}