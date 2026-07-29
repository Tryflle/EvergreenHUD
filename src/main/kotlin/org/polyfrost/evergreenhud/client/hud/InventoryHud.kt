package org.polyfrost.evergreenhud.client.hud

import com.mojang.blaze3d.platform.InputConstants
//? if < 26
//import net.minecraft.client.gui.GuiGraphics
//? if >= 26
import net.minecraft.client.gui.GuiGraphicsExtractor as GuiGraphics
import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.ItemContainerContents
import org.polyfrost.compose.render.PolyColor
import org.polyfrost.evergreenhud.client.hooks.EnderChestTracker
import org.polyfrost.evergreenhud.client.hooks.ShulkerPreview
import org.polyfrost.evergreenhud.client.hooks.heldShulkerBox
import org.polyfrost.evergreenhud.client.hooks.shulkerContents
import org.polyfrost.oneconfig.api.config.v1.annotations.Color
import org.polyfrost.oneconfig.api.config.v1.annotations.Keybind
import org.polyfrost.oneconfig.api.config.v1.annotations.RadioButton
import org.polyfrost.oneconfig.api.config.v1.annotations.Switch
import org.polyfrost.oneconfig.api.hud.v1.HudManager
import org.polyfrost.oneconfig.api.hud.v1.LegacyHud
import org.polyfrost.oneconfig.api.ui.v1.keybind.KeybindHelper
import org.polyfrost.oneconfig.api.ui.v1.keybind.OneConfigKeybind
import org.polyfrost.oneconfig.utils.v1.dsl.mc

class InventoryHud : LegacyHud(
    id = "inventory.json",
    title = "Inventory",
    category = Category.PLAYER,
), HudBackground {
    private companion object {
        private const val PLAYER = 0
        private const val ENDER_CHEST = 1
        private const val HELD_SHULKER = 2

        private const val COLS = 9
        private const val ROWS = 3
        private const val SLOT = 18
        private const val ITEM = 16
        private const val EDGE = 8

        private val exampleShulker by lazy {
            ItemStack(Items.SHULKER_BOX).apply {
                set(
                    DataComponents.CONTAINER,
                    ItemContainerContents.fromItems(
                        listOf(
                            ItemStack(Items.DIAMOND, 32),
                            ItemStack(Items.ENCHANTED_BOOK),
                            ItemStack(Items.GOLDEN_APPLE, 8),
                            ItemStack(Items.DIAMOND_PICKAXE),
                        ),
                    ),
                )
            }
        }

        private val exampleContents by lazy {
            List(ROWS * COLS) { i ->
                when (i) {
                    0 -> ItemStack(Items.DIAMOND, 32)
                    1 -> ItemStack(Items.ENCHANTED_BOOK)
                    9 -> ItemStack(Items.GOLDEN_APPLE, 8)
                    11 -> ItemStack(Items.DIAMOND_PICKAXE)
                    else -> ItemStack.EMPTY
                }
            }
        }
    }

    @RadioButton(title = "Inventory", options = ["Player", "Ender Chest", "Held Shulker"])
    var type = PLAYER

    @Switch(title = "Show Title")
    var showTitle = true

    @Switch(title = "Background")
    override var background = true

    @Color(title = "Background Color")
    override var backgroundColor = PolyColor(0x90000000.toInt())

    @Keybind(
        title = "Pin Shulker Preview",
        description = "Held Shulker only. Keeps the shulker's contents on screen after you stop holding it. Press again to unpin.",
    )
    var pinKey: OneConfigKeybind = KeybindHelper.builder()
        .key(InputConstants.KEY_V)
        .inScreens()
        .action { pressed: Boolean -> if (pressed) ShulkerPreview.togglePin(); true }
        .register()

    private fun shulker(): ItemStack? =
        if (!isReal) exampleShulker else ShulkerPreview.pinnedStack ?: mc.player?.heldShulkerBox()

    private val visible: Boolean
        get() = type != HELD_SHULKER || HudManager.isEditing || shulker() != null

    override val width get() = if (visible) 176f else 0f
    override val height get() = if (!visible) 0f else if (showTitle) 92f else 78f

    override fun defaultPosition(): Pair<Float, Float> = 0f to 0f

    override fun setup() {
        super.setup()
        staticWidth = true
        if (isReal) {
            hideIf("backgroundColor") { !background }
        }
    }

    override fun update() = false

    private fun titleText(): String = when (type) {
        PLAYER -> "Inventory"
        ENDER_CHEST -> "Ender Chest"
        else -> shulker()?.hoverName?.string ?: "Shulker Box"
    }

    private fun contents(): List<ItemStack>? = when (type) {
        HELD_SHULKER -> shulker()?.shulkerContents()
        ENDER_CHEST -> if (!isReal) exampleContents else EnderChestTracker.contents()
        else -> {
            val inv = mc.player?.inventory
            // slot 0..8 is the hotbar, which the main inventory grid does not show
            inv?.let { List(ROWS * COLS) { i -> if (COLS + i < it.containerSize) it.getItem(COLS + i) else ItemStack.EMPTY } }
        }
    }

    override fun render(graphics: GuiGraphics) {
        if (!visible) return

        backgroundArgb?.let {
            graphics.fill(0, 0, width.toInt(), height.toInt(), it)
        }
        if (showTitle) {
            //? if < 26
            //graphics.drawString(mc.font, titleText(), EDGE, 6, 0xFFFFFFFF.toInt())
            //? if >= 26
            graphics.text(mc.font, titleText(), EDGE, 6, 0xFFFFFFFF.toInt())
        }

        val items = contents() ?: return
        val font = mc.font
        val top = if (showTitle) 20 else 6
        val slots = if (type == HELD_SHULKER && isReal && !HudManager.isEditing) ArrayList<ShulkerPreview.Slot>(items.size) else null

        for (i in items.indices) {
            val item = items[i]
            if (item.isEmpty) continue
            val itemX = EDGE + (i % COLS) * SLOT
            val itemY = top + (i / COLS) * SLOT
            //? if < 26 {
            /*graphics.renderItem(item, itemX, itemY)
            graphics.renderItemDecorations(font, item, itemX, itemY)
            *///?} else {
            graphics.item(item, itemX, itemY)
            graphics.itemDecorations(font, item, itemX, itemY)
            //?}
            slots?.add(ShulkerPreview.Slot(x + itemX * effectiveScale, y + itemY * effectiveScale, ITEM * effectiveScale, item))
        }

        slots?.let { ShulkerPreview.publishSlots(it) }
    }
}
