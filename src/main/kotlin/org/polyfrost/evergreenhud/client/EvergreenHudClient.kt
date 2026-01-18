package org.polyfrost.evergreenhud.client

import dev.deftu.omnicore.api.client.player
import dev.deftu.omnicore.api.client.resources.OmniClientResources
import dev.deftu.omnicore.api.client.world
import dev.deftu.omnicore.api.data.vec.OmniVec3d
import dev.deftu.omnicore.api.entity.currentPos
import net.fabricmc.api.ClientModInitializer
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket
import net.minecraft.world.entity.Entity
import org.polyfrost.evergreenhud.client.hud.*
import org.polyfrost.evergreenhud.client.hud.battery.BatteryHud
import org.polyfrost.evergreenhud.client.hud.clock.ClockHud
import org.polyfrost.evergreenhud.client.hud.hypixel.*
import org.polyfrost.evergreenhud.client.utils.FrameTimeHelper
import org.polyfrost.evergreenhud.client.utils.PinkuluMapCache
import org.polyfrost.evergreenhud.client.utils.ResourceReloadEventReloadListener
import org.polyfrost.evergreenhud.client.utils.uniqueEntityId
import org.polyfrost.oneconfig.api.event.v1.EventManager
import org.polyfrost.oneconfig.api.event.v1.eventHandler
import org.polyfrost.oneconfig.api.event.v1.events.PacketEvent
import org.polyfrost.oneconfig.api.event.v1.events.TickEvent
import org.polyfrost.oneconfig.api.hud.v1.HudManager
import kotlin.jvm.optionals.getOrNull

object EvergreenHudClient : ClientModInitializer {
    override fun onInitializeClient() {
        FrameTimeHelper.initialize()
        PinkuluMapCache.initialize()
        OmniClientResources.registerReloadListener(ResourceReloadEventReloadListener)

        HudManager.register(
            BatteryHud(), /*BiomeHud(),*/ BlockAboveHud(),
            ClockHud(), ComboHud(), CpsHud(),
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
        var lastPos = OmniVec3d.ZERO
        eventHandler { _: TickEvent.End ->
            val player = player ?: return@eventHandler
            val pos = player.currentPos
            if (pos != lastPos) {
                lastPos = pos
                EventManager.INSTANCE.post(BlockPositionChangedEvent(pos.x.toInt(), pos.y.toInt(), pos.z.toInt()))
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
            if (packet !is ClientboundEntityEventPacket || packet.eventId.toInt() != 2) {
                return@eventHandler
            }

            val world = world ?: return@eventHandler
            val target = packet.getEntity(world) ?: return@eventHandler
            if (lastAttacker == null || lastTargetId != target.uniqueEntityId) {
                return@eventHandler
            }

            EventManager.INSTANCE.post(ServerDamageEntityEvent(lastAttacker!!, target))
            lastAttacker = null
            lastTargetId = -1
        }
    }
}
