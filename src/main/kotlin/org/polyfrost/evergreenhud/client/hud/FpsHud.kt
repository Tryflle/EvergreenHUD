package org.polyfrost.evergreenhud.client.hud

import org.polyfrost.evergreenhud.client.utils.FrameTimeHelper
import org.polyfrost.evergreenhud.client.utils.GenericNumberHud
import org.polyfrost.evergreenhud.client.utils.replace
import org.polyfrost.oneconfig.api.config.v1.annotations.Text
import org.polyfrost.oneconfig.api.event.v1.eventHandler
import org.polyfrost.oneconfig.utils.v1.dsl.mc

class FpsHud : GenericNumberHud(
    title = "FPS",
    category = Category.INFO,
) {
    @Text(title = "Format String", description = "Use #avg for average, #med for median, #fps for fps, #p95 for 95th percentile, #p99 for 99th percentile, #cst for consistency, #mc for the vanilla counter")
    private var formatString = "#mc"

    init {
        accuracy = 0
    }

    override fun setup() {
        super.setup()
        eventHandler { (cst, avg, med, p95, p99): FrameTimeHelper.FrameDataEvent ->
            val avgS = avg / 1_000_000.0
            val text = StringBuilder().append(formatString)
                .replace("#fps", format(1_000.0 / avgS))
                .replace("#avg", format(avgS))
                .replace("#med", format(med / 1_000_000.0))
                .replace("#p95", format(p95 / 1_000_000.0))
                .replace("#p99", format(p99 / 1_000_000.0))
                .replace("#cst", format((1.0 - cst) * 100.0))
                .replace("#mc", format(mc.fps))

            updateWithText(text)
        }

        if (isReal) {
            updateWhenChanged("formatString")
        }
    }
}
