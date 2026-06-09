package org.polyfrost.evergreenhud.client.hud

import org.polyfrost.oneconfig.api.hud.v1.TextHud
import org.polyfrost.oneconfig.utils.v1.dsl.mc
import kotlin.time.Duration.Companion.seconds

// CHECK OK
class DayHud : TextHud(
    id = "day.json",
    title = "Day",
    category = Category.INFO,
    prefix = "Day: ",
) {
    override fun getText(): String? {
        sb.append(mc.level?.dayTime?.div(24000L)?.toString() ?: "0")
        return null
    }

    override fun updateFrequency(): Long {
        return 1.seconds.inWholeNanoseconds
    }
}
