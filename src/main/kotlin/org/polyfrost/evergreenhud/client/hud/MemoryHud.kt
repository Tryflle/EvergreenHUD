package org.polyfrost.evergreenhud.client.hud

import org.polyfrost.evergreenhud.client.utils.CachedTextHud
import org.polyfrost.evergreenhud.utils.decimalFormat
import org.polyfrost.oneconfig.api.config.v1.annotations.Number
import org.polyfrost.oneconfig.api.config.v1.annotations.RadioButton
import org.polyfrost.oneconfig.api.config.v1.annotations.Switch
import org.polyfrost.oneconfig.api.hud.v1.TextHud
import java.text.DecimalFormat
import kotlin.time.Duration.Companion.seconds

class MemoryHud : CachedTextHud(
    title = "Memory",
    category = Category.INFO,
    suffix = "GB"
) {
    @RadioButton(
        title = "Display Type",
        options = ["Absolute", "Percentage"]
    )
    var displayType = 0

    @Switch(title = "Trailing Zeros")
    var trailingZeros = false

    @Number(title = "Update Frequency (requires restart)", min = 0.5F, max = 5F)
    var updateFrequency = 0.5F

    private var df: DecimalFormat = decimalFormat(1, trailingZeros)

    override fun setup() {
        super.setup()
        if (isReal) {
            addCallback("trailingZeros") { state: Boolean ->
                df = decimalFormat(1, state, displayType == 1)
                updateAndRecalculate()
                false
            }

            addCallback("displayType") { value: Int ->
                suffix = when (value) {
                    1 -> "%"
                    else -> " GB"
                }

                df = decimalFormat(1, trailingZeros, value == 1)
                updateAndRecalculate()
                false
            }
        }
    }

    override fun updateFrequency(): Long {
        return updateFrequency.toDouble().seconds.inWholeNanoseconds
    }

    override fun getText(): String {
        val r = Runtime.getRuntime()
        val usedBytes = bytesToMb(r.totalMemory() - r.freeMemory())
        return df.format(when (displayType) {
            1 -> usedBytes.toDouble() / bytesToMb(r.maxMemory()).toDouble()
            else -> usedBytes / 1024.0
        }).trim()
    }

    private fun bytesToMb(bytes: Long): Long {
        return bytes / 1024L / 1024L
    }
}
