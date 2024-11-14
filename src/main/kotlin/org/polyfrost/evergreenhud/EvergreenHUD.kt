package org.polyfrost.evergreenhud

import org.polyfrost.evergreenhud.utils.PinkuluAPIManager
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.World
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import org.polyfrost.oneconfig.api.event.v1.events.Event

@Mod(modid = "@ID@", name = "@NAME@", version = "@VER@")
class EvergreenHUD {

    @Mod.EventHandler
    fun onFMLInitialization(event: FMLInitializationEvent) {
        PinkuluAPIManager.initialize()
    }
}

data class ClientDamageEntityEvent(val attacker: Entity, val target: Entity) : Event
class ClientPlaceBlockEvent(val player: EntityPlayer, val world: World) : Event
