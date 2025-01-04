package org.polyfrost.evergreenhud.hud.data

import org.polyfrost.oneconfig.api.config.v1.annotations.Switch
import org.polyfrost.oneconfig.api.hud.v1.TextHud
import org.polyfrost.polyui.unit.seconds

class Playtime : TextHud("Time Played: ") {
    @Switch(title = "Show Seconds")
    var seconds = true

    private var time: Long = 0L

    override fun initialize() {
        if (isReal) {
            updateWhenChanged("seconds")
        }
        super.initialize()
    }

    override fun id() = "playtime.json"

    override fun category() = Category.INFO

    override fun title() = "Playtime"

    override fun getText(): String? {
        time++
        val hours = time / 60L / 60L % 60L
        if (hours < 10L) sb.append('0')
        sb.append(hours).append(':')

        val mins = time / 60L % 60L
        if (mins < 10L) sb.append('0')
        sb.append(mins)

        if (seconds) {
            sb.append(':')
            val secs = time % 60L
            if (secs < 10L) sb.append('0')
            sb.append(secs)
        }
        return null
    }

    override fun updateFrequency() = 1.seconds
}