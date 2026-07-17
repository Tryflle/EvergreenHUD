package org.polyfrost.evergreenhud.client.hud

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import org.polyfrost.evergreenhud.client.utils.Facing
import org.polyfrost.evergreenhud.client.utils.GenericNumberHud
import org.polyfrost.evergreenhud.client.utils.HudStyledLines
import org.polyfrost.evergreenhud.client.utils.StyledRun
import org.polyfrost.oneconfig.api.config.v1.annotations.Checkbox
import org.polyfrost.oneconfig.api.config.v1.annotations.RadioButton
import org.polyfrost.oneconfig.api.config.v1.annotations.Switch
import org.polyfrost.oneconfig.api.event.v1.eventHandler
import org.polyfrost.oneconfig.api.event.v1.events.TickEvent
import org.polyfrost.oneconfig.api.hud.v1.Hud
import org.polyfrost.oneconfig.utils.v1.dsl.mc

private const val NO_SIGN = ' '

private const val CELL_SEP = '\u0000'

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
    var showAxis = true

    @Switch(title = "Show Direction")
    var showDirection = false

    @Switch(title = "Align Direction")
    var alignDirection = false

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

    private var linesState: MutableState<List<List<StyledRun>>> = mutableStateOf(emptyList())
    private var alignState: MutableState<Boolean> = mutableStateOf(false)

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
            updateWhenChanged("alignDirection")
            updateWhenChanged("showX")
            updateWhenChanged("showY")
            updateWhenChanged("showZ")
            updateWhenChanged("showAxis")
            updateWhenChanged("displayMode")

            hideIf("alignDirection") { !showDirection || displayMode != 0 }
        }
    }

    @Composable
    override fun Content() = HudStyledLines(linesState.value, alignColumns = alignState.value)

    override fun update(): Boolean {
        val raw = createText()
        currentText = raw.replace(CELL_SEP.toString(), "")
        val result = super.update()

        linesState.value = concat(prefix, raw, suffix).lines().map { line ->
            line.split(CELL_SEP).map { StyledRun(it, null, false, false) }
        }
        alignState.value = alignDirection && showDirection && displayMode == 0
        return result
    }

    private fun entry(axis: Char, value: Double, sign: Char): String = buildString {
        if (showAxis) {
            append(axis).append(": ")
        }

        append(format(value))
        if (showDirection && sign != NO_SIGN) {
            append(CELL_SEP).append("  (").append(sign).append(')')
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
        it.alignState = mutableStateOf(false)
    }

    override fun defaultPosition(): Pair<Float, Float> = 1f to 1f
}
