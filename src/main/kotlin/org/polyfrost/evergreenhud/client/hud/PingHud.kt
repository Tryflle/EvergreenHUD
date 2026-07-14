package org.polyfrost.evergreenhud.client.hud

import org.polyfrost.evergreenhud.client.utils.CachedTextHud
import org.polyfrost.oneconfig.api.event.v1.eventHandler
import org.polyfrost.oneconfig.api.event.v1.events.WorldEvent
import org.polyfrost.oneconfig.utils.v1.dsl.mc
import kotlin.time.Duration.Companion.seconds

class PingHud : CachedTextHud(
    title = "Ping",
    category = Category.INFO,
) {
    override fun setup() {
        super.setup()
        eventHandler { _: WorldEvent.Load ->
            hidden = mc.hasSingleplayerServer()
        }
    }

    override fun updateFrequency(): Long {
        return 0.5.seconds.inWholeNanoseconds
    }

    override fun getText(): String {
        val ping = if (isReal) {
            mc.player?.uuid?.let { mc.connection?.getPlayerInfo(it)?.latency }
        } else null
        return "${ping ?: -1}ms"
    }
}
