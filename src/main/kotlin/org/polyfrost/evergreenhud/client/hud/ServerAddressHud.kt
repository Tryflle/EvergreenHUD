package org.polyfrost.evergreenhud.client.hud

import org.polyfrost.evergreenhud.client.ServerChangedEvent
import org.polyfrost.evergreenhud.client.utils.CachedTextHud
import org.polyfrost.oneconfig.api.config.v1.annotations.Switch
import org.polyfrost.oneconfig.api.config.v1.annotations.Text
import org.polyfrost.oneconfig.api.event.v1.eventHandler
import org.polyfrost.oneconfig.utils.v1.dsl.mc

class ServerAddressHud : CachedTextHud(
    id = "server_ip.json",
    title = "Server Address",
    category = Category.INFO,
    prefix = "IP:",
) {
    @Switch(title = "Show in Single Player")
    var showInSinglePlayer = true

    @Text(title = "No Server Text")
    var noServerText = "127.0.0.1"

    override val defaultText: String by ::noServerText

    override fun setup() {
        super.setup()
        eventHandler { (ip, _, _): ServerChangedEvent ->
            if (!showInSinglePlayer) {
                hidden = mc.hasSingleplayerServer()
            }

            updateWithText(ip)
            updateAndRecalculate()
        }

        if (isReal) {
            updateWhenChanged("showInSinglePlayer")
            updateWhenChanged("noServerText")
        }
    }
}
