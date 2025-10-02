package org.polyfrost.evergreenhud.client.hud

import org.polyfrost.evergreenhud.client.SaturationChangedEvent
import org.polyfrost.evergreenhud.client.utils.GenericNumberHud
import org.polyfrost.oneconfig.api.event.v1.eventHandler

// CHECK OK
class SaturationHud : GenericNumberHud(
    title ="Saturation",
    category = Category.INFO
) {
    override fun setup() {
        super.setup()
        eventHandler { (saturation): SaturationChangedEvent ->
            value = saturation
            updateAndRecalculate()
        }
    }
}
