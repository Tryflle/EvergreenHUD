package org.polyfrost.evergreenhud.hud

import org.polyfrost.evergreenhud.ClientPlaceBlockEvent
import org.polyfrost.oneconfig.api.config.v1.annotations.*
import org.polyfrost.oneconfig.api.event.v1.EventManager
import org.polyfrost.oneconfig.api.event.v1.events.event.*
import org.polyfrost.oneconfig.hud.SingleTextHud
import org.polyfrost.oneconfig.libs.eventbus.Subscribe
import org.polyfrost.oneconfig.utils.v1.dsl.mc
import org.polyfrost.evergreenhud.config.HudConfig

class PlaceCount: HudConfig("Block Place Count", "evergreenhud/placecount.json", false) {
    @HUD(name = "Main")
    var hud = PlaceCountHud()

    init {
        initialize()
    }

    class PlaceCountHud : SingleTextHud("Blocks", true, 120, 30) {
        @Slider(
            name = "Interval",
            min = 500F,
            max = 3000F
        )
        var interval = 1000

        init {
            EventManager.INSTANCE.register(this)
        }

        private val blockCount = ArrayDeque<Long>()

        @Subscribe
        private fun onTick(event: TickEvent) {
            if (event.stage == Stage.START) {
                val currentTime = System.currentTimeMillis()
                if (!blockCount.isEmpty()) {
                    while ((currentTime - blockCount.first()) > interval) {
                        blockCount.removeFirst()
                        if (blockCount.isEmpty()) break
                    }
                }
            }
        }

        @Subscribe
        private fun onBlockPlace(event: ClientPlaceBlockEvent) {
            if (event.player == mc.thePlayer) {
                blockCount.addLast(System.currentTimeMillis())
            }
        }

        override fun getText(example: Boolean): String {
            return blockCount.size.toString()
        }

    }
}