package org.polyfrost.evergreenhud.hud.data

import net.minecraft.client.Minecraft
import org.polyfrost.evergreenhud.ServerChangedEvent
import org.polyfrost.oneconfig.api.config.v1.annotations.Switch
import org.polyfrost.oneconfig.api.config.v1.annotations.Text
import org.polyfrost.oneconfig.api.event.v1.eventHandler
import org.polyfrost.oneconfig.api.hud.v1.TextHud

class ServerIP : TextHud("IP: ") {
    @Switch(title = "Show in Single Player")
    var showInSinglePlayer = true

    @Text(title = "No Server Text")
    var noServerText = "127.0.0.1"

    private var currentIP: String? = null

    override fun initialize() {
        if (isReal) {
            updateWhenChanged("showInSinglePlayer")
            updateWhenChanged("noServerText")
        }
        eventHandler { (ip, name, motd): ServerChangedEvent ->
            if (!showInSinglePlayer) hidden = Minecraft.getMinecraft().isIntegratedServerRunning
            this.currentIP = ip
            updateAndRecalculate()
        }.register()
        super.initialize()
    }

    override fun getText(): String? {
        sb.append(currentIP ?: noServerText)
        return null
    }

    override fun title() = "Server IP"

    override fun category() = Category.INFO

    override fun id() = "server_ip.json"
}