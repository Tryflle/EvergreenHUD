package org.polyfrost.evergreenhud.hud.player

import org.polyfrost.oneconfig.api.config.v1.annotations.RadioButton
import org.polyfrost.oneconfig.api.config.v1.annotations.Text
import org.polyfrost.oneconfig.api.event.v1.eventHandler
import org.polyfrost.oneconfig.api.event.v1.events.MouseInputEvent
import org.polyfrost.oneconfig.api.hud.v1.TextHud
import org.polyfrost.polyui.unit.milliseconds
import org.polyfrost.polyui.utils.fastRemoveIfReversed

class CPS : TextHud("CPS: ", "") {
    @Text(title = "CPS Button Divider")
    var divider = " | "

    @RadioButton(
        title = "Button",
        options = ["Left", "Right", "Both"]
    )
    var mode = 2

    private val left = ArrayList<Long>(20)
    private val right = ArrayList<Long>()

    override fun initialize() {
        eventHandler { (btn, state): MouseInputEvent ->
            if (state == 0) {
                when (btn) {
                    0 -> onLeftClick()
                    1 -> onRightClick()
                }
            }
        }.register()
        if(isReal) {
            updateWhenChanged("mode")
            updateWhenChanged("divider")
        }
        super.initialize()
    }

    private fun onLeftClick() {
        if (mode != 1) {
            left.add(System.nanoTime())
            updateAndRecalculate()
        }
    }

    private fun onRightClick() {
        if (mode > 0) {
            right.add(System.nanoTime())
            updateAndRecalculate()
        }
    }

    override fun getText(): String? {
        val cur = System.nanoTime()
        process(left, cur)
        process(right, cur)
        when (mode) {
            0 -> sb.append(left.size)
            1 -> sb.append(right.size)
            2 -> sb.append(left.size).append(divider).append(right.size)
        }
        return null
    }

    private fun process(list: ArrayList<Long>, time: Long) {
        list.fastRemoveIfReversed {
            if (time - it > 1_000_000_000) true
            else return
        }
    }

    override fun updateFrequency() = 100.milliseconds

    override fun title() = "CPS"

    override fun id() = "evergreenhud/cps.json"

    override fun category() = Category.INFO
}