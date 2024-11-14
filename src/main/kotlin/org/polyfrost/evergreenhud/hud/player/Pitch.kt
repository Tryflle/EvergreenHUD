package org.polyfrost.evergreenhud.hud.player

import org.polyfrost.evergreenhud.utils.decimalFormat
import org.polyfrost.oneconfig.api.config.v1.annotations.Slider
import org.polyfrost.oneconfig.api.config.v1.annotations.Switch
import org.polyfrost.oneconfig.api.hud.v1.TextHud

class Pitch : TextHud("Pitch: ", "\u00b0") {
    @Slider(title = "Accuracy", min = 0F, max = 8F)
    var accuracy = 2

    @Switch(title = "Trailing Zeros")
    var trailingZeros = true

    private var df = decimalFormat(accuracy, trailingZeros)
    private var pitch = 0f

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

    fun update(pitch: Float) {
        this.pitch = pitch
        update()
    }

    override fun getText(): String {
        sb.append(df.format(pitch))
        return null
    }

    override fun title() = "Pitch"

    override fun category() = Category.INFO

    override fun id() = "evergreenhud/pitch.json"
}