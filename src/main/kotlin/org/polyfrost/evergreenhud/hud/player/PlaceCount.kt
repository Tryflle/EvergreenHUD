package org.polyfrost.evergreenhud.hud.player

import net.minecraft.client.Minecraft
import org.polyfrost.evergreenhud.ClientPlaceBlockEvent
import org.polyfrost.oneconfig.api.config.v1.annotations.Slider
import org.polyfrost.oneconfig.api.event.v1.eventHandler
import org.polyfrost.oneconfig.api.hud.v1.TextHud
import org.polyfrost.polyui.unit.milliseconds
import org.polyfrost.polyui.utils.fastRemoveIfReversed

class PlaceCount : TextHud("Blocks: ", "/s") {
    @Slider(title = "Interval (ms)", min = 500F, max = 3000F)
    var interval = 1000

    private val blockCount = ArrayList<Long>()

    override fun initialize() {
        super.initialize()
        eventHandler { event: ClientPlaceBlockEvent ->
            if (event.player == Minecraft.getMinecraft().thePlayer) {
                blockCount.add(System.nanoTime())
                updateAndRecalculate()
            }
        }
    }

    override fun id() = "placecount.json"

    override fun title() = "Block Place Count"

    override fun category() = Category.COMBAT

    override fun getText(): String? {
        val time = System.nanoTime()
        val max = interval * 1_000_000L
        blockCount.fastRemoveIfReversed { time - it > max }
        sb.append(blockCount.size)
        return null
    }

    override fun updateFrequency() = 50.milliseconds
}