package org.polyfrost.evergreenhud.client.hud.clock

import org.polyfrost.oneconfig.api.config.v1.annotations.Slider
import org.polyfrost.oneconfig.api.hud.v1.Hud
import org.polyfrost.polyui.color.Colors
import org.polyfrost.polyui.component.Drawable
import org.polyfrost.polyui.unit.Vec2
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

// check OK
class ClockHud : Hud<Drawable>(
    id = "clock.json",
    title = "Clock",
    category = Category.INFO,
) {

    @Slider(title = "Hand Width", min = 1F, max = 10F)
    var handWidth = 2f

    override fun create(): Drawable {
        return ClockDrawable(System.currentTimeMillis(), 100f, handWidth)
    }

    override fun update(): Boolean {
        return false
    }

}