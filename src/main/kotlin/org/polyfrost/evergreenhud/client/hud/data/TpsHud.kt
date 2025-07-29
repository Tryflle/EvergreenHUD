package org.polyfrost.evergreenhud.client.hud.data

import org.polyfrost.evergreenhud.client.hud.GenericHud1f
import org.polyfrost.oneconfig.api.event.v1.eventHandler
import org.polyfrost.oneconfig.api.event.v1.events.PacketEvent

// CHECK OK
class TpsHud : GenericHud1f(
    title = "TPS",
    category = Category.INFO,
) {

    private var lastUpdated = 0L

    override fun setup() {
        super.setup()
        eventHandler { event: PacketEvent.Receive ->
            if (event.getPacket<Any>() is
                    //#if MC>=11202
                    //$$ net.minecraft.network.play.server.SPacketTimeUpdate
                    //#else
                    net.minecraft.network.play.server.S03PacketTimeUpdate
                    //#endif
            ) {
                val now = System.currentTimeMillis()
                val timeTaken = now - lastUpdated
                lastUpdated = now
                value = (20000f / timeTaken).coerceIn(0f, 20f)
                updateAndRecalculate()
            }
        }
    }

}
