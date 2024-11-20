package org.polyfrost.evergreenhud.hud.hypixel

import org.polyfrost.oneconfig.api.config.v1.annotations.Switch
import org.polyfrost.oneconfig.api.event.v1.eventHandler
import org.polyfrost.oneconfig.api.event.v1.events.HypixelLocationEvent
import org.polyfrost.oneconfig.api.hud.v1.TextHud
import org.polyfrost.oneconfig.api.hypixel.v1.HypixelUtils.Location

class LocationHUD(private val title: String, prefix: String = "$title: ", suffix: String = "", private val getter: Location.() -> String?) : TextHud(prefix, suffix) {
    private var string: String? = null

    @Switch(title = "Hide If Not In-Game or Supported")
    var shouldHide = true

    override fun initialize() {
        super.initialize()
        eventHandler { event: HypixelLocationEvent ->
            this.string = event.location.getter()
            if (shouldHide) hidden = this.string == null
            updateAndRecalculate()
        }.register()
        if(isReal) {
            updateWhenChanged("shouldHide")
        }
    }

    override fun id() = "${title.replace(' ', '_').lowercase()}.json"

    override fun title() = title

    override fun category() = Category.INFO

    override fun getText(): String? {
        if (string == null) sb.append("Unknown")
        else sb.append(string)
        return null
    }
}