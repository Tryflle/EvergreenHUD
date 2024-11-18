package org.polyfrost.evergreenhud.hud.player

import org.polyfrost.evergreenhud.hud.GenericHUD1f
import org.polyfrost.evergreenhud.utils.Facing
import org.polyfrost.oneconfig.api.config.v1.annotations.Switch

class Direction : GenericHUD1f("Direction") {
    @Switch(title = "Abbreviated")
    var abbreviated = false

    override fun getText(): String? {
        val facing = Facing.parse(value)
        sb.append(if (abbreviated) facing.abbreviated else facing.full)
        return null
    }

    override fun initialize() {
        super.initialize()
        if (isReal) {
            updateWhenChanged("abbreviated")
        }
    }
}