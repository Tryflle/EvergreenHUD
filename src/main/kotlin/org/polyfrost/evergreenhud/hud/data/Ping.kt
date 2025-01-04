package org.polyfrost.evergreenhud.hud.data

import net.minecraft.client.Minecraft
import org.polyfrost.evergreenhud.utils.ServerPinger
import org.polyfrost.oneconfig.api.config.v1.annotations.Slider
import org.polyfrost.oneconfig.api.config.v1.annotations.Switch
import org.polyfrost.oneconfig.api.event.v1.eventHandler
import org.polyfrost.oneconfig.api.event.v1.events.WorldLoadEvent
import org.polyfrost.oneconfig.api.hud.v1.TextHud
import org.polyfrost.polyui.unit.seconds

class Ping : TextHud("Ping: ", "ms") {

    @Slider(title = "Ping Period", min = 20F, max = 120F)
    var interval = 20

    @Switch(title = "Show in Single Player")
    var showInSinglePlayer = true



    private val ping = ServerPinger.createListener({ interval }) { Minecraft.getMinecraft().currentServerData }

    override fun initialize() {
        if (isReal) {
            updateWhenChanged("showInSinglePlayer")
        }
        eventHandler { _: WorldLoadEvent ->
            hidden = !showInSinglePlayer && Minecraft.getMinecraft().isSingleplayer
        }.register()
        super.initialize()
    }

    override fun title() = "Ping"

    override fun id() = "ping.json"

    override fun category() = Category.INFO

    override fun updateFrequency() = 0.5.seconds

    override fun getText(): String? {
        sb.append(ping.ping?.toString() ?: "???")
        return null
    }
}