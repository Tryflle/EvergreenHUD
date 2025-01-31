package org.polyfrost.evergreenhud.hud.player

import org.polyfrost.evergreenhud.PlayerPosEvent
import org.polyfrost.evergreenhud.hud.GenericHUD1f
import org.polyfrost.evergreenhud.utils.Facing
import org.polyfrost.oneconfig.api.config.v1.annotations.Checkbox
import org.polyfrost.oneconfig.api.config.v1.annotations.RadioButton
import org.polyfrost.oneconfig.api.config.v1.annotations.Switch
import org.polyfrost.oneconfig.api.event.v1.eventHandler

// TODO implement the facing stuff and pitch/yaw

class Coordinates : GenericHUD1f("Coordinates") {
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

    private val facing get() = Facing.parseExact(value)
    private var x: Double = 0.0
    private var y: Double = 0.0
    private var z: Double = 0.0

    override fun initialize() {
        eventHandler { (x, y, z): PlayerPosEvent ->
            this.x = x
            this.y = y
            this.z = z
        }
        super.initialize()
        if(isReal) {
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
            if (displayMode == 0) sb.append('\n') else sb.append(", ")
        }
        if (showAxis) sb.append(axis).append(": ")
        sb.append(df.format(value))
        if (showDirection && sign != '\u0000') sb.append('(').append(sign).append(')')
    }

    override fun getText(): String? {
        val facing = this.facing
        if (showX) createString('X', x, if (facing.isEast) '+' else if (facing.isWest) '-' else ' ')
        if (showY) createString('Y', y, '\u0000')
        if (showZ) createString('Z', z, if (facing.isSouth) '+' else if (facing.isNorth) '-' else ' ')
        return null
    }
}