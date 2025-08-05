package org.polyfrost.evergreenhud.client

import dev.deftu.omnicore.client.OmniClientPlayer
import org.polyfrost.evergreenhud.client.hud.*
import org.polyfrost.evergreenhud.client.hud.battery.BatteryHud
import org.polyfrost.evergreenhud.client.hud.clock.ClockHud
import org.polyfrost.evergreenhud.client.hud.hypixel.*
import org.polyfrost.evergreenhud.client.utils.FrameTimeHelper
import org.polyfrost.evergreenhud.client.utils.PinkuluMapCache
import org.polyfrost.oneconfig.api.event.v1.EventManager
import org.polyfrost.oneconfig.api.event.v1.eventHandler
import org.polyfrost.oneconfig.api.event.v1.events.TickEvent
import org.polyfrost.oneconfig.api.hud.v1.HudManager
import kotlin.jvm.optionals.getOrNull

object EvergreenHudClient {

    fun initialize() {
        FrameTimeHelper.initialize()
        PinkuluMapCache.initialize()

        HudManager.register(
            BatteryHud(), BiomeHud(), BlockAboveHud(),
            ClockHud(), /*ComboHud(),*/ CpsHud(),
            DayHud(), EntityCounterHud(), FpsHud(),
            InGameTimeHud(), /*InventoryHud(),*/ /*ItemHud(),*/
            KeyHud(), LoreHud(), MemoryHud(),
            PingHud(), PlaceCountHud(), /*PlayerPreviewHud(),*/
            PlayTimeHud(), PositionHud(), ReachHud(),
            /*ResourcePackHud(),*/ SaturationHud(), ServerAddressHud(),
            SpeedHud(), TpsHud(),

            // Hypixel HUDs
            HypixelLocationHud("Map Name") { mapName.getOrNull() },
            HypixelLocationHud("Game Type") { gameType.getOrNull()?.name },
            HypixelLocationHud("Game Mode") { mode.getOrNull() },
            HypixelLocationHud("Build Remaining") { PinkuluMapCache.getMapHeight(this).let { if (it == -1) "Unknown" else it.toString() } },
        )

        var lastPosX = 0.0
        var lastPosY = 0.0
        var lastPosZ = 0.0
        eventHandler { _: TickEvent.End ->
            val posX = OmniClientPlayer.posX
            val posY = OmniClientPlayer.posY
            val posZ = OmniClientPlayer.posZ
            if (posX != lastPosX || posY != lastPosY || posZ != lastPosZ) {
                lastPosX = posX
                lastPosY = posY
                lastPosZ = posZ
                EventManager.INSTANCE.post(BlockPositionChangedEvent(posX.toInt(), posY.toInt(), posZ.toInt()))
            }
        }
    }

}
