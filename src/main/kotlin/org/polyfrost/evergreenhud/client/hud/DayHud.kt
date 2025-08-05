package org.polyfrost.evergreenhud.client.hud

import dev.deftu.omnicore.client.OmniClient
import org.polyfrost.oneconfig.api.hud.v1.TextHud
import org.polyfrost.polyui.unit.seconds

// CHECK OK
class DayHud : TextHud(
    id = "day.json",
    title = "Day",
    category = Category.INFO,
    prefix = "Day: ",
) {

    override fun getText(): String? {
        sb.append(OmniClient.currentWorld?.worldTime?.div(24000L)?.toString() ?: "0")
        return null
    }

    override fun updateFrequency(): Long {
        return 1.seconds
    }

}
