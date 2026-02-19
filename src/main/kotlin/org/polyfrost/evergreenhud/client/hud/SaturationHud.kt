package org.polyfrost.evergreenhud.client.hud

import net.minecraft.network.protocol.game.ClientboundSetHealthPacket
import org.polyfrost.evergreenhud.client.SaturationChangedEvent
import org.polyfrost.evergreenhud.client.utils.GenericNumberHud
import org.polyfrost.oneconfig.api.event.v1.eventHandler
import org.polyfrost.oneconfig.api.event.v1.events.PacketEvent

// CHECK OK
class SaturationHud : GenericNumberHud(
    title ="Saturation",
    category = Category.INFO
) {
    private fun update(saturation: Float) {
        value = saturation
        updateAndRecalculate()
    }

    override fun setup() {
        super.setup()
        eventHandler { (saturation): SaturationChangedEvent ->
            update(saturation)
        }
        eventHandler { (packet): PacketEvent.Receive ->
            val packet = packet as? SaturationChangedEvent ?: return@eventHandler
            update(packet.saturation)
        }
    }
}
