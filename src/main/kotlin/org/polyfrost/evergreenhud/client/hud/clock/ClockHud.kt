package org.polyfrost.evergreenhud.client.hud.clock

import androidx.compose.runtime.Composable
import org.polyfrost.oneconfig.api.config.v1.annotations.Slider
import org.polyfrost.oneconfig.api.hud.v1.Hud

// check OK
class ClockHud : Hud(
    id = "clock.json",
    title = "Clock",
    category = Category.INFO,
) {

    @Slider(title = "Hand Width", min = 1F, max = 10F)
    var handWidth = 2f

    @Composable
    override fun Content() {
        Clock(System.currentTimeMillis(), handWidth)
    }

    override fun update(): Boolean {
        return false
    }

}