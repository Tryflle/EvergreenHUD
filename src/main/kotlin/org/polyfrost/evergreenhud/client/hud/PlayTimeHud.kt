package org.polyfrost.evergreenhud.client.hud

import org.polyfrost.oneconfig.api.config.v1.annotations.Switch
import org.polyfrost.oneconfig.api.hud.v1.TextHud
import org.polyfrost.polyui.unit.seconds

// CHECK OK
class PlayTimeHud : TextHud(
    id = "playtime.json",
    title = "Play Time",
    category = Category.INFO,
    prefix = "Time Played: ",
) {
    private var time: Long = 0L

    @Switch(title = "Show Seconds")
    var seconds = true

    override fun setup() {
        super.setup()
        if (isReal) {
            updateWhenChanged("seconds")
        }
    }

    override fun getText(): String? {
        time++
        val hours = time / 60L / 60L % 60L
        if (hours < 10L) {
            sb.append('0')
        }

        sb.append(hours).append(':')

        val mins = time / 60L % 60L
        if (mins < 10L) {
            sb.append('0')
        }

        sb.append(mins)
        if (seconds) {
            sb.append(':')
            val secs = time % 60L
            if (secs < 10L) sb.append('0')
            sb.append(secs)
        }

        return null
    }

    override fun updateFrequency(): Long {
        return 1.seconds
    }
}
