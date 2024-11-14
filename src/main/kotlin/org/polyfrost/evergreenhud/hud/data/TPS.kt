package org.polyfrost.evergreenhud.hud.data

import org.polyfrost.oneconfig.api.event.v1.eventHandler
import org.polyfrost.oneconfig.api.event.v1.events.ReceivePacketEvent
import org.polyfrost.oneconfig.api.hud.v1.TextHud

class TPS : TextHud("TPS: ") {
    private var lastUpdated = 0L
    private var tps = 20.0

    override fun initialize() {
        if (isReal) {
            eventHandler { event: ReceivePacketEvent ->
                if (event.getPacket<Any>() !is
                    //#if MC>=11202
                    //$$ net.minecraft.network.play.server.SPacketTimeUpdate
                    //#else
                    net.minecraft.network.play.server.S03PacketTimeUpdate
                    //#endif
                ) return

                val now = System.currentTimeMillis()
                val timeTaken = now - lastUpdated
                lastUpdated = now
                tps = (20000.0 / timeTaken).coerceIn(0.0, 20.0)
            }
        }
    }

    override fun getText(): String {
        sb.append(tps)
        return null
    }

    override fun id() = "evergreenhud/tps.json"

    override fun title() = "TPS"

    override fun category() = Category.INFO
}