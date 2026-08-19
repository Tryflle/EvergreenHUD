package org.polyfrost.evergreenhud.client.hud

import org.polyfrost.compose.render.PolyColor
import org.polyfrost.evergreenhud.client.utils.FrameTimeHelper
import org.polyfrost.evergreenhud.client.utils.GenericNumberHud
import org.polyfrost.evergreenhud.client.utils.quality
import org.polyfrost.evergreenhud.client.utils.qualityColor
import org.polyfrost.evergreenhud.client.utils.replace
import org.polyfrost.oneconfig.api.config.v1.annotations.Slider
import org.polyfrost.oneconfig.api.config.v1.annotations.Switch
import org.polyfrost.oneconfig.api.config.v1.annotations.Text

private const val WORST_RATIO = 0.5

class FpsHud : GenericNumberHud(
    title = "FPS",
    category = Category.INFO,
) {
    @Text(title = "Format String", description = "Use #avg for average, #med for median, #fps for fps, #p95 for 95th percentile, #p99 for 99th percentile, #cst for consistency")
    private var formatString = "#fps"

    @Slider(title = "Update Rate", description = "Seconds between display updates. 0 updates as fast as possible.", min = 0F, max = 5F, step = 0.5F)
    private var updateRate = 1F

    @Switch(title = "Color By Value", description = "Colours the value green when FPS is near your usual, fading to red at half of it.", subcategory = "Colors")
    private var colorByValue = false

    init {
        accuracy = 0
    }

    override fun setup() {
        super.setup()

        if (isReal) {
            updateWhenChanged("formatString")
            updateWhenChanged("colorByValue")
            updateWhenChanged("updateRate")
        }
    }

    override fun getText(): String {
        val data = FrameTimeHelper.latest
        return StringBuilder().append(formatString)
            .replace("#fps", format(data.currentFps))
            .replace("#avg", format(data.averageFps))
            .replace("#med", format(data.medianFps))
            .replace("#p95", format(data.p95Millis))
            .replace("#p99", format(data.p99Millis))
            .replace("#cst", format(data.consistencyPercent))
            .toString()
    }

    override fun updateFrequency(): Long =
        if (updateRate <= 0F) -1L else (updateRate * 1_000_000_000.0).toLong()

    override fun valueColor(): PolyColor? {
        val baseline = FrameTimeHelper.baselineFps
        if (!colorByValue || baseline <= 0.0) return null
        val fps = FrameTimeHelper.latest.currentFps
        return qualityColor(quality(fps.toFloat(), (baseline * WORST_RATIO).toFloat(), baseline.toFloat()))
    }
}
