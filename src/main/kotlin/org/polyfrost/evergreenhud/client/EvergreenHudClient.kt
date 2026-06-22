package org.polyfrost.evergreenhud.client

import net.fabricmc.api.ClientModInitializer
import net.minecraft.core.BlockPos
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket
import net.minecraft.network.protocol.game.ClientboundSectionBlocksUpdatePacket
import net.minecraft.world.entity.Entity
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
import org.polyfrost.oneconfig.api.hud.v1.TextHud
import org.polyfrost.oneconfig.utils.v1.dsl.mc
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.jvm.optionals.getOrNull

object EvergreenHudClient : ClientModInitializer {
    override fun onInitializeClient() {
        FrameTimeHelper.initialize()
        PinkuluMapCache.initialize()
        //OmniClientResources.registerReloadListener(ResourceReloadEventReloadListener)

        val huds = arrayOf(
            BatteryHud(), BiomeHud(), BlockAboveHud(),
            ClockHud(), ComboHud(), CpsHud(),
            DayHud(), EntityCounterHud(), FpsHud(),
            InGameTimeHud(), InventoryHud(), ItemHud(),
            LoreHud(), MemoryHud(),
            PingHud(), PlaceCountHud(), PlayerPreviewHud(),
            PlayTimeHud(), PositionHud(), ReachHud(),
            /*ResourcePackHud(),*/ SaturationHud(), ServerAddressHud(),
            SpeedHud(), TpsHud(),

            // Hypixel HUDs
            HypixelLocationHud("Map Name") { mapName.getOrNull() },
            HypixelLocationHud("Game Type") { gameType.getOrNull()?.name },
            HypixelLocationHud("Game Mode") { mode.getOrNull() },
            // TODO: check that this actually works
            HypixelLocationHud("Build Remaining") { PinkuluMapCache.getMapHeight(this).let { if (it == -1) "Unknown" else it.toString() } },
        )

        // TODO: improve this workaround
        huds.forEach {
            if (it is TextHud) it.staticWidth = false
        }

        HudManager.register(*huds)

        BlockPositionChangedEvent()
        ServerDamageEntityEvent()
    }

    private val recentBlockChanges = ConcurrentLinkedQueue<BlockPos>()

    private fun BlockChangeEvent() {
        eventHandler { event: PacketEvent.Receive ->
            when (val packet = event.getPacket<Any>()) {
                is ClientboundBlockUpdatePacket -> recentBlockChanges.add(packet.pos)
                is ClientboundSectionBlocksUpdatePacket -> packet.runUpdates { pos, _ -> recentBlockChanges.add(pos) }

            }
        }
        eventHandler { _: TickEvent ->
            while (true) {
                val pos = recentBlockChanges.poll() ?: break
                EventManager.INSTANCE.post(BlockChangeEvent(pos))
            }
        }
    }

    @Suppress("FunctionName", "AssignedValueIsNeverRead")
    private fun BlockPositionChangedEvent() {
        var lastPos = BlockPos.ZERO
        eventHandler { _: TickEvent.End ->
            val player = mc.player ?: return@eventHandler
            val pos = player.blockPosition()
            if (pos != lastPos) {
                lastPos = pos
                EventManager.INSTANCE.post(BlockPositionChangedEvent(pos))
            }
        }
    }

    @Suppress("FunctionName", "AssignedValueIsNeverRead")
    private fun ServerDamageEntityEvent() {
        var lastAttacker: Entity? = null
        var lastTargetId: Int = -1

        eventHandler { (attacker, target): ClientDamageEntityEvent ->
            fun isPlayer(entity: Entity) = entity == mc.player
            println("Attacker: ${isPlayer(attacker)}, Target: $${isPlayer(target)}")
            lastAttacker = attacker
            lastTargetId = target.uniqueEntityId
        }

        eventHandler { (packet): PacketEvent.Receive ->
            if (packet !is ClientboundEntityEventPacket || packet.eventId.toInt() != 2) {
                return@eventHandler
            }

            val world = mc.level ?: return@eventHandler
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
