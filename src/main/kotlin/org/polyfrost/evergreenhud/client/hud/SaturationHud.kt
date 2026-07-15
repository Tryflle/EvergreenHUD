package org.polyfrost.evergreenhud.client.hud

import org.polyfrost.evergreenhud.client.SaturationChangedEvent
import org.polyfrost.evergreenhud.client.utils.GenericNumberHud
import org.polyfrost.evergreenhud.client.utils.SaturationTracker
import org.polyfrost.oneconfig.api.event.v1.eventHandler

class SaturationHud : GenericNumberHud(
    title = "Saturation",
    category = Category.INFO,
    defaultValue = 5f,
) {
    override fun setup() {
        super.setup()
        if (isReal) {
            updateWithNumber(SaturationTracker.saturation)
        }
        eventHandler { (saturation): SaturationChangedEvent ->
            updateWithNumber(saturation)
        }
    }
}
