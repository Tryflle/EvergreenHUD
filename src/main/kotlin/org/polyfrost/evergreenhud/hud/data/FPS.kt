package org.polyfrost.evergreenhud.hud.data

import org.polyfrost.evergreenhud.hud.GenericHUD1f
import org.polyfrost.evergreenhud.utils.FrameTimeHelper
import org.polyfrost.oneconfig.api.config.v1.annotations.Text
import org.polyfrost.oneconfig.api.event.v1.eventHandler

class FPS : GenericHUD1f("FPS") {
    private var event: FrameTimeHelper.FrameData? = null

    @Text(title = "Format String", description = "Use #avg for average, #med for median, ")
    private var formatString = "#avg"

    private var fpsIndex = -1
    private var avgIndex = -1
    private var medIndex = -1
    private var cstIndex = -1
    private var p99Index = -1
    private var p95Index = -1

    override fun initialize() {
        FrameTimeHelper
        eventHandler { ev: FrameTimeHelper.FrameData ->
            event = ev
            updateAndRecalculate()
        }.register()
        if (isReal) addCallback("formatString") { value: String ->
            processIndices(value)
            updateAndRecalculate()
            false
        }
        processIndices(formatString)
        super.initialize()
    }

    private fun processIndices(value: String) {
        fpsIndex = value.indexOf("#fps")
        avgIndex = value.indexOf("#avg")
        medIndex = value.indexOf("#med")
        cstIndex = value.indexOf("#cst")
        p99Index = value.indexOf("#p99")
        p95Index = value.indexOf("#p95")
    }

    override fun getText(): String? {
        val event = event
        if (event == null) {
            sb.append("???")
            return null
        }
        sb.append(formatString)
        val (cst, avg, med, p95, p99) = event
        var i = sb.length
        if (fpsIndex != -1) {
            sb.replace(i + fpsIndex, i + fpsIndex + 4, df.format((1_000_000.0 / avg)))
            i = sb.length
        }
        if (cstIndex != -1) {
            sb.replace(i + cstIndex, i + cstIndex + 4, df.format(((1 - cst) * 100.0)))
            i = sb.length
        }
        if (avgIndex != -1) {
            sb.replace(i + avgIndex, i + avgIndex + 4, df.format(avg / 1_000.0))
            i = sb.length
        }
        if (p95Index != -1) {
            sb.replace(i + p95Index, i + p95Index + 4, df.format(p95 / 1_000.0))
            i = sb.length
        }
        if (p99Index != -1) {
            sb.replace(i + p99Index, i + p99Index + 4, df.format(p99 / 1_000.0))
            i = sb.length
        }
        if (medIndex != -1) {
            sb.replace(i + medIndex, i + medIndex + 4, df.format(med / 1_000.0))
        }
        return null
    }

    override fun id() = "fps.json"

    override fun title() = "FPS"

    override fun category() = Category.INFO
}
