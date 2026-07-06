package org.polyfrost.evergreenhud.client.hud

import org.polyfrost.evergreenhud.client.utils.CachedTextHud
import org.polyfrost.evergreenhud.client.utils.fastRemoveIfReversed
import org.polyfrost.oneconfig.api.config.v1.annotations.RadioButton
import org.polyfrost.oneconfig.api.config.v1.annotations.Text
import org.polyfrost.oneconfig.api.event.v1.eventHandler
import org.polyfrost.oneconfig.api.event.v1.events.MouseInputEvent
import org.polyfrost.oneconfig.api.hud.v1.TextHud
import kotlin.time.Duration.Companion.milliseconds

class CpsHud : CachedTextHud(
    title = "CPS",
    category = Category.INFO,
    defaultText = "0 | 0",
) {
    @Text(title = "CPS Button Divider")
    var divider = " | "

    @RadioButton(
        title = "Button",
        options = ["Left", "Right", "Both"]
    )
    var mode = 2

    private val left: ArrayList<Long> = ArrayList(20)
    private val right: ArrayList<Long> = ArrayList(10)

    override fun setup() {
        super.setup()

        if (isReal) {
            updateWhenChanged("mode")
            updateWhenChanged("divider")
            println("SETTING UP CPS HUD!!!!!!")
            eventHandler { (btn, state): MouseInputEvent ->
                if (state == 0) {
                    when (btn) {
                        0 -> onLeftClick()
                        1 -> onRightClick()
                    }

                    updateAndRecalculate()
                }
            }
        }
    }

    private fun onLeftClick() {
        if (mode != 1) {
            left.add(System.nanoTime())
        }
    }

    private fun onRightClick() {
        if (mode > 0) {
            right.add(System.nanoTime())
        }
    }

    override fun getText(): String {
        val time = System.nanoTime()
        left.fastRemoveIfReversed { time - it > 1_000_000_000 }
        right.fastRemoveIfReversed { time - it > 1_000_000_000 }
        val nleft = left.size
        val nright = right.size
        val sb = StringBuilder()
        when (mode) {
            0 -> sb.append(nleft)
            1 -> sb.append(nright)
            2 -> sb.append(nleft).append(divider).append(nright)
        }

        return sb.toString()
    }

    override fun updateFrequency() = 100.milliseconds.inWholeNanoseconds
}
