package org.polyfrost.evergreenhud.hud

import org.polyfrost.evergreenhud.utils.PinkuluAPIManager
import org.polyfrost.oneconfig.api.config.v1.annotations.*
import org.polyfrost.oneconfig.hud.SingleTextHud
import org.polyfrost.oneconfig.utils.v1.dsl.mc
import org.polyfrost.oneconfig.api.hypixel.v1.*
import org.polyfrost.evergreenhud.config.HudConfig

class HeightLimit : HudConfig("Height Limit", "evergreenhud/heightlimit.json", false) {

    @HUD(
        name = "Main"
    )
    var hud = HeightLimitHud()

    init {
        initialize()
    }

    class HeightLimitHud : SingleTextHud("Height Limit", true, 180, 150) {

        @Switch(
            name = "Hide If Not In-Game or Supported"
        )
        var hide = true

        @Switch(
            name = "Show Distance To Limit"
        )
        var showDistance = false

        override fun getText(example: Boolean): String {
            return PinkuluAPIManager.getMapHeight()?.let { if (showDistance) it - mc.thePlayer.position.y else it }?.toString() ?: "Unknown"
        }

        override fun shouldShow(): Boolean {
            return super.shouldShow() && (!hide || (LocrawUtil.INSTANCE.locrawInfo?.mapName?.isNotBlank() == true && PinkuluAPIManager.getMapHeight() != null)) && mc.thePlayer != null && mc.theWorld != null
        }
    }
}