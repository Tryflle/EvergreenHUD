package org.polyfrost.evergreenhud.client.hud

import net.minecraft.network.play.server.S03PacketTimeUpdate
import org.polyfrost.evergreenhud.client.utils.GenericNumberHud
import org.polyfrost.oneconfig.api.event.v1.eventHandler
import org.polyfrost.oneconfig.api.event.v1.events.PacketEvent

// CHECK OK
class TpsHud : GenericNumberHud(
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
                    S03PacketTimeUpdate
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
