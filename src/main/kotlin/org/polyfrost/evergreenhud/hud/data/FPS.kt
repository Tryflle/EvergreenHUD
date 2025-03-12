package org.polyfrost.evergreenhud.hud.data

import org.polyfrost.evergreenhud.hud.GenericHUD1f
import org.polyfrost.evergreenhud.replace
import org.polyfrost.evergreenhud.utils.FrameTimeHelper
import org.polyfrost.oneconfig.api.config.v1.annotations.Text
import org.polyfrost.oneconfig.api.event.v1.eventHandler

// CHECK OK
class FPS : GenericHUD1f("FPS") {
    @Text(title = "Format String", description = "Use #avg for average, #med for median, #fps for fps, #p95 for 95th percentile, #p99 for 99th percentile, #cst for consistency")
    private var formatString = "#fps"

    override fun initialize() {
        FrameTimeHelper
        eventHandler { (cst, avg, med, p95, p99): FrameTimeHelper.FrameData ->
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
        if (isReal) updateWhenChanged("formatString")
        super.initialize()
    }

    override fun getText() = null

    override fun id() = "fps.json"

    override fun title() = "FPS"

    override fun category() = Category.INFO
}
