package org.polyfrost.evergreenhud.hud.player

import org.polyfrost.evergreenhud.utils.decimalFormat
import org.polyfrost.oneconfig.api.config.v1.annotations.Slider
import org.polyfrost.oneconfig.api.config.v1.annotations.Switch
import org.polyfrost.oneconfig.api.hud.v1.TextHud

class Saturation : TextHud("Saturation: ") {
    @Slider(title = "Accuracy", min = 0F, max = 5F)
    var accuracy = 1

    @Switch(title = "Trailing Zeros")
    var trailingZeros = true

    private var df = decimalFormat(accuracy, trailingZeros)
    private var saturation = 20f

    override fun initialize() {
        if (isReal) {
            addCallback("accuracy") { value: Int ->
                df = decimalFormat(value, trailingZeros)
            }
            addCallback("trailingZeros") { state: Boolean ->
                df = decimalFormat(accuracy, state)
            }
        }
    }

    fun update(saturation: Float) {
        this.saturation = saturation
        update()
    }

    override fun getText(): String {
        sb.append(df.format(saturation))
        return null
    }

    override fun title() = "Saturation"

    override fun category() = Category.PLAYER

    override fun id() = "evergreenhud/saturation.json"
}
