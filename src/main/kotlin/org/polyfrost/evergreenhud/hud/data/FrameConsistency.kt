package org.polyfrost.evergreenhud.hud.data

import org.polyfrost.evergreenhud.hud.GenericHUD1f
import org.polyfrost.evergreenhud.utils.FrameTimeHelper
import org.polyfrost.polyui.unit.milliseconds
import org.polyfrost.polyui.utils.fastEach
import kotlin.math.abs

class FrameConsistency : GenericHUD1f("Frame Consistency", "%") {
    private fun ArrayList<Float>.consistency(): Float {
        if (this.size <= 1) return 0f
        var change = 0f
        var count = 0
        var previous = 0f
        var sum = 0f
        this.fastEach {
            if (count != 0) change += abs(it - previous)
            count++
            previous = it
            sum += it
        }
        return change / count / sum
    }

    override fun getText(): String? {
        value = ((1 - FrameTimeHelper.frameTimes.consistency()) * 100)
        return super.getText()
    }

    override fun updateFrequency() = 200.milliseconds
}