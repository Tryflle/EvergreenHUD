package org.polyfrost.evergreenhud.client.hooks

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.world.inventory.ChestMenu
import net.minecraft.world.item.ItemStack
import org.polyfrost.oneconfig.api.event.v1.eventHandler
import org.polyfrost.oneconfig.api.event.v1.events.TickEvent
import org.polyfrost.oneconfig.utils.v1.dsl.mc

const val ENDER_CHEST_SLOTS = 27

object EnderChestTracker {
    private val enderChestTitle = Component.translatable("container.enderchest").contents

    private var contents: List<ItemStack> = emptyList()

    fun initialize() {
        eventHandler { _: TickEvent.End ->
            if (mc.player == null) {
                contents = emptyList()
            } else {
                capture()
            }
        }
    }

    fun contents(): List<ItemStack>? = contents.takeIf { it.isNotEmpty() }

    private fun capture() {
        //? if < 26.2
        val screen = mc.screen as? AbstractContainerScreen<*> ?: return
        //? if >= 26.2
        //val screen = mc.gui.screen() as? AbstractContainerScreen<*> ?: return
        val menu = screen.menu as? ChestMenu ?: return
        // The title is the only thing separating an ender chest from any other 3-row chest, since both
        // arrive as a plain GENERIC_9x3 menu. EnderChestBlockEntity cannot be renamed, so the key holds.
        if (menu.rowCount != 3 || screen.title.contents != enderChestTitle) return

        val container = menu.container
        if (container.containerSize != ENDER_CHEST_SLOTS) return
        // copied: the menu's container is discarded and its stacks mutated once the screen closes
        contents = List(ENDER_CHEST_SLOTS) { container.getItem(it).copy() }
    }
}
