package org.polyfrost.evergreenhud

import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.World
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import org.polyfrost.evergreenhud.hud.data.*
import org.polyfrost.evergreenhud.hud.hypixel.*
import org.polyfrost.evergreenhud.hud.player.*
import org.polyfrost.evergreenhud.utils.PinkuluAPIManager
import org.polyfrost.oneconfig.api.event.v1.events.Event
import org.polyfrost.oneconfig.api.hud.v1.HudManager
import kotlin.jvm.optionals.getOrNull

@Mod(modid = "@ID@", name = "@NAME@", version = "@VER@")
class EvergreenHUD {

    @Mod.EventHandler
    fun onFMLInitialization(event: FMLInitializationEvent) {
        println("<clinit>")
        PinkuluAPIManager.initialize()
        HudManager.register(
            Biome(), CCounter(), Clock(), Day(), ECounter(), FPS(), InGameTime(), Memory(), Ping(), Playtime(), /* ResourcePack(), */ ServerIP(), TPS(), ItemHUD(),
            /* BedwarsResource(), */ LocationHUD("Map Name") { mapName.getOrNull() }, LocationHUD("Game Type") { gameType.getOrNull()?.name }, LocationHUD("Game Mode") { mode.getOrNull() }, /* HeightLimit(), */
            /* Armour(), */ BlockAbove(), Combo(), Coordinates(), CPS(), Direction(), /* HeldItemLore(), Inventory(), */ PlaceCount(), /* PlayerPreview(), */ Reach(), Speed() /* Saturation(), */
        )

    }
}

data class ClientDamageEntityEvent(val attacker: Entity, val target: Entity) : Event
data class ClientPlaceBlockEvent(val player: EntityPlayer, val world: World) : Event
data class PlayerPosEvent(val x: Double, val y: Double, val z: Double, val yaw: Float, val pitch: Float) : Event
data class ServerChangedEvent(val ip: String?, val name: String?, val motd: String?) : Event