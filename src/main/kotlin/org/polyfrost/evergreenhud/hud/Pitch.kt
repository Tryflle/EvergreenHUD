package org.polyfrost.evergreenhud.hud

import org.polyfrost.evergreenhud.utils.decimalFormat
import org.polyfrost.oneconfig.api.config.v1.annotations.*
import org.polyfrost.oneconfig.hud.SingleTextHud
import org.polyfrost.oneconfig.utils.v1.dsl.mc
import org.polyfrost.evergreenhud.config.HudConfig

class Pitch: HudConfig("Pitch", "evergreenhud/pitch.json", false) {
    @HUD(name = "Main")
    var hud = PitchHud()

    init {
        initialize()
    }

    class PitchHud: SingleTextHud("Pitch", true, 180, 50) {

        @Slider(
            name = "Accuracy",
            min = 0F,
            max = 8F
        )
        var accuracy = 2

        @Switch(
            name = "Trailing Zeros"
        )
        var trailingZeros = true

        override fun getText(example: Boolean): String {
            return decimalFormat(accuracy, trailingZeros).format(mc.thePlayer?.rotationPitch ?: 0f )
        }

    }
}