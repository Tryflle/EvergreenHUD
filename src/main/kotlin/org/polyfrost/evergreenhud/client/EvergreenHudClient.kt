package org.polyfrost.evergreenhud.client

import dev.deftu.omnicore.client.OmniClient
import dev.deftu.omnicore.client.OmniClientPlayer
import net.minecraft.entity.Entity
import net.minecraft.network.play.server.S19PacketEntityStatus
import org.polyfrost.evergreenhud.client.hud.*
import org.polyfrost.evergreenhud.client.hud.battery.BatteryHud
import org.polyfrost.evergreenhud.client.hud.clock.ClockHud
import org.polyfrost.evergreenhud.client.hud.hypixel.*
import org.polyfrost.evergreenhud.client.utils.FrameTimeHelper
import org.polyfrost.evergreenhud.client.utils.PinkuluMapCache
import org.polyfrost.evergreenhud.client.utils.uniqueEntityId
import org.polyfrost.oneconfig.api.event.v1.EventManager
import org.polyfrost.oneconfig.api.event.v1.eventHandler
import org.polyfrost.oneconfig.api.event.v1.events.PacketEvent
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

        BlockPositionChangedEvent()
        ServerDamageEntityEvent()
    }

    @Suppress("FunctionName", "AssignedValueIsNeverRead")
    private fun BlockPositionChangedEvent() {
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

    @Suppress("FunctionName", "AssignedValueIsNeverRead")
    private fun ServerDamageEntityEvent() {
        var lastAttacker: Entity? = null
        var lastTargetId: Int = -1

        eventHandler { (attacker, target): ClientDamageEntityEvent ->
            lastAttacker = attacker
            lastTargetId = target.uniqueEntityId
        }

        eventHandler { (packet): PacketEvent.Receive ->
            if (packet !is S19PacketEntityStatus || packet.opCode.toInt() != 2) {
                return@eventHandler
            }

            val world = OmniClient.currentWorld ?: return@eventHandler
            val target = packet.getEntity(world.vanilla) ?: return@eventHandler
            if (lastAttacker == null || lastTargetId != target.uniqueEntityId) {
                return@eventHandler
            }

            EventManager.INSTANCE.post(ServerDamageEntityEvent(lastAttacker!!, target))
            lastAttacker = null
            lastTargetId = -1
        }
    }

}
