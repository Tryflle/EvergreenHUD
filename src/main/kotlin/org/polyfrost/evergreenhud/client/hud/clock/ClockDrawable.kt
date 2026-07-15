package org.polyfrost.evergreenhud.client.hud.clock

import androidx.compose.runtime.Composable
import org.polyfrost.compose.composables.PolyCanvas
import org.polyfrost.compose.composables.PolyModifier
import org.polyfrost.compose.composables.size
import org.polyfrost.compose.render.PolyColor
import java.lang.Math.PI
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin

private val SECOND_HAND = PolyColor.rgba(170, 170, 170, 255)
private val MINUTE_HAND = PolyColor.rgba(255, 255, 255, 255)
private val HOUR_HAND = PolyColor.rgba(120, 180, 255, 255)

@Composable
fun Clock(
    timeMillis: Long,
    handWidth: Float = 2f,
    ticking: Boolean = false,
    modifier: PolyModifier = PolyModifier
        .size(200f, 200f)
) {
    PolyCanvas(modifier) { _, _, width, height ->

        val radius = minOf(width, height) / 2f
        val cx = width / 2f
        val cy = height / 2f

        val secs = if (ticking) floor(timeMillis / 1000.0) else timeMillis / 1000.0

        val secondAngle = (2 * PI * (secs % 60.0 / 60.0) - PI / 2).toFloat()

        val minuteAngle = (2 * PI * ((secs / 60.0) % 60.0 / 60.0) - PI / 2).toFloat()

        val hourAngle = (2 * PI * ((secs / 3600.0) % 12.0 / 12.0) - PI / 2).toFloat()

        val secondLength = radius * 0.9f
        val minuteLength = radius * 0.75f
        val hourLength = radius * 0.4f

        line(
            cx,
            cy,
            cx + secondLength * cos(secondAngle),
            cy + secondLength * sin(secondAngle),
            SECOND_HAND,
            handWidth * 0.6f
        )

        line(
            cx,
            cy,
            cx + minuteLength * cos(minuteAngle),
            cy + minuteLength * sin(minuteAngle),
            MINUTE_HAND,
            handWidth * 0.9f
        )

        line(
            cx,
            cy,
            cx + hourLength * cos(hourAngle),
            cy + hourLength * sin(hourAngle),
            HOUR_HAND,
            handWidth
        )
    }
}
