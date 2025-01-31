package org.polyfrost.evergreenhud

import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.World
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import org.polyfrost.evergreenhud.hud.data.*
import org.polyfrost.evergreenhud.hud.hypixel.LocationHUD
import org.polyfrost.evergreenhud.hud.player.*
import org.polyfrost.evergreenhud.utils.PinkuluAPIHelper
import org.polyfrost.oneconfig.api.event.v1.events.Event
import org.polyfrost.oneconfig.api.hud.v1.HudManager
import kotlin.jvm.optionals.getOrNull

@Mod(modid = "@ID@", name = "@NAME@", version = "@VER@")
class EvergreenHUD {

    @Mod.EventHandler
    fun onFMLInitialization(event: FMLInitializationEvent) {
        HudManager.register(
            Biome(), CCounter(), Clock(), Day(), ECounter(), FPS(), InGameTime(), Memory(), Ping(), Playtime(), ResourcePack(), ServerIP(), TPS(), ItemHUD(), LoreHud(), KeyHud(),
            LocationHUD("Map Name") { mapName.getOrNull() }, LocationHUD("Game Type") { gameType.getOrNull()?.name }, LocationHUD("Game Mode") { mode.getOrNull() }, LocationHUD("Build Remaining") { PinkuluAPIHelper.getMapHeight(this).let { if (it == -1) "Unknown" else it.toString() } },
            BlockAbove(), Combo(), Coordinates(), CPS(), Direction(), Inventory(), PlaceCount(), PlayerPreview(), Reach(), Speed(), Saturation()
        )
    }
}

data class ClientDamageEntityEvent(val attacker: Entity, val target: Entity) : Event
data class ClientPlaceBlockEvent(val player: EntityPlayer, val world: World) : Event
data class PlayerPosEvent(val x: Double, val y: Double, val z: Double, val yaw: Float, val pitch: Float) : Event
data class ServerChangedEvent(val ip: String?, val name: String?, val motd: String?) : Event
data class SaturationChangedEvent(val saturation: Float) : Event
class ECounterEvent : Event {
    var total = 0
    var rendered = 0
}

val eCounter = ECounterEvent()

fun StringBuilder.replace(string: String, value: String): StringBuilder {
    val index = indexOf(string)
    if (index != -1) replace(index, index + string.length, value)
    return this
}