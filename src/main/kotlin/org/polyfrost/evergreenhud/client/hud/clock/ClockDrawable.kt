package org.polyfrost.evergreenhud.client.hud.clock

import org.polyfrost.polyui.color.Colors
import org.polyfrost.polyui.component.Drawable
import org.polyfrost.polyui.unit.Vec2
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class ClockDrawable(
    timeMillis: Long,
    radius: Float,
    var handWidth: Float = 2f,
    at: Vec2 = Vec2.Constants.ZERO,
    visibleSize: Vec2 = Vec2.Constants.ZERO,
    palette: Colors.Palette? = null
) : Drawable(
    at = at,
    size = Vec2(radius * 2f, radius * 2f),
    visibleSize = visibleSize,
    palette = palette
) {

    var time: Long = timeMillis

    override fun preRender(delta: Long) {
        time += delta / 1_000_000L
        super.preRender(delta)
    }

    override fun render() {
        val radius = width / 2f
        val cx = x + radius
        val cy = y + radius
        val secs = time / 1000.0
        val sA = (2 * PI * (secs % 60.0 / 60.0) - PI / 2).toFloat()
        val mA = (2 * PI * ((secs / 60.0) % 60.0 / 60.0) - PI / 2).toFloat()
        val hA = (2 * PI * ((secs / 3600.0) % 12.0 / 12.0) - PI / 2).toFloat()
        val sL = radius * 0.9f
        val mL = radius * 0.75f
        val hL = radius * 0.4f
        val bars = polyUI.colors.text
        renderer.line(cx, cy, cx + sL * cos(sA), cy + sL * sin(sA), bars.secondary.normal, handWidth * 0.6f)
        renderer.line(cx, cy, cx + mL * cos(mA), cy + mL * sin(mA), bars.primary.hovered, handWidth * 0.9f)
        renderer.line(cx, cy, cx + hL * cos(hA), cy + hL * sin(hA), bars.primary.normal, handWidth)
    }

}
