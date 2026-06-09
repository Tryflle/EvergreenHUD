package org.polyfrost.evergreenhud.client.hud

import org.polyfrost.evergreenhud.client.utils.Facing
import org.polyfrost.evergreenhud.client.utils.GenericNumberHud
import org.polyfrost.oneconfig.api.config.v1.annotations.Checkbox
import org.polyfrost.oneconfig.api.config.v1.annotations.RadioButton
import org.polyfrost.oneconfig.api.config.v1.annotations.Switch
import org.polyfrost.oneconfig.api.event.v1.eventHandler
import org.polyfrost.oneconfig.api.event.v1.events.TickEvent
import org.polyfrost.oneconfig.utils.v1.dsl.mc

// TODO implement the facing stuff and pitch/yaw
class PositionHud : GenericNumberHud(
    title = "Position",
    category = Category.INFO
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

    private fun createString(axis: Char, value: Double, sign: Char) {
        if (sb.isNotEmpty()) {
            if (displayMode == 0) {
                sb.append('\n')
            } else {
                sb.append(", ")
            }
        }

        if (showAxis) {
            sb.append(axis).append(": ")
        }

        sb.append(df.format(value))
        if (showDirection && sign != '\u0000') {
            sb.append('(').append(sign).append(')')
        }
    }

    override fun getText(): String? {
        val facing = this.facing
        if (showX) {
            createString('X', px, if (facing.isEast) '+' else if (facing.isWest) '-' else ' ')
        }

        if (showY) {
            createString('Y', py, '\u0000')
        }

        if (showZ) {
            createString('Z', pz, if (facing.isSouth) '+' else if (facing.isNorth) '-' else ' ')
        }

        return null
    }
}
