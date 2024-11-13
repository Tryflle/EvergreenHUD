package org.polyfrost.evergreenhud.hud

import org.polyfrost.evergreenhud.utils.ServerPinger
import org.polyfrost.oneconfig.api.config.v1.annotations.*
import org.polyfrost.oneconfig.hud.SingleTextHud
import org.polyfrost.universal.UMatrixStack
import org.polyfrost.oneconfig.utils.v1.dsl.mc
import org.polyfrost.evergreenhud.config.HudConfig

class Ping: HudConfig("Ping", "evergreenhud/ping.json", false) {
    @HUD(name = "Main")
    var hud = PingHud()

    init {
        initialize()
    }

    class PingHud : SingleTextHud("Ping", true, 60, 70) {

        @Slider(
            name = "Ping Period",
            min = 20F,
            max = 120F
        )
        var interval = 20

        @Switch(
            name = "Show in Single Player"
        )
        var showInSinglePlayer = true

        private val ping = ServerPinger.createListener({ interval * 20 }) { mc.currentServerData }
        override fun draw(matrices: UMatrixStack?, x: Float, y: Float, scale: Float, example: Boolean) {
            if (mc.isSingleplayer && !showInSinglePlayer && !example) return
            super.draw(matrices, x, y, scale, example)
        }

        override fun getText(example: Boolean): String {
            return ping.ping?.toString() ?: "N/A"
        }

    }
}