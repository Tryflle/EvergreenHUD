package org.polyfrost.evergreenhud.client.hud

//? if >= 1.21.10 {
import net.minecraft.client.input.KeyEvent
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.client.input.MouseButtonInfo
//?}
import net.minecraft.client.KeyMapping
import org.polyfrost.evergreenhud.client.utils.CachedTextHud
import org.polyfrost.evergreenhud.client.utils.fastRemoveIfReversed
import org.polyfrost.oneconfig.api.config.v1.annotations.RadioButton
import org.polyfrost.oneconfig.api.config.v1.annotations.Slider
import org.polyfrost.oneconfig.api.config.v1.annotations.Text
import org.polyfrost.oneconfig.api.event.v1.eventHandler
import org.polyfrost.oneconfig.api.event.v1.events.KeyInputEvent
import org.polyfrost.oneconfig.api.event.v1.events.MouseInputEvent
import org.polyfrost.oneconfig.api.hud.v1.HudManager
import org.polyfrost.oneconfig.utils.v1.dsl.mc
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

    @Slider(
        title = "Hide After (s)",
        description = "Hide the HUD once you haven't clicked for this many seconds. 0 keeps it always shown.",
        min = 0F, max = 30F, step = 1F
    )
    var hideAfter = 0f

    private val left: ArrayList<Long> = ArrayList(20)
    private val right: ArrayList<Long> = ArrayList(10)

    private var lastClick = System.nanoTime()

    private val hideAfterNanos get() = (hideAfter * 1_000_000_000.0).toLong()

    override fun setup() {
        super.setup()

        if (isReal) {
            updateWhenChanged("mode")
            updateWhenChanged("divider")
            addCallback("hideAfter") {
                lastClick = System.nanoTime()
                hidden = false
                updateAndRecalculate()
            }
            eventHandler { (btn, state): MouseInputEvent ->
                if (state == 0) {
                    val options = mc.options ?: return@eventHandler
                    var counted = false
                    if (options.keyAttack.matchesMouseButton(btn)) { onLeftClick(); counted = true }
                    if (options.keyUse.matchesMouseButton(btn)) { onRightClick(); counted = true }
                    if (counted) updateAndRecalculate()
                }
            }
            eventHandler { (key, _, state): KeyInputEvent ->
                if (state == 1 && key != 0) {
                    val options = mc.options ?: return@eventHandler
                    var counted = false
                    if (options.keyAttack.matchesKeyCode(key)) { onLeftClick(); counted = true }
                    if (options.keyUse.matchesKeyCode(key)) { onRightClick(); counted = true }
                    if (counted) updateAndRecalculate()
                }
            }
        }
    }

    private fun onLeftClick() {
        if (mode != 1) {
            left.add(System.nanoTime())
            registerClick()
        }
    }

    private fun onRightClick() {
        if (mode > 0) {
            right.add(System.nanoTime())
            registerClick()
        }
    }

    private fun registerClick() {
        lastClick = System.nanoTime()
        hidden = false
    }

    private fun KeyMapping.matchesMouseButton(button: Int): Boolean {
        //? if >= 1.21.10 {
        return matchesMouse(MouseButtonEvent(0.0, 0.0, MouseButtonInfo(button, 0)))
        //?} else {
        /*return matchesMouse(button)
        *///?}
    }

    private fun KeyMapping.matchesKeyCode(keyCode: Int): Boolean {
        //? if >= 1.21.10 {
        return matches(KeyEvent(keyCode, 0, 0))
        //?} else {
        /*return matches(keyCode, 0)
        *///?}
    }

    override fun getText(): String {
        val time = System.nanoTime()
        val nanos = hideAfterNanos
        if (nanos > 0L && !HudManager.isEditing && time - lastClick > nanos) {
            hidden = true
        }
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
