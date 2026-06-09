package org.polyfrost.evergreenhud.client.hud

import org.polyfrost.evergreenhud.client.utils.fastRemoveIfReversed
import org.polyfrost.oneconfig.api.config.v1.annotations.RadioButton
import org.polyfrost.oneconfig.api.config.v1.annotations.Text
import org.polyfrost.oneconfig.api.event.v1.eventHandler
import org.polyfrost.oneconfig.api.event.v1.events.MouseInputEvent
import org.polyfrost.oneconfig.api.hud.v1.TextHud
import kotlin.time.Duration.Companion.milliseconds

// CHECK OK
class CpsHud : TextHud(
    id = "cps.json",
    title = "CPS",
    category = Category.INFO,
    prefix = "CPS: ",
    suffix = ""
) {
    @Text(title = "CPS Button Divider")
    var divider = " | "

    @RadioButton(
        title = "Button",
        options = ["Left", "Right", "Both"]
    )
    var mode = 2

    private var left: ArrayList<Long>? = null
    private var right: ArrayList<Long>? = null

    override fun setup() {
        super.setup()

        if (isReal) {
            left = ArrayList(20)
            right = ArrayList(10)
            updateWhenChanged("mode")
            updateWhenChanged("divider")
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
            left?.add(System.nanoTime())
        }
    }

    private fun onRightClick() {
        if (mode > 0) {
            right?.add(System.nanoTime())
        }
    }

    override fun getText(): String? {
        val time = System.nanoTime()
        left?.fastRemoveIfReversed { time - it > 1_000_000_000 }
        right?.fastRemoveIfReversed { time - it > 1_000_000_000 }
        val nleft = left?.size ?: 0
        val nright = right?.size ?: 0
        when (mode) {
            0 -> sb.append(nleft)
            1 -> sb.append(nright)
            2 -> sb.append(nleft).append(divider).append(nright)
        }

        return null
    }

    override fun updateFrequency() = 100.milliseconds.inWholeNanoseconds
}
