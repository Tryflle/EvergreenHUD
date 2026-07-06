package org.polyfrost.evergreenhud.client.hud


import net.minecraft.network.protocol.game.ClientboundSetTimePacket
import org.polyfrost.evergreenhud.client.utils.GenericNumberHud
import org.polyfrost.oneconfig.api.event.v1.eventHandler
import org.polyfrost.oneconfig.api.event.v1.events.PacketEvent

class TpsHud : GenericNumberHud(
    title = "TPS",
    category = Category.INFO,
) {
    private var lastUpdated = 0L

    override fun setup() {
        super.setup()
        eventHandler { event: PacketEvent.Receive ->
            if (event.getPacket<Any>() is ClientboundSetTimePacket) {
                val now = System.currentTimeMillis()
                val timeTaken = now - lastUpdated
                lastUpdated = now
                updateWithNumber((20000f / timeTaken).coerceIn(0f, 20f))
            }
        }
    }
}
