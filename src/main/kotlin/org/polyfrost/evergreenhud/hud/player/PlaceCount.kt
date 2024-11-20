package org.polyfrost.evergreenhud.hud.player

import net.minecraft.client.Minecraft
import org.polyfrost.evergreenhud.ClientPlaceBlockEvent
import org.polyfrost.oneconfig.api.config.v1.annotations.Slider
import org.polyfrost.oneconfig.api.event.v1.eventHandler
import org.polyfrost.oneconfig.api.hud.v1.TextHud
import org.polyfrost.polyui.utils.fastRemoveIfReversed

class PlaceCount : TextHud("Blocks: ") {
    @Slider(title = "Interval (ms)", min = 500F, max = 3000F)
    var interval = 1000

    private val blockCount = ArrayList<Long>()

    init {
        eventHandler { event: ClientPlaceBlockEvent ->
            if (event.player == Minecraft.getMinecraft().thePlayer) {
                blockCount.add(System.nanoTime())
                updateAndRecalculate()
            }
        }.register()
    }

    override fun id() = "placecount.json"

    override fun title() = "Block Place Count"

    override fun category() = Category.COMBAT

    override fun getText(): String? {
        process()
        sb.append(blockCount.size)
        return null
    }

    private fun process() {
        val current = System.nanoTime()
        blockCount.fastRemoveIfReversed {
            if (current - it > interval * 1000) true
            else return
        }
    }
}