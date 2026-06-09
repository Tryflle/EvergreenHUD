package org.polyfrost.evergreenhud.client.hud

import net.minecraft.client.Minecraft
import org.polyfrost.evergreenhud.client.utils.pinger.ServerPinger
import org.polyfrost.evergreenhud.client.utils.pinger.ServerPingerPool
import org.polyfrost.oneconfig.api.config.v1.annotations.Slider
import org.polyfrost.oneconfig.api.config.v1.annotations.Switch
import org.polyfrost.oneconfig.api.event.v1.eventHandler
import org.polyfrost.oneconfig.api.event.v1.events.WorldEvent
import org.polyfrost.oneconfig.api.hud.v1.TextHud
import org.polyfrost.oneconfig.utils.v1.dsl.mc
import kotlin.time.Duration.Companion.seconds

class PingHud : TextHud(
    id = "ping.json",
    title = "Ping",
    category = Category.INFO,
    prefix = "Ping: ",
    suffix = "ms"
) {
    @Slider(title = "Ping Period", min = 20F, max = 120F)
    var interval = 20

    @Switch(title = "Show in Single Player")
    var showInSinglePlayer = true

    private lateinit var pinger: ServerPinger

    override fun setup() {
        super.setup()
        eventHandler { _: WorldEvent.Load ->
            hidden = !showInSinglePlayer && Minecraft.getInstance().hasSingleplayerServer()
        }

        if (isReal) {
            updateWhenChanged("showInSinglePlayer")
            pinger = ServerPingerPool.createPinger(
                intervalSupplier = { interval },
                serverSupplier = { mc.currentServer }
            )
        }
    }

    override fun updateFrequency(): Long {
        return 0.5.seconds.inWholeNanoseconds
    }

    override fun getText(): String? {
        if (isReal && ::pinger.isInitialized) {
            sb.append(pinger.ping ?: "-1 (unknown)")
        } else {
            sb.append("-1")
        }

        return null
    }
}
