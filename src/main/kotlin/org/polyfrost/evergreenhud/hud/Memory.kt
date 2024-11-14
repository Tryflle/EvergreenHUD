package org.polyfrost.evergreenhud.hud

import org.polyfrost.evergreenhud.utils.decimalFormat
import org.polyfrost.oneconfig.api.config.v1.annotations.RadioButton
import org.polyfrost.oneconfig.api.config.v1.annotations.Switch
import org.polyfrost.oneconfig.api.hud.v1.TextHud
import org.polyfrost.polyui.unit.seconds
import java.text.DecimalFormat

class Memory : TextHud("Memory: ", " GB") {
    @RadioButton(
        title = "Display Type",
        options = ["Absolute", "Percentage"]
    )
    var displayType = 0

    @Switch(title = "Trailing Zeros")
    var trailingZeros = false

    private var df: DecimalFormat = decimalFormat(1, trailingZeros)

    override fun initialize() {
        if (isReal) {
            addCallback("trailingZeroes") { state: Boolean ->
                df = decimalFormat(1, state, displayType == 1)
            }
            addCallback("displayType") { value: Int ->
                suffix = when (value) {
                    1 -> "%"
                    else -> " GB"
                }
                df = decimalFormat(1, trailingZeros, value == 1)
            }
        }
    }

    override fun id() = "evergreenhud/memory.json"

    override fun title() = "Memory"

    override fun category() = Category.INFO

    override fun updateFrequency() = 1.seconds

    override fun getText(): String {
        val usedBytes = bytesToMb(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())
        sb.append(
            df.format(
                when (displayType) {
                    1 -> getPercent(usedBytes, 0, bytesToMb(Runtime.getRuntime().maxMemory()))
                    else -> usedBytes / 1024f
                }
            )
        )
        return null
    }

    /**
     * Returns number between 0 - 1 depending on the range and value given
     *
     * @param num the value
     * @param min minimum of what the value can be
     * @param max maximum of what the value can be
     * @return converted percentage
     * @author isXander
     */
    private fun getPercent(num: Long, min: Long = 0L, max: Long = 100L): Long {
        return (num - min) / (max - min)
    }

    private fun bytesToMb(bytes: Long): Long {
        return bytes / 1024L / 1024L
    }
}