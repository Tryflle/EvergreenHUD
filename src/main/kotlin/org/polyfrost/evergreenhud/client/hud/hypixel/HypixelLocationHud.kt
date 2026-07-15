package org.polyfrost.evergreenhud.client.hud.hypixel

import org.polyfrost.evergreenhud.client.utils.CachedTextHud
import org.polyfrost.oneconfig.api.config.v1.annotations.Switch
import org.polyfrost.oneconfig.api.config.v1.annotations.Text
import org.polyfrost.oneconfig.api.event.v1.eventHandler
import org.polyfrost.oneconfig.api.event.v1.events.HypixelLocationEvent
import org.polyfrost.oneconfig.api.hypixel.v1.HypixelUtils.Location

class HypixelLocationHud(
    title: String,
    private val getter: Location.() -> String?
) : CachedTextHud(
    title = title,
    category = Category.INFO,
) {
    @Switch(title = "Hide If Not In-Game or Supported")
    var shouldHide = true

    @Text(title = "No Location Text")
    var noLocationText = "Unknown"

    override val defaultText: String by ::noLocationText

    override fun setup() {
        super.setup()
        hidden = true
        eventHandler { event: HypixelLocationEvent ->
            val string = event.location.getter()
            hidden = string == null && shouldHide
            updateWithText(string)
        }

        if (isReal) {
            updateWhenChanged("shouldHide")
        }
    }
}
