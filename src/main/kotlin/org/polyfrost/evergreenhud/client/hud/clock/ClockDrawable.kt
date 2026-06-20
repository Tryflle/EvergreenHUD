package org.polyfrost.evergreenhud.client.hud.clock

import androidx.compose.runtime.Composable
import org.polyfrost.compose.composables.PolyCanvas
import org.polyfrost.compose.composables.PolyModifier
import org.polyfrost.compose.composables.size
import org.polyfrost.evergreenhud.client.utils.toPolyColor
import org.polyfrost.oneconfig.internal.ui.themes.LocalTheme
import java.lang.Math.PI
import kotlin.math.cos
import kotlin.math.sin


/*@Composable
fun Clock(
    timeMillis: Long,
    handWidth: Float = 2f,
    modifier: PolyModifier = PolyModifier
        .size(200f, 200f)
) {
    val theme = LocalTheme.current
    PolyCanvas(modifier) { _, _, width, height ->

        val radius = minOf(width, height) / 2f
        val cx = width / 2f
        val cy = height / 2f

        val secs = timeMillis / 1000.0

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
            theme.textColorSecondary.toPolyColor(),
            handWidth * 0.6f
        )

        line(
            cx,
            cy,
            cx + minuteLength * cos(minuteAngle),
            cy + minuteLength * sin(minuteAngle),
            theme.textColor.toPolyColor(),
            handWidth * 0.9f
        )

        line(
            cx,
            cy,
            cx + hourLength * cos(hourAngle),
            cy + hourLength * sin(hourAngle),
            theme.accentTextColor.toPolyColor(),
            handWidth
        )
    }
}
*/