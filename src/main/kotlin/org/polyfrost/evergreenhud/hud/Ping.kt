package org.polyfrost.evergreenhud.hud

import net.minecraft.client.Minecraft
import org.polyfrost.evergreenhud.utils.ServerPinger
import org.polyfrost.oneconfig.api.config.v1.annotations.Slider
import org.polyfrost.oneconfig.api.config.v1.annotations.Switch
import org.polyfrost.oneconfig.api.hud.v1.TextHud

class Ping : TextHud("Ping: ", "ms") {

    @Slider(title = "Ping Period", min = 20F, max = 120F)
    var interval = 20

    @Switch(title = "Show in Single Player")
    var showInSinglePlayer = true

    private val ping = ServerPinger.createListener({ interval * 20 }) { Minecraft.getMinecraft().currentServerData }

    override fun title() = "Ping"

    override fun id() = "evergreenhud/ping.json"

    override fun category() = Category.INFO

    override fun getText(): String {
        sb.append(ping.ping?.toString() ?: "???")
        return null
    }
}