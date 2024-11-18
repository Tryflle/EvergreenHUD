package org.polyfrost.evergreenhud.hud.data

import net.minecraft.client.Minecraft
import org.polyfrost.oneconfig.api.config.v1.annotations.Switch
import org.polyfrost.oneconfig.api.hud.v1.TextHud
import org.polyfrost.polyui.unit.seconds
import java.text.SimpleDateFormat
import java.util.Date

class InGameTime : TextHud("Ingame Time: ") {
    @Switch(title = "Twelve Hour Time")
    var twelveHour = false

    private var sdf = SimpleDateFormat(if (twelveHour) "hh:mm a" else "HH:mm")

    override fun initialize() {
        if (isReal) {
            addCallback("twelveHour") { state: Boolean ->
                sdf = SimpleDateFormat(if (state) "hh:mm a" else "HH:mm")
                updateAndRecalculate()
                false
            }
        }
        super.initialize()
    }

    override fun getText(): String? {
        val world = Minecraft.getMinecraft().theWorld ?: return "06:00 AM"
        // ticks to ticks in day to seconds to millis plus six hours (time 0 = 6am)
        val date = Date(world.worldTime / 20 * 1000 + 21_600_000) // 6 hours == 21,600,000 milliseconds
        sb.append(sdf.format(date).uppercase())
        return null
    }

    override fun updateFrequency() = 1.seconds

    override fun title() = "In Game Time"

    override fun id() = "evergreenhud/ingametime.json"

    override fun category() = Category.INFO
}