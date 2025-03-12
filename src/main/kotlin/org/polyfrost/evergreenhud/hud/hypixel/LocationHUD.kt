package org.polyfrost.evergreenhud.hud.hypixel

import org.polyfrost.oneconfig.api.config.v1.annotations.Switch
import org.polyfrost.oneconfig.api.config.v1.annotations.Text
import org.polyfrost.oneconfig.api.event.v1.eventHandler
import org.polyfrost.oneconfig.api.event.v1.events.HypixelLocationEvent
import org.polyfrost.oneconfig.api.hud.v1.TextHud
import org.polyfrost.oneconfig.api.hypixel.v1.HypixelUtils.Location

class LocationHUD(private val title: String, prefix: String = "$title: ", suffix: String = "", private val getter: Location.() -> String?) : TextHud(prefix, suffix) {

    @Switch(title = "Hide If Not In-Game or Supported")
    var shouldHide = true

    @Text(title = "No Location Text")
    var noLocationText = "Unknown"

    override fun initialize() {
        super.initialize()
        eventHandler { event: HypixelLocationEvent ->
            val string = event.location.getter()
            if (string != null) {
                sb.append(string)
                hidden = false
            } else {
                sb.append(noLocationText)
                hidden = shouldHide
            }
            updateAndRecalculate()
        }
        if(isReal) {
            updateWhenChanged("shouldHide")
        }
    }

    override fun id() = "${title.replace(' ', '_').lowercase()}.json"

    override fun title() = title

    override fun category() = Category.INFO

    override fun getText() = null
}