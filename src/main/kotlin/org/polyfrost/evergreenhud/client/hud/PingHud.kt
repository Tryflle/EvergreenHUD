package org.polyfrost.evergreenhud.client.hud

import org.polyfrost.evergreenhud.client.utils.CachedTextHud
import org.polyfrost.oneconfig.api.config.v1.annotations.Slider
import org.polyfrost.oneconfig.api.event.v1.eventHandler
import org.polyfrost.oneconfig.api.event.v1.events.WorldEvent
import org.polyfrost.oneconfig.api.hud.v1.HudManager
import org.polyfrost.oneconfig.utils.v1.dsl.mc
import kotlin.time.Duration.Companion.milliseconds

class PingHud : CachedTextHud(
    title = "Ping",
    category = Category.INFO,
) {
    companion object {
        private const val MS_PER_TICK = 50

        @JvmStatic
        fun sampleIntervalTicks(): Int {
            var interval = -1
            for (hud in HudManager.activeInstances) {
                if (hud !is PingHud || hud.hidden) continue
                if (interval == -1 || hud.sampleIntervalTicks < interval) interval = hud.sampleIntervalTicks
            }
            return interval
        }
    }

    @Slider(title = "Update Rate (ms)", min = 250F, max = 5000F, step = 250F)
    var updateRate = 500

    private val sampleIntervalTicks get() = (updateRate / MS_PER_TICK).coerceAtLeast(1)

    override fun setup() {
        super.setup()
        eventHandler { _: WorldEvent.Load ->
            hidden = mc.hasSingleplayerServer()
        }
    }

    override fun updateFrequency(): Long {
        return updateRate.milliseconds.inWholeNanoseconds
    }

    override fun getText(): String {
        val ping = if (isReal) measuredPing() ?: serverReportedPing() else null
        return "${ping ?: -1}ms"
    }

    private fun measuredPing(): Long? {
        if (mc.connection == null) return null
        val logger = mc.debugOverlay?.pingLogger ?: return null
        val size = logger.size()
        return if (size == 0) null else logger.get(size - 1)
    }

    private fun serverReportedPing(): Long? =
        mc.player?.uuid?.let { mc.connection?.getPlayerInfo(it)?.latency?.toLong() }
}
