package org.polyfrost.evergreenhud.client.hud

import org.polyfrost.evergreenhud.client.utils.FrameTimeHelper
import org.polyfrost.evergreenhud.client.utils.GenericNumberHud
import org.polyfrost.evergreenhud.client.utils.replace
import org.polyfrost.oneconfig.api.config.v1.annotations.Slider
import org.polyfrost.oneconfig.api.config.v1.annotations.Text
import org.polyfrost.oneconfig.api.event.v1.eventHandler

class FpsHud : GenericNumberHud(
    title = "FPS",
    category = Category.INFO,
) {
    @Text(title = "Format String", description = "Use #avg for average, #med for median, #fps for fps, #p95 for 95th percentile, #p99 for 99th percentile, #cst for consistency")
    private var formatString = "#fps"

    @Slider(title = "Update Rate", description = "Seconds between display updates. 0 updates as fast as possible.", min = 0F, max = 5F, step = 0.5F)
    private var updateRate = 1F

    private var lastUpdate = 0L

    init {
        accuracy = 0
    }

    override fun setup() {
        super.setup()

        eventHandler { _: FrameTimeHelper.FrameDataEvent ->
            val now = System.nanoTime()
            if (now - lastUpdate < (updateRate * 1_000_000_000.0).toLong()) return@eventHandler
            lastUpdate = now

            val data = FrameTimeHelper.latest
            val text = StringBuilder().append(formatString)
                .replace("#fps", format(data.meanFps))
                .replace("#avg", format(data.meanFps))
                .replace("#med", format(data.medianFps))
                .replace("#p95", format(data.p95Millis))
                .replace("#p99", format(data.p99Millis))
                .replace("#cst", format(data.consistencyPercent))

            updateWithText(text)
        }

        if (isReal) {
            updateWhenChanged("formatString")
        }
    }
}
