package org.polyfrost.evergreenhud.hud

import org.polyfrost.evergreenhud.utils.decimalFormat
import org.polyfrost.oneconfig.api.config.v1.annotations.HUD
import org.polyfrost.oneconfig.api.config.v1.annotations.Slider
import org.polyfrost.oneconfig.api.config.v1.annotations.Switch
import org.polyfrost.oneconfig.hud.SingleTextHud
import org.polyfrost.oneconfig.utils.v1.dsl.mc
import org.polyfrost.evergreenhud.config.HudConfig

class Saturation: HudConfig("Saturation", "evergreenhud/saturation.json", false) {
    @HUD(name = "Main")
    var hud = SaturationHud()

    init {
        initialize()
    }

    class SaturationHud: SingleTextHud("Saturation", true, 180, 10) {

        @Slider(
            name = "Accuracy",
            min = 0F,
            max = 20F
        )
        var accuracy = 1

        @Switch(
            name = "Trailing Zeros"
        )
        var trailingZeros = true

        override fun getText(example: Boolean): String {
            return decimalFormat(accuracy, trailingZeros).format(mc.thePlayer?.foodStats?.saturationLevel ?: 20)
        }
    }
}
