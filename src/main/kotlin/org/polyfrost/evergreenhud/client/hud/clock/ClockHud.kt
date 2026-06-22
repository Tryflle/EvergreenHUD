package org.polyfrost.evergreenhud.client.hud.clock

import androidx.compose.runtime.Composable
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
        if (showBackground) {
            PolyBox(
                modifier = sizeModifier
                    .background(PolyColor(bgColor, bgChroma, bgChromaSpeed), bgRadius)
            ) {
                Clock(System.currentTimeMillis(), handWidth, sizeModifier)
            }
        } else {
            Clock(System.currentTimeMillis(), handWidth, sizeModifier)
        }
    }

    override fun update(): Boolean {
        return false
    }

}
