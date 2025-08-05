package org.polyfrost.evergreenhud.client.hud

import dev.deftu.omnicore.client.OmniClient
import org.polyfrost.oneconfig.api.config.v1.annotations.Switch
import org.polyfrost.oneconfig.api.hud.v1.TextHud
import org.polyfrost.polyui.unit.milliseconds

// CHECK OK
class InGameTimeHud : TextHud(
    id = "ingametime.json",
    title = "In Game Time",
    category = Category.INFO,
    prefix = "In Game Time: ",
) {

    @Switch(title = "Twelve Hour Time")
    var twelveHour = false

    override fun setup() {
        super.setup()
        if (isReal) {
            updateWhenChanged("twelveHour")
        }
    }

    override fun getText(): String? {
        val time = ((OmniClient.currentWorld?.worldTime ?: 0L) + 6000L) % 24000L
        val seconds = (time * 3.6).toLong()
        val minutes = (seconds % 3600L) / 60L
        val hours = (seconds / 3600L) % 24L
        val realHours = if (twelveHour) {
            if (hours % 12L == 0L) 12L else hours % 12L
        } else {
            hours
        }

        if (realHours < 10L) {
            sb.append('0')
        }

        sb.append(realHours).append(':')
        if (minutes < 10L) {
            sb.append('0')
        }

        sb.append(minutes)
        if (twelveHour) {
            sb.append(if (realHours < 12L) " AM" else " PM")
        }

        return null
    }

    override fun updateFrequency(): Long {
        return 500.milliseconds
    }

}
