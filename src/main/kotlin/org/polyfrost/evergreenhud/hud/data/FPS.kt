package org.polyfrost.evergreenhud.hud.data

import net.minecraft.client.Minecraft
import org.polyfrost.evergreenhud.utils.FrameTimeHelper
import org.polyfrost.oneconfig.api.config.v1.annotations.Dropdown
import org.polyfrost.oneconfig.api.config.v1.annotations.Switch
import org.polyfrost.oneconfig.api.hud.v1.TextHud
import org.polyfrost.polyui.unit.milliseconds
import kotlin.math.ceil
import kotlin.math.roundToInt

class FPS : TextHud("FPS: ") {

    @Switch(title = "Use more Accurate method")
    var fast = false

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

    override fun updateFrequency() = 200.milliseconds

    override fun getText(): String? {
        if (fast) sb.append((1000f / (average(FrameTimeHelper.frameTimes).takeUnless { it.isNaN() } ?: 1f)).roundToInt())
        else sb.append(Minecraft.getDebugFPS())
        return null
    }

    override fun id() = "evergreenhud/fps.json"

    override fun title() = "FPS"

    override fun category() = Category.INFO
}
