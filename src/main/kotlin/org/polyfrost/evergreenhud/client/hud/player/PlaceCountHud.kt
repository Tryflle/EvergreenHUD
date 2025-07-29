package org.polyfrost.evergreenhud.client.hud.player

import dev.deftu.omnicore.client.OmniClientPlayer
import org.polyfrost.evergreenhud.client.ClientPlaceBlockEvent
import org.polyfrost.oneconfig.api.config.v1.annotations.Slider
import org.polyfrost.oneconfig.api.event.v1.eventHandler
import org.polyfrost.oneconfig.api.hud.v1.TextHud
import org.polyfrost.polyui.unit.milliseconds
import org.polyfrost.polyui.utils.fastRemoveIfReversed

// CHECK OK
class PlaceCountHud : TextHud(
    id = "placecount.json",
    title = "Block Place Count",
    category = Category.COMBAT,
    prefix = "Blocks: ",
    suffix = "/s"
) {

    @Slider(title = "Interval (ms)", min = 500F, max = 3000F)
    var interval = 1000

    private var blockCount: ArrayList<Long>? = null

    override fun setup() {
        super.setup()

        if (isReal) {
            blockCount = ArrayList()
            eventHandler { event: ClientPlaceBlockEvent ->
                if (event.player == OmniClientPlayer.getInstance()) {
                    blockCount?.add(System.nanoTime())
                    updateAndRecalculate()
                }
            }
        }
    }

    override fun getText(): String? {
        val time = System.nanoTime()
        val max = interval * 1_000_000L
        blockCount?.fastRemoveIfReversed { time - it > max }
        sb.append(blockCount?.size ?: 0L)
        return null
    }

    override fun updateFrequency() = 50.milliseconds

}
