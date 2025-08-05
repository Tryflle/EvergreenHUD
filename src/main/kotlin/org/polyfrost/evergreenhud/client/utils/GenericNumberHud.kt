package org.polyfrost.evergreenhud.client.utils

import org.polyfrost.evergreenhud.utils.decimalFormat
import org.polyfrost.oneconfig.api.config.v1.annotations.Slider
import org.polyfrost.oneconfig.api.config.v1.annotations.Switch
import org.polyfrost.oneconfig.api.hud.v1.TextHud

open class GenericNumberHud(
    private var title: String,
    category: Category,
    prefix: String = "$title: ",
    suffix: String = "",
) : TextHud(
    id = "${title.replace(' ', '_').lowercase()}.json",
    title = title,
    category = category,
    prefix = prefix,
    suffix = suffix
) {

    @Slider(title = "Accuracy", min = 0F, max = 8F)
    var accuracy = 2

    @Switch(title = "Trailing Zeros")
    var trailingZeros = true

    protected var df = decimalFormat(accuracy, trailingZeros)
    protected var value = 0f

    override fun setup() {
        super.setup()

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
    }

    override fun getText(): String? {
        sb.append(df.format(value))
        return null // Not needed. We use `sb` for better performance
    }

}
