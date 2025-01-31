package org.polyfrost.evergreenhud.hud.data

import org.polyfrost.evergreenhud.hud.GenericHUD1f
import org.polyfrost.oneconfig.api.event.v1.eventHandler
import org.polyfrost.oneconfig.api.event.v1.events.ReceivePacketEvent

class TPS : GenericHUD1f("TPS") {
    private var lastUpdated = 0L

    override fun initialize() {
        eventHandler { event: ReceivePacketEvent ->
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
        super.initialize()
    }
}