package org.polyfrost.evergreenhud.hud

import org.polyfrost.oneconfig.api.config.v1.annotations.*
import org.polyfrost.oneconfig.hud.SingleTextHud
import org.polyfrost.universal.UMatrixStack
import org.polyfrost.oneconfig.utils.v1.dsl.mc
import org.polyfrost.evergreenhud.config.HudConfig

class ServerIP: HudConfig("Server IP", "evergreenhud/serverip.json", false) {
    @HUD(name = "Main")
    var hud = ServerIPHud()

    init {
        initialize()
    }

    class ServerIPHud: SingleTextHud("Server", true, 180, 30) {

        @Switch(
            name = "Show in Single Player"
        )
        var showInSinglePlayer = true

        @Text(name = "No Server Text")
        var noServerText = "127.0.0.1"

        override fun draw(matrices: UMatrixStack?, x: Float, y: Float, scale: Float, example: Boolean) {
            if (mc.currentServerData == null && !showInSinglePlayer && !example) return
            super.draw(matrices, x, y, scale, example)
        }

        override fun getText(example: Boolean): String {
            return mc.currentServerData?.serverIP ?: noServerText
        }

    }
}