package org.polyfrost.evergreenhud.client.hud.clock

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.withFrameMillis
import org.polyfrost.compose.composables.PolyBox
import org.polyfrost.compose.composables.PolyModifier
import org.polyfrost.compose.composables.background
import org.polyfrost.compose.composables.size
import org.polyfrost.compose.render.PolyColor
import org.polyfrost.oneconfig.api.config.v1.annotations.Slider
import org.polyfrost.oneconfig.api.hud.v1.Hud

class ClockHud : Hud(
    id = "clock.json",
    title = "Clock",
    category = Category.INFO,
) {

    @Slider(title = "Hand Width", min = 1F, max = 10F, step = 1F)
    var handWidth = 2f

    override fun setup() {
        super.setup()
        staticWidth = true
        if (staticW < 16f) staticW = 64f
        if (staticH < 16f) staticH = 64f
    }

    override fun minimumSize(): Pair<Float, Float> = 16f to 16f

    @Composable
    override fun Content() {
        val sizeModifier = PolyModifier.size(scaledWidth, scaledHeight)
        val timeState = remember { mutableLongStateOf(System.currentTimeMillis()) }
        LaunchedEffect(Unit) {
            while (true) {
                withFrameMillis { timeState.longValue = System.currentTimeMillis() }
            }
        }
        val timeMillis by timeState
        if (showBackground) {
            PolyBox(
                modifier = sizeModifier
                    .background(PolyColor(bgColor, bgChroma, bgChromaSpeed), bgRadius)
            ) {
                Clock(timeMillis, handWidth, sizeModifier)
            }
        } else {
            Clock(timeMillis, handWidth, sizeModifier)
        }
    }

    override fun update(): Boolean {
        return false
    }
}
