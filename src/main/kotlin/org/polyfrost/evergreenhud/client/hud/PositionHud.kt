package org.polyfrost.evergreenhud.client.hud

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import org.polyfrost.evergreenhud.client.utils.Facing
import org.polyfrost.evergreenhud.client.utils.GenericNumberHud
import org.polyfrost.evergreenhud.client.utils.HudTextLines
import org.polyfrost.oneconfig.api.config.v1.annotations.Checkbox
import org.polyfrost.oneconfig.api.config.v1.annotations.RadioButton
import org.polyfrost.oneconfig.api.config.v1.annotations.Switch
import org.polyfrost.oneconfig.api.event.v1.eventHandler
import org.polyfrost.oneconfig.api.event.v1.events.TickEvent
import org.polyfrost.oneconfig.api.hud.v1.Hud
import org.polyfrost.oneconfig.utils.v1.dsl.mc

private const val NO_SIGN = ' '

// TODO pitch/yaw are still not shown; facing is handled by showDirection
class PositionHud : GenericNumberHud(
    title = "Position",
    category = Category.INFO,
    prefix = ""
) {
    @RadioButton(
        title = "Mode",
        options = ["Vertical", "Horizontal"]
    )
    var displayMode = 0

    @Switch(title = "Show Axis")
    var showAxis = false

    @Switch(title = "Show Direction")
    var showDirection = false

    @Checkbox(title = "Show X")
    var showX = true

    @Checkbox(title = "Show Y")
    var showY = true

    @Checkbox(title = "Show Z")
    var showZ = true

    private val facing get() = Facing.parseExact(mc.player?.yRot ?: 0f)
    private var px = 0.0
    private var py = 0.0
    private var pz = 0.0

    private var linesState: MutableState<List<String>> = mutableStateOf(emptyList())

    override fun setup() {
        super.setup()
        eventHandler { _: TickEvent.End ->
            val player = mc.player ?: return@eventHandler
            this.px = player.x
            this.py = player.y
            this.pz = player.z
            updateAndRecalculate()
        }

        if (isReal) {
            updateWhenChanged("showDirection")
            updateWhenChanged("showX")
            updateWhenChanged("showY")
            updateWhenChanged("showZ")
            updateWhenChanged("showAxis")
            updateWhenChanged("displayMode")
        }
    }

    @Composable
    override fun Content() = HudTextLines(linesState.value)

    override fun update(): Boolean {
        currentText = createText()
        val result = super.update()
        // Concatenate first so prefix and suffix land on the first and last line, the same way they
        // did when this HUD handed one \n-joined string to TextHud.
        linesState.value = concat(prefix, currentText, suffix).lines()
        return result
    }

    private fun entry(axis: Char, value: Double, sign: Char): String = buildString {
        if (showAxis) {
            append(axis).append(": ")
        }

        append(format(value))
        if (showDirection && sign != NO_SIGN) {
            append("  (").append(sign).append(')')
        }
    }

    private fun createText(): String {
        val facing = this.facing
        val entries = ArrayList<String>(3)

        if (showX) {
            entries.add(entry('X', px, if (facing.isEast) '+' else if (facing.isWest) '-' else NO_SIGN))
        }

        if (showY) {
            entries.add(entry('Y', py, NO_SIGN))
        }

        if (showZ) {
            entries.add(entry('Z', pz, if (facing.isSouth) '+' else if (facing.isNorth) '-' else NO_SIGN))
        }

        return entries.joinToString(if (displayMode == 0) "\n" else ", ")
    }

    override fun clone(): Hud = (super.clone() as PositionHud).also {
        it.linesState = mutableStateOf(emptyList())
    }

    override fun defaultPosition(): Pair<Float, Float> = 1f to 1f
}
