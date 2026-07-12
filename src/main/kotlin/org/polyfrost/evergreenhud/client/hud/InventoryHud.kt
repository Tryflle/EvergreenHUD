package org.polyfrost.evergreenhud.client.hud

//? if < 26
import net.minecraft.client.gui.GuiGraphics
//? if >= 26
/*import net.minecraft.client.gui.GuiGraphicsExtractor as GuiGraphics*/
import net.minecraft.world.Container
import org.polyfrost.oneconfig.api.config.v1.annotations.RadioButton
import org.polyfrost.oneconfig.api.config.v1.annotations.Switch
import org.polyfrost.oneconfig.api.hud.v1.LegacyHud
import org.polyfrost.oneconfig.utils.v1.dsl.mc

class InventoryHud : LegacyHud(
    id = "inventory.json",
    title = "Inventory",
    category = Category.PLAYER,
) {
    override val width get() = 176f
    override val height get() = if (showTitle) 92f else 78f

    @RadioButton(title = "Inventory", options = ["Player", "Ender Chest"])
    var type = 0

    @Switch(title = "Show Title")
    var showTitle = true

    override fun setup() {
        super.setup()
        staticWidth = true
    }

    override fun update() = false

    override fun hasBackground() = false

    private val container: Container?
        get() {
            val player = mc.player ?: return null
            return if (type == 0) player.inventory else player.enderChestInventory
        }

    override fun render(graphics: GuiGraphics) {
        graphics.fill(0, 0, width.toInt(), height.toInt(), 0x90000000.toInt())
        if (showTitle) {
            //? if < 26
            graphics.drawString(mc.font, if (type == 0) "Inventory" else "Ender Chest", 8, 6, 0xFFFFFFFF.toInt())
            //? if >= 26
            /*graphics.text(mc.font, if (type == 0) "Inventory" else "Ender Chest", 8, 6, 0xFFFFFFFF.toInt())*/
        }

        val inv = container ?: return
        val font = mc.font
        val base = if (type == 0) 9 else 0
        for (i in 0 until 27) {
            val slot = base + i
            if (slot >= inv.containerSize) break
            val item = inv.getItem(slot)
            if (item.isEmpty) continue
            val itemX = 8 + (i % 9) * 18
            val itemY = (if (showTitle) 20 else 6) + (i / 9) * 18
            //? if < 26 {
            graphics.renderItem(item, itemX, itemY)
            graphics.renderItemDecorations(font, item, itemX, itemY)
            //?} else {
            /*graphics.item(item, itemX, itemY)
            graphics.itemDecorations(font, item, itemX, itemY)
            *///?}
        }
    }
}
