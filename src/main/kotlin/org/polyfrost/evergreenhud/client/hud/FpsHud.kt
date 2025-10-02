package org.polyfrost.evergreenhud.client.hud

import org.polyfrost.evergreenhud.client.utils.FrameTimeHelper
import org.polyfrost.evergreenhud.client.utils.GenericNumberHud
import org.polyfrost.evergreenhud.client.utils.replace
import org.polyfrost.oneconfig.api.config.v1.annotations.Text
import org.polyfrost.oneconfig.api.event.v1.eventHandler

// CHECK OK
class FpsHud : GenericNumberHud(
    title = "FPS",
    category = Category.INFO,
) {
    @Text(title = "Format String", description = "Use #avg for average, #med for median, #fps for fps, #p95 for 95th percentile, #p99 for 99th percentile, #cst for consistency")
    private var formatString = "#fps"

    override fun setup() {
        super.setup()
        eventHandler { (cst, avg, med, p95, p99): FrameTimeHelper.FrameDataEvent ->
            val avgS = avg / 1_000_000.0
            sb.append(formatString)
                .replace("#fps", df.format(1_000.0 / avgS))
                .replace("#avg", df.format(avgS))
                .replace("#med", df.format(med / 1_000_000.0))
                .replace("#p95", df.format(p95 / 1_000_000.0))
                .replace("#p99", df.format(p99 / 1_000_000.0))
                .replace("#cst", df.format((1.0 - cst) * 100.0))
            updateAndRecalculate()
        }

        if (isReal) {
            updateWhenChanged("formatString")
        }
    }

    override fun getText(): String? {
        return null
    }
}
