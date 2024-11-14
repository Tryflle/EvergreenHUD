package org.polyfrost.evergreenhud.hud.player

import org.polyfrost.evergreenhud.utils.*
import org.polyfrost.oneconfig.api.config.v1.annotations.*
import org.polyfrost.oneconfig.api.hud.v1.TextHud

class Yaw : TextHud("Yaw: ", "\u00b0") {
    @Slider(title = "Accuracy", min = 0F, max = 8F)
    var accuracy = 2

    @Switch(title = "Trailing Zeros")
    var trailingZeros = true

    private var df = decimalFormat(accuracy, trailingZeros)
    private var yaw = 0f

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

    fun update(yaw: Float) {
        this.yaw = yaw
        update()
    }

    override fun getText(): String {
        sb.append(df.format(yaw))
        return null
    }

    override fun title() = "Yaw"

    override fun category() = Category.INFO

    override fun id() = "evergreenhud/yaw.json"
}