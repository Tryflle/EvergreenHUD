package org.polyfrost.evergreenhud.hud.player

import org.polyfrost.evergreenhud.SaturationChangedEvent
import org.polyfrost.evergreenhud.hud.GenericHUD1f
import org.polyfrost.oneconfig.api.event.v1.eventHandler

// CHECK OK
class Saturation : GenericHUD1f("Saturation") {
    override fun initialize() {
        super.initialize()
        eventHandler { (saturation): SaturationChangedEvent ->
            value = saturation
            updateAndRecalculate()
        }
    }
}