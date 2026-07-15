package org.polyfrost.evergreenhud.client.hud


import net.minecraft.network.protocol.common.ClientboundPingPacket
import net.minecraft.network.protocol.game.ClientboundSetTimePacket
import org.polyfrost.evergreenhud.client.ServerChangedEvent
import org.polyfrost.evergreenhud.client.utils.GenericNumberHud
import org.polyfrost.oneconfig.api.event.v1.eventHandler
import org.polyfrost.oneconfig.api.event.v1.events.PacketEvent
import org.polyfrost.oneconfig.api.hypixel.v1.HypixelUtils

class TpsHud : GenericNumberHud(
    title = "TPS",
    category = Category.INFO,
) {
    private var lastUpdated = 0L

    private var useTickPings = false

    private val pingTimes = ArrayDeque<Long>()

    override fun setup() {
        super.setup()

        eventHandler { (ip): ServerChangedEvent ->
            useTickPings = HypixelUtils.isHypixel()
            lastUpdated = 0L
            pingTimes.clear()
        }

        eventHandler { (packet): PacketEvent.Receive ->
            when (packet) {
                is ClientboundSetTimePacket -> if (!useTickPings) onTimeUpdate()
                is ClientboundPingPacket -> if (useTickPings) onTickPing()
            }
        }
    }

    private fun onTimeUpdate() {
        val now = System.currentTimeMillis()
        val timeTaken = now - lastUpdated
        lastUpdated = now
        updateWithNumber((20000f / timeTaken).coerceIn(0f, 20f))
    }

    private fun onTickPing() {
        val now = System.currentTimeMillis()
        pingTimes.addLast(now)

        while (pingTimes.size > 2 && now - pingTimes.first() > WINDOW_MS) {
            pingTimes.removeFirst()
        }

        val span = now - pingTimes.first()
        if (pingTimes.size < 2 || span <= 0L) return
        updateWithNumber(((pingTimes.size - 1) * 1000f / span).coerceIn(0f, 20f))
    }

    private companion object {
        const val WINDOW_MS = 1000L
    }
}
