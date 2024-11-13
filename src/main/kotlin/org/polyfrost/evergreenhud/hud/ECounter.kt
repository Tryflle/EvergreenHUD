package org.polyfrost.evergreenhud.hud

import org.polyfrost.oneconfig.api.config.v1.annotations.*
import org.polyfrost.oneconfig.hud.SingleTextHud
import org.polyfrost.oneconfig.utils.v1.dsl.mc
import org.polyfrost.evergreenhud.config.HudConfig

class ECounter: HudConfig("E Counter", "evergreenhud/ecounter.json", false) {
    @HUD(name = "Main")
    var hud = ECounterHUD()

    init {
        initialize()
    }

    class ECounterHUD : SingleTextHud("E", true, 400, 90) {

        @Switch(
                name = "Simplified"
        )
        var simplified = true

        override fun getText(example: Boolean): String {
            if (mc.thePlayer == null) return "Unknown"

            val delimiter = if (simplified) '/' else ','
            return mc.renderGlobal.debugInfoEntities.substringAfter("E: ").substringBefore(delimiter)
        }
    }

}