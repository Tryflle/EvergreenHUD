package org.polyfrost.evergreenhud.client.hud

import dev.deftu.omnicore.api.client.player
import dev.deftu.omnicore.api.direction.OmniPlanarDirection
import dev.deftu.omnicore.api.entity.currentX
import dev.deftu.omnicore.api.entity.currentY
import dev.deftu.omnicore.api.entity.currentYaw
import dev.deftu.omnicore.api.entity.currentZ
import org.polyfrost.evergreenhud.client.utils.GenericNumberHud
import org.polyfrost.oneconfig.api.config.v1.annotations.Checkbox
import org.polyfrost.oneconfig.api.config.v1.annotations.RadioButton
import org.polyfrost.oneconfig.api.config.v1.annotations.Switch
import org.polyfrost.oneconfig.api.event.v1.eventHandler
import org.polyfrost.oneconfig.api.event.v1.events.TickEvent

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

    private val facing get() = OmniPlanarDirection.from(player?.currentYaw ?: error("uh oh"), isExact = true)
    private var x = 0.0
    private var y = 0.0
    private var z = 0.0

    override fun setup() {
        super.setup()
        eventHandler { _: TickEvent.End ->
            val player = player ?: return@eventHandler
            this.x = player.currentX
            this.y = player.currentY
            this.z = player.currentZ
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
            createString('X', x, if (facing.isEast) '+' else if (facing.isWest) '-' else ' ')
        }

        if (showY) {
            createString('Y', y, '\u0000')
        }

        if (showZ) {
            createString('Z', z, if (facing.isSouth) '+' else if (facing.isNorth) '-' else ' ')
        }

        return null
    }
}
