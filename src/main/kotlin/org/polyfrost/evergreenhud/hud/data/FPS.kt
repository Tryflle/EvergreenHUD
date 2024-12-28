package org.polyfrost.evergreenhud.hud.data

import org.polyfrost.evergreenhud.hud.GenericHUD1f
import org.polyfrost.evergreenhud.utils.FrameTimeHelper
import org.polyfrost.oneconfig.api.config.v1.annotations.Text
import org.polyfrost.oneconfig.api.event.v1.eventHandler

class FPS : GenericHUD1f("FPS") {
    private var event: FrameTimeHelper.FrameData? = null

    @Text(title = "Format String", description = "Use #avg for average, #med for median, ")
    private var formatString = "#avg"

    override fun initialize() {
        FrameTimeHelper
        eventHandler { ev: FrameTimeHelper.FrameData ->
            event = ev
            updateAndRecalculate()
        }.register()
        if (isReal) updateWhenChanged("formatString")
        super.initialize()
    }

    override fun getText(): String? {
        val (cst, avg, med, p95, p99) = event ?: return "???"
        sb.append(formatString)
            .replace("#avg", df.format(avg / 1_000.0))
            .replace("#med", df.format(med / 1_000.0))
            .replace("#p95", df.format(p95 / 1_000.0))
            .replace("#p99", df.format(p99 / 1_000.0))
            .replace("#cst", df.format((1 - cst) * 100.0))
            .replace("#fps", df.format(1_000_000.0 / avg))
        return null
    }

    private fun StringBuilder.replace(string: String, value: String): StringBuilder {
        val index = indexOf(string)
        if (index != -1) replace(index, index + string.length, value)
        return this
    }

    override fun id() = "fps.json"

    override fun title() = "FPS"

    override fun category() = Category.INFO
}
