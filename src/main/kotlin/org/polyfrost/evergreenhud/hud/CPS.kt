package org.polyfrost.evergreenhud.hud

import org.polyfrost.oneconfig.api.config.v1.annotations.RadioButton
import org.polyfrost.oneconfig.api.config.v1.annotations.Text
import org.polyfrost.oneconfig.api.hud.v1.TextHud
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

    fun onLeftClick() {
        if (mode != 1) {
            left.add(System.nanoTime())
            update()
        }
    }

    fun onRightClick() {
        if (mode > 0) {
            right.add(System.nanoTime())
            update()
        }
    }

    override fun getText(): String {
        val cur = System.nanoTime()
        process(left, cur)
        process(right, cur)
        when (mode) {
            0 -> sb.append(left)
            1 -> sb.append(right)
            2 -> sb.append(left).append(divider).append(right)
        }
        return null
    }

    private fun process(list: ArrayList<Long>, time: Long) {
        list.fastRemoveIfReversed {
            if (time - it > 1_000_000_000) true
            else return
        }
    }

    override fun title() = "CPS"

    override fun id() = "evergreenhud/cps.json"

    override fun category() = Category.INFO
}