package org.polyfrost.evergreenhud.client.hud.player

import org.polyfrost.evergreenhud.client.SaturationChangedEvent
import org.polyfrost.evergreenhud.client.hud.GenericHud1f
import org.polyfrost.oneconfig.api.event.v1.eventHandler

// CHECK OK
class SaturationHud : GenericHud1f(
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
