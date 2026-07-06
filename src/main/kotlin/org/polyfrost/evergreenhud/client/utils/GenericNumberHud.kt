package org.polyfrost.evergreenhud.client.utils

import org.polyfrost.evergreenhud.utils.decimalFormat
import org.polyfrost.oneconfig.api.config.v1.annotations.Slider
import org.polyfrost.oneconfig.api.config.v1.annotations.Switch

open class GenericNumberHud(
    title: String,
    category: Category,
    prefix: String = "$title:",
    suffix: String = "",
    id: String = "${title.replace(' ', '_').lowercase()}.json",
) : CachedTextHud(title, category, prefix, suffix, id) {

    @Slider(title = "Accuracy", min = 0F, max = 8F, step = 1F)
    var accuracy = 2

    @Switch(title = "Trailing Zeros")
    var trailingZeros = true

    private var df = decimalFormat(accuracy, trailingZeros)
    protected var value = 0f

    protected fun format(number: Number): String = df.format(number)

    protected fun updateWithNumber(number: Float) {
        value = number
        updateWithText(format(number))
    }

    override fun setup() {
        super.setup()

        if (isReal) {
            addCallback("accuracy") { value: Int ->
                df = decimalFormat(value, trailingZeros)
                updateWithNumber(this.value)
                false
            }
            addCallback("trailingZeros") { state: Boolean ->
                df = decimalFormat(accuracy, state)
                updateWithNumber(this.value)
                false
            }
        }
    }
}
