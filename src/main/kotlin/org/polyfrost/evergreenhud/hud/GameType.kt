package org.polyfrost.evergreenhud.hud

import org.polyfrost.oneconfig.api.config.v1.annotations.*
import org.polyfrost.oneconfig.hud.SingleTextHud
import org.polyfrost.oneconfig.api.hypixel.v1.*
import org.polyfrost.evergreenhud.config.HudConfig

class GameType : HudConfig("Game Type", "evergreenhud/gametype.json", false) {

    @HUD(
        name = "Main"
    )
    var hud = GameTypeHud()

    init {
        initialize()
    }

    class GameTypeHud : SingleTextHud("Game Type", true, 180, 130) {

        @Switch(
            name = "Hide If Not In-Game or Supported"
        )
        var hide = true

        override fun getText(example: Boolean): String {
            return LocrawUtil.INSTANCE.locrawInfo?.rawGameType ?: "Unknown"
        }

        override fun shouldShow(): Boolean {
            return super.shouldShow() && (!hide || LocrawUtil.INSTANCE.locrawInfo?.rawGameType?.isNotBlank() == true)
        }
    }
}