package org.polyfrost.evergreenhud.hud

import org.polyfrost.oneconfig.api.config.v1.annotations.*
import org.polyfrost.oneconfig.hud.SingleTextHud
import org.polyfrost.oneconfig.utils.v1.dsl.mc
import org.polyfrost.evergreenhud.config.HudConfig

class CCounter: HudConfig("C Counter", "evergreenhud/ccounter.json", false) {
    @HUD(name = "Main")
    var hud = CCounterHud()

    init {
        initialize()
    }

    class CCounterHud: SingleTextHud("C", true, 400, 70) {

        @Switch(
                name = "Simplified"
        )
        var simplified = true

        override fun getText(example: Boolean): String {
            if (mc.thePlayer == null) return "Unknown"
            return if (simplified) mc.renderGlobal.debugInfoRenders.split("/")[0].replace("C: ", "")
                else mc.renderGlobal.debugInfoRenders.split(" ")[1]
        }
    }

}