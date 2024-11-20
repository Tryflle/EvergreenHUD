package org.polyfrost.evergreenhud.hud

import org.polyfrost.evergreenhud.utils.decimalFormat
import org.polyfrost.oneconfig.api.config.v1.annotations.Slider
import org.polyfrost.oneconfig.api.config.v1.annotations.Switch
import org.polyfrost.oneconfig.api.hud.v1.TextHud

open class GenericHUD1f(private var title: String, suffix: String = "", prefix: String = "$title: ") : TextHud(prefix, suffix) {
    @Slider(title = "Accuracy", min = 0F, max = 8F)
    var accuracy = 2

    @Switch(title = "Trailing Zeros")
    var trailingZeros = true

    protected var df = decimalFormat(accuracy, trailingZeros)
    protected var value = 0f

    override fun initialize() {
        if (isReal) {
            addCallback("accuracy") { value: Int ->
                df = decimalFormat(value, trailingZeros)
                updateAndRecalculate()
                false
            }
            addCallback("trailingZeros") { state: Boolean ->
                df = decimalFormat(accuracy, state)
                updateAndRecalculate()
                false
            }
        }
        super.initialize()
    }

    override fun getText(): String? {
        sb.append(df.format(value))
        return null
    }

    override fun title() = title

    override fun category() = Category.INFO

    override fun id() = "${title.replace(' ', '_').lowercase()}.json"
}