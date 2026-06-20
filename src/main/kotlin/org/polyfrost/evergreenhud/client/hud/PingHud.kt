package org.polyfrost.evergreenhud.client.hud

import org.polyfrost.evergreenhud.client.utils.CachedTextHud
import org.polyfrost.evergreenhud.client.utils.pinger.ServerPinger
import org.polyfrost.evergreenhud.client.utils.pinger.ServerPingerPool
import org.polyfrost.oneconfig.api.config.v1.annotations.Slider
import org.polyfrost.oneconfig.api.config.v1.annotations.Switch
import org.polyfrost.oneconfig.api.event.v1.eventHandler
import org.polyfrost.oneconfig.api.event.v1.events.WorldEvent
import org.polyfrost.oneconfig.utils.v1.dsl.mc
import kotlin.time.Duration.Companion.seconds

class PingHud : CachedTextHud(
    title = "Ping",
    category = Category.INFO,
) {
    @Slider(title = "Ping Period", min = 20F, max = 120F)
    var interval = 20

    private lateinit var pinger: ServerPinger

    override fun setup() {
        super.setup()
        eventHandler { _: WorldEvent.Load ->
            hidden = mc.hasSingleplayerServer()
        }

        if (isReal) {
            pinger = ServerPingerPool.createPinger(
                intervalSupplier = { interval },
                serverSupplier = { mc.currentServer }
            )
        }
    }

    override fun updateFrequency(): Long {
        return 0.5.seconds.inWholeNanoseconds
    }

    override fun getText(): String {
        return (if (isReal && ::pinger.isInitialized) {
            pinger.ping?.toString() ?: "-1 (unknown)"
        } else "-1") + "ms"
    }
}
