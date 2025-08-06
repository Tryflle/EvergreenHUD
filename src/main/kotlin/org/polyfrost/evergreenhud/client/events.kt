package org.polyfrost.evergreenhud.client

import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import org.polyfrost.oneconfig.api.event.v1.events.Event

data class ClientDamageEntityEvent(
    val attacker: Entity,
    val target: Entity
) : Event

data class ServerDamageEntityEvent(
    val attacker: Entity,
    val target: Entity
) : Event

data class ClientPlaceBlockEvent(
    val player: EntityPlayer,
    val world: World
) : Event

data class ServerChangedEvent(
    val ip: String?,
    val name: String?,
    val motd: String?
) : Event

data class SaturationChangedEvent(
    val saturation: Float
) : Event

data class SelectedItemChangedEvent(
    val item: ItemStack?
) : Event

data class BlockPositionChangedEvent(
    val x: Int,
    val y: Int,
    val z: Int
) : Event

/** This is an object so that we don't need to create a new one every frame. Slight optimization. */
data object EntityCounterEvent : Event {

    @JvmStatic
    var total = 0

    @JvmStatic
    var rendered = 0

}
