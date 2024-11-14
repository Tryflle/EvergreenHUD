package org.polyfrost.evergreenhud.hud

import org.polyfrost.evergreenhud.utils.Facing
import org.polyfrost.evergreenhud.utils.decimalFormat
import org.polyfrost.oneconfig.api.config.v1.annotations.Checkbox
import org.polyfrost.oneconfig.api.config.v1.annotations.RadioButton
import org.polyfrost.oneconfig.api.config.v1.annotations.Slider
import org.polyfrost.oneconfig.api.config.v1.annotations.Switch
import org.polyfrost.oneconfig.api.hud.v1.TextHud

class Coordinates : TextHud("XYZ") {
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

    @Slider(
        title = "Accuracy",
        min = 0f, max = 16f,
        step = 1f
    )
    var accuracy = 0

    @Switch(title = "Trailing Zeros")
    var trailingZeros = false

    private var df = decimalFormat(accuracy, trailingZeros)
    private var facing: Facing = Facing.NORTH

    override fun initialize() {
        if (isReal) {
            addCallback("accuracy") { value: Int ->
                df = decimalFormat(value, trailingZeros)
            }
            addCallback("trailingZeros") { state: Boolean ->
                df = decimalFormat(accuracy, state)
            }
        }
    }

    fun update(x: Double, y: Double, z: Double) {
        sb.clear()
        if (showX) createString('X', x, if (facing.isEast) '+' else if (facing.isWest) '-' else ' ')
        if (showY) createString('Y', y, '\u0000')
        if (showZ) createString('Z', z, if (facing.isSouth) '+' else if (facing.isNorth) '-' else ' ')
    }

    private fun createString(axis: Char, value: Double, sign: Char) {
        if (sb.isNotEmpty()) {
            if (displayMode == 0) sb.append('\n') else sb.append(", ")
        }
        if (showAxis) sb.append(axis).append(": ")
        sb.append(df.format(value))
        if (showDirection && sign != '\u0000') sb.append('(').append(sign).append(')')
    }

    fun updateFacing(yaw: Float) {
        facing = Facing.parseExact(yaw)
    }

    override fun update() = false

    override fun id() = "evergreenhud/coordinates.json"

    override fun title() = "Coordinates"

    override fun category() = Category.INFO
}