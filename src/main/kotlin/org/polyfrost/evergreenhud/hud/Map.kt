package org.polyfrost.evergreenhud.hud

import org.polyfrost.oneconfig.api.config.v1.annotations.*
import org.polyfrost.oneconfig.hud.SingleTextHud
import org.polyfrost.oneconfig.api.hypixel.v1.*
import org.polyfrost.evergreenhud.config.HudConfig

class Map : HudConfig("Map", "evergreenhud/map.json", false) {

    @HUD(
        name = "Main"
    )
    var hud = MapHud()

    init {
        initialize()
    }

    class MapHud : SingleTextHud("Map", true, 180, 90) {
        @Switch(
            name = "Hide If Not In-Game or Supported"
        )
        var hide = true

        override fun getText(example: Boolean): String {
            return LocrawUtil.INSTANCE.locrawInfo?.mapName ?: "Unknown"
        }

        override fun shouldShow(): Boolean {
            return super.shouldShow() && (!hide || LocrawUtil.INSTANCE.locrawInfo?.mapName?.isNotBlank() == true)
        }
    }
}