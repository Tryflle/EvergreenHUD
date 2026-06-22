package org.polyfrost.evergreenhud.client.hud

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import org.polyfrost.oneconfig.api.config.v1.annotations.Dropdown
import org.polyfrost.oneconfig.api.config.v1.annotations.Switch
import org.polyfrost.oneconfig.api.hud.v1.LegacyHud
import org.polyfrost.oneconfig.utils.v1.dsl.mc

class ItemHud : LegacyHud(
    id = "item_hud.json",
    title = "Item HUD",
    category = Category.INFO,
) {
    private companion object {
        private val preview by lazy { ItemStack(Items.DIAMOND_SWORD) }
    }

    @Dropdown(
        title = "Item",
        options = [
            "Feet", "Legs", "Chest", "Head", "Main Hand",
            "Iron Ingot", "Gold Ingot", "Diamond", "Emerald", "Off Hand"
        ],
    )
    var option = 4

    @Switch(title = "Show Durability & Amount")
    var showDecorations = true

    override val width get() = 16f
    override val height get() = 16f

    override fun setup() {
        super.setup()
        staticWidth = true
    }

    override fun update() = false

    override fun render(graphics: GuiGraphics) {
        graphics.fill(0, 0, width.toInt(), height.toInt(), 0x90000000.toInt())

        val item = getItem(option) ?: return
        if (item.isEmpty) return

        graphics.renderItem(item, 0, 0)
        if (showDecorations) {
            graphics.renderItemDecorations(mc.font, item, 0, 0)
        }
    }

    private fun getItem(index: Int): ItemStack? {
        val player = mc.player ?: return preview
        return when (index) {
            0 -> player.getItemBySlot(EquipmentSlot.FEET)
            1 -> player.getItemBySlot(EquipmentSlot.LEGS)
            2 -> player.getItemBySlot(EquipmentSlot.CHEST)
            3 -> player.getItemBySlot(EquipmentSlot.HEAD)
            4 -> player.getItemBySlot(EquipmentSlot.MAINHAND)
            5 -> ItemStack(Items.IRON_INGOT)
            6 -> ItemStack(Items.GOLD_INGOT)
            7 -> ItemStack(Items.DIAMOND)
            8 -> ItemStack(Items.EMERALD)
            9 -> player.getItemBySlot(EquipmentSlot.OFFHAND)
            else -> null
        }
    }
}
