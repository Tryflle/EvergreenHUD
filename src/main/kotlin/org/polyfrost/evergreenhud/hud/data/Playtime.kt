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

    override fun id() = "evergreenhud/playtime.json"

    override fun category() = Category.INFO

    override fun title() = "Playtime"

    override fun getText(): String? {
        time++
        sb.append(time / 60L / 60L).append(':')
        sb.append(time / 60L)
        if (seconds) sb.append(':').append(time)
        return null
    }

    override fun updateFrequency() = 1.seconds
}