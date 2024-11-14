package org.polyfrost.evergreenhud.hud.player

import org.polyfrost.evergreenhud.utils.Facing
import org.polyfrost.oneconfig.api.config.v1.annotations.Switch
import org.polyfrost.oneconfig.api.hud.v1.TextHud

class Direction : TextHud("Direction: ", "evergreenhud/direction.json", false) {
    private var facing: Facing? = null

    @Switch(title = "Abbreviated")
    var abbreviated = false

    fun onChange(yaw: Float) {
        facing = Facing.parse(yaw)
    }

    override fun getText(): String {
        val facing = facing
        if (facing == null) sb.append("Unknown")
        else sb.append(if (abbreviated) facing.abbreviated else facing.full)
        return null
    }

    override fun id() = "evergreenhud/direction.json"

    override fun category() = Category.INFO

    override fun title() = "Direction"
}