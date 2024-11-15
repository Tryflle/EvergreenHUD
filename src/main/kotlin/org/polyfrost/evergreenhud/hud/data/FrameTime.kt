package org.polyfrost.evergreenhud.hud.data

import org.polyfrost.evergreenhud.hud.GenericHUD1f
import org.polyfrost.evergreenhud.utils.FrameTimeHelper
import org.polyfrost.oneconfig.api.config.v1.annotations.Dropdown
import org.polyfrost.polyui.unit.milliseconds
import kotlin.math.ceil

class FrameTime : GenericHUD1f("Frame Time", "ms") {
    @Dropdown(
        title = "Average Method",
        options = ["Mean", "Median", "99th Percentile", "95th Percentile"]
    )
    var averageMethod = 0

    private fun average(list: List<Float>): Float = when (averageMethod) {
        0 -> list.average().toFloat()
        1 -> percentile(list, 0.5f)
        2 -> percentile(list, 0.99f)
        3 -> percentile(list, 0.95f)
        else -> 0f
    }

    private fun percentile(list: List<Float>, percentile: Float): Float {
        val index = ceil(list.size * percentile).toInt() - 1
        return list.sorted()[index]
    }

    override fun getText(): String? {
        value = average(FrameTimeHelper.frameTimes).takeUnless { it.isNaN() } ?: 1f
        return super.getText()
    }

    override fun updateFrequency() = 200.milliseconds
}