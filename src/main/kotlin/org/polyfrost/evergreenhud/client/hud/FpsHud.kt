package org.polyfrost.evergreenhud.client.hud

import org.polyfrost.evergreenhud.client.utils.FrameTimeHelper
import org.polyfrost.evergreenhud.client.utils.GenericNumberHud
import org.polyfrost.evergreenhud.client.utils.replace
import org.polyfrost.oneconfig.api.config.v1.annotations.Slider
import org.polyfrost.oneconfig.api.config.v1.annotations.Text
import org.polyfrost.oneconfig.api.event.v1.eventHandler
import org.polyfrost.oneconfig.utils.v1.dsl.mc

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

        // FIXME: Remove later down the line
        if (formatString.contains("#mc")) formatString = formatString.replace("#mc", "#fps")

        eventHandler { (cst, avg, med, p95, p99): FrameTimeHelper.FrameDataEvent ->
            val now = System.nanoTime()
            if (now - lastUpdate < (updateRate * 1_000_000_000.0).toLong()) return@eventHandler
            lastUpdate = now

            val avgS = avg / 1_000_000.0
            val text = StringBuilder().append(formatString)
                .replace("#fps", format(1_000.0 / avgS))
                .replace("#avg", format(1_000.0 / avgS))
                .replace("#med", format(1_000.0 / (med / 1_000_000.0)))
                .replace("#p95", format(p95 / 1_000_000.0))
                .replace("#p99", format(p99 / 1_000_000.0))
                .replace("#cst", format((1.0 - cst) * 100.0))

            updateWithText(text)
        }

        if (isReal) {
            updateWhenChanged("formatString")
        }
    }
}
