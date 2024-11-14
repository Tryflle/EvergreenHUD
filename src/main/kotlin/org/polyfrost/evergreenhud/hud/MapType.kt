package org.polyfrost.evergreenhud.hud

import org.polyfrost.evergreenhud.utils.PinkuluAPIManager
import org.polyfrost.oneconfig.api.config.v1.annotations.Switch
import org.polyfrost.oneconfig.api.event.v1.eventHandler
import org.polyfrost.oneconfig.api.event.v1.events.HypixelLocationEvent
import org.polyfrost.oneconfig.api.hud.v1.TextHud
import kotlin.jvm.optionals.getOrNull

class MapType : TextHud("Map Type:") {

    private var mapName: String? = null

    init {
        eventHandler { event: HypixelLocationEvent ->
            this.mapName = event.location.mapName.getOrNull()?.ifEmpty { null }
            if (shouldHide) hidden = this.mapName == null
            update()
        }
    }

    @Switch(title = "Hide If Not In-Game or Supported")
    var shouldHide = true

    override fun id() = "evergreenhud/map.json"

    override fun title() = "Hypixel Map"

    override fun category() = Category.INFO

    override fun getText(): String {
        sb.append(PinkuluAPIManager.getMapPool() ?: "Unknown")
        return null
    }
}