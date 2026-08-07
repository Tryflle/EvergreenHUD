package org.polyfrost.evergreenhud.client.hud

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.ItemContainerContents
import org.polyfrost.compose.composables.PolyBox
import org.polyfrost.compose.composables.PolyMcText
import org.polyfrost.compose.composables.PolyModifier
import org.polyfrost.compose.composables.PolyText
import org.polyfrost.compose.composables.absoluteAt
import org.polyfrost.compose.composables.background
import org.polyfrost.compose.composables.size
import org.polyfrost.compose.render.PolyColor
import org.polyfrost.evergreenhud.client.hooks.EnderChestTracker
import org.polyfrost.evergreenhud.client.hooks.ShulkerPreview
import org.polyfrost.evergreenhud.client.hooks.heldShulkerBox
import org.polyfrost.evergreenhud.client.hooks.shulkerContents
import org.polyfrost.evergreenhud.client.hud.item.ITEM_SIZE
import org.polyfrost.evergreenhud.client.hud.item.ItemIcon
import org.polyfrost.evergreenhud.client.hud.item.whenItemsReady
import org.polyfrost.oneconfig.api.config.v1.annotations.Color
import org.polyfrost.oneconfig.api.config.v1.annotations.Keybind
import org.polyfrost.oneconfig.api.config.v1.annotations.RadioButton
import org.polyfrost.oneconfig.api.config.v1.annotations.Slider
import org.polyfrost.oneconfig.api.config.v1.annotations.Switch
import org.polyfrost.oneconfig.api.hud.v1.Font
import org.polyfrost.oneconfig.api.hud.v1.Hud
import org.polyfrost.oneconfig.api.hud.v1.HudManager
import org.polyfrost.oneconfig.api.ui.v1.keybind.KeybindHelper
import org.polyfrost.oneconfig.api.ui.v1.keybind.OneConfigKeybind
import org.polyfrost.oneconfig.utils.v1.dsl.mc

private const val PLAYER = 0
private const val ENDER_CHEST = 1
private const val HELD_SHULKER = 2

private const val COLS = 9
private const val ROWS = 3
private const val SLOT = 18f
private const val SLOT_BOUND_GAP = 2f
private const val SLOT_BOUND_SIZE = SLOT - SLOT_BOUND_GAP
private const val EDGE = 8f
private const val TITLE_Y = 6f
private const val FONT_SIZE = 8f

/** vanilla inventory panel metrics so the grid keeps its proportions */
private const val WIDTH = 176f
private const val GRID_HEIGHT = ROWS * SLOT
private const val GRID_TOP = 6f
private const val GRID_TOP_TITLED = 20f

/** mirrors [GRID_TOP] because the extra vanilla room was the hotbar row we do not draw */
private const val GRID_BOTTOM = GRID_TOP

class InventoryHud : Hud(
    id = "inventory.json",
    title = "Inventory",
    category = Category.PLAYER,
) {
    private companion object {
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

    @Switch(title = "Slot Bounds", description = "Draw a background behind every slot in the grid.")
    var slotBounds = false

    @Color(title = "Slot Bounds Color")
    var slotBoundsColor = PolyColor(0x60000000)

    @Slider(title = "Slot Bounds Radius", min = 0F, max = 8F, step = 1F)
    var slotBoundsRadius = 0f

    @Keybind(
        title = "Pin Shulker Preview",
        description = "Held Shulker only. Keeps the shulker's contents on screen after you stop holding it. Press again to unpin.",
    )
    var pinKey: OneConfigKeybind = KeybindHelper.builder()
        .key(InputConstants.KEY_V)
        .inScreens()
        .action { pressed: Boolean -> if (pressed) ShulkerPreview.togglePin(); true }
        .register()

    private class Grid(val title: String?, val items: List<ItemStack>)

    private var grid = mutableStateOf<Grid?>(null)

    private fun shulker(): ItemStack? =
        if (!isReal) exampleShulker else ShulkerPreview.pinnedStack ?: mc.player?.heldShulkerBox()

    private val visible: Boolean
        get() = type != HELD_SHULKER || HudManager.isEditing || shulker() != null

    override fun defaultPosition(): Pair<Float, Float> = 0f to 0f

    override fun canMergeBackground(): Boolean = true

    override fun minimumSize(): Pair<Float, Float> = WIDTH to gridTop() + GRID_HEIGHT + GRID_BOTTOM

    override fun setup() {
        staticWidth = true
        staticW = WIDTH
        staticH = gridTop() + GRID_HEIGHT + GRID_BOTTOM
        if (isReal) {
            hideIf("slotBoundsColor") { !slotBounds }
            hideIf("slotBoundsRadius") { !slotBounds }
            updateWhenChanged("type")
            updateWhenChanged("showTitle")
        }
    }

    override fun update(): Boolean {
        val next = whenItemsReady(null) {
            if (visible) Grid(if (showTitle) titleText() else null, contents().orEmpty()) else null
        }
        publishSlots(next)
        staticH = gridTop() + GRID_HEIGHT + GRID_BOTTOM

        val current = grid.value
        if (next == null && current == null) return false
        if (next != null && current != null && next.sameAs(current)) return false
        grid.value = next
        return true
    }

    private fun Grid.sameAs(other: Grid): Boolean =
        title == other.title && items.size == other.items.size &&
            items.indices.all { ItemStack.matches(items[it], other.items[it]) }

    /** the tooltip hook hit tests these screen positions */
    private fun publishSlots(next: Grid?) {
        if (next == null || type != HELD_SHULKER || !isReal || HudManager.isEditing) return
        val scale = effectiveScale
        val top = gridTop()
        val slots = ArrayList<ShulkerPreview.Slot>(next.items.size)
        for (i in next.items.indices) {
            val item = next.items[i]
            if (item.isEmpty) continue
            slots.add(
                ShulkerPreview.Slot(
                    x + (EDGE + i % COLS * SLOT) * scale,
                    y + (top + i / COLS * SLOT) * scale,
                    ITEM_SIZE * scale,
                    item,
                ),
            )
        }
        ShulkerPreview.publishSlots(slots)
    }

    private fun gridTop(): Float = if (showTitle) GRID_TOP_TITLED else GRID_TOP

    private fun titleText(): String = when (type) {
        PLAYER -> "Inventory"
        ENDER_CHEST -> "Ender Chest"
        else -> shulker()?.hoverName?.string ?: "Shulker Box"
    }

    private fun contents(): List<ItemStack>? = when (type) {
        HELD_SHULKER -> shulker()?.shulkerContents()
        ENDER_CHEST -> if (!isReal) exampleContents else EnderChestTracker.contents()
        else -> if (!isReal) {
            exampleContents
        } else {
            val inv = mc.player?.inventory
            // slots 0 to 8 are the hotbar which the main grid does not show
            // copied because the inventory mutates its stacks in place
            inv?.let { List(ROWS * COLS) { i -> if (COLS + i < it.containerSize) it.getItem(COLS + i).copy() else ItemStack.EMPTY } }
        }
    }

    @Composable
    override fun Content() {
        val current = grid.value ?: return
        val top = gridTop()
        val height = top + GRID_HEIGHT + GRID_BOTTOM

        PolyBox(modifier = hudBackground().size(WIDTH, height)) {
            current.title?.let { Title(it) }

            // centres the bounds box on the item whatever the gap
            val boundInset = (SLOT_BOUND_SIZE - ITEM_SIZE) / 2f

            for (row in 0 until ROWS) {
                for (col in 0 until COLS) {
                    val itemX = EDGE + col * SLOT
                    val itemY = top + row * SLOT
                    if (slotBounds) {
                        PolyBox(
                            modifier = PolyModifier
                                .absoluteAt(itemX - boundInset, itemY - boundInset)
                                .size(SLOT_BOUND_SIZE, SLOT_BOUND_SIZE)
                                .background(slotBoundsColor, slotBoundsRadius),
                        )
                    }

                    val item = current.items.getOrNull(row * COLS + col) ?: continue
                    if (item.isEmpty) continue
                    ItemIcon(item, modifier = PolyModifier.absoluteAt(itemX, itemY))
                }
            }
        }
    }

    @Composable
    private fun Title(title: String) {
        val color = PolyColor(textColor, textChroma, textChromaSpeed)
        val modifier = PolyModifier.absoluteAt(EDGE, TITLE_Y)
        if (font == Font.Minecraft) {
            PolyMcText(title, color = color, shadow = showShadow, modifier = modifier)
        } else {
            PolyText(
                title,
                color = color,
                fontSize = FONT_SIZE,
                shadow = showShadow,
                shadowColor = PolyColor(shadowColor, shadowChroma, shadowChromaSpeed),
                shadowOffset = shadowOffsetX,
                font = getPoppinsFontName(),
                modifier = modifier,
            )
        }
    }

    override fun clone(): Hud = (super.clone() as InventoryHud).also {
        it.grid = mutableStateOf(null)
    }
}
