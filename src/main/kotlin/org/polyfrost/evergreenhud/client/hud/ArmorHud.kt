package org.polyfrost.evergreenhud.client.hud

//? if < 26
import net.minecraft.client.gui.GuiGraphics
//? if >= 26
//import net.minecraft.client.gui.GuiGraphicsExtractor as GuiGraphics
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import org.polyfrost.oneconfig.api.config.v1.annotations.Dropdown
import org.polyfrost.oneconfig.api.config.v1.annotations.RadioButton
import org.polyfrost.oneconfig.api.config.v1.annotations.Slider
import org.polyfrost.oneconfig.api.config.v1.annotations.Switch
import org.polyfrost.oneconfig.api.hud.v1.LegacyHud
import org.polyfrost.oneconfig.utils.v1.dsl.mc
import kotlin.math.ceil
import kotlin.math.max

class ArmorHud : LegacyHud(
    id = "armor.json",
    title = "Armor Status",
    category = Category.PLAYER,
) {
    private companion object {
        private const val ICON = 16
        private const val TEXT_GAP = 2

        private const val HORIZONTAL = 0
        private const val RIGHT = 1

        private const val DURABILITY = 1
        private const val DURABILITY_PERCENT = 2
        private const val NAME = 3

        private val exampleHelmet by lazy { ItemStack(Items.DIAMOND_HELMET) }
        private val exampleChestplate by lazy { ItemStack(Items.DIAMOND_CHESTPLATE) }
        private val exampleLeggings by lazy { ItemStack(Items.DIAMOND_LEGGINGS) }
        private val exampleBoots by lazy { ItemStack(Items.DIAMOND_BOOTS) }
        private val exampleMainHand by lazy { ItemStack(Items.DIAMOND_SWORD) }
        private val exampleOffhand by lazy { ItemStack(Items.SHIELD) }
    }

    @Switch(title = "Helmet")
    var showHelmet = true

    @Switch(title = "Chestplate")
    var showChestplate = true

    @Switch(title = "Leggings")
    var showLeggings = true

    @Switch(title = "Boots")
    var showBoots = true

    @Switch(title = "Main Hand")
    var showMainHand = true

    @Switch(title = "Off Hand")
    var showOffhand = true

    @Slider(title = "Padding", min = 0F, max = 20F, step = 1F)
    var padding = 5f

    @RadioButton(title = "Direction", options = ["Horizontal", "Vertical"])
    var direction = HORIZONTAL

    @Switch(title = "Reversed")
    var reversed = false

    @Switch(title = "Item Decorations", description = "Vanilla durability bar and stack count.")
    var showDecorations = true

    @Dropdown(title = "Extra Info", options = ["None", "Durability", "Durability %", "Item Name"])
    var extraInfo = 0

    @RadioButton(title = "Text Position", options = ["Left", "Right"])
    var textPosition = RIGHT

    @Switch(title = "Dynamic Durability Color", description = "Colour durability text from green (full) to red (empty).")
    var dynamicColor = false

    private class Entry(
        val stack: ItemStack,
        val text: String,
        val textColor: Int,
        val iconX: Int,
        val iconY: Int,
        val textX: Int,
        val textY: Int,
    )

    private var layout: List<Entry> = emptyList()
    private var actualW = ICON.toFloat()
    private var actualH = ICON.toFloat()

    override val width get() = actualW
    override val height get() = actualH

    override fun update(): Boolean {
        recompute()
        return false
    }

    private fun currentItems(): List<ItemStack> {
        val list = ArrayList<ItemStack>(6)
        if (!isReal) {
            if (showHelmet) list.add(exampleHelmet)
            if (showChestplate) list.add(exampleChestplate)
            if (showLeggings) list.add(exampleLeggings)
            if (showBoots) list.add(exampleBoots)
            if (showMainHand) list.add(exampleMainHand)
            if (showOffhand) list.add(exampleOffhand)
        } else {
            val player = mc.player ?: return list
            fun add(slot: EquipmentSlot) = player.getItemBySlot(slot).let { if (!it.isEmpty) list.add(it) }
            if (showHelmet) add(EquipmentSlot.HEAD)
            if (showChestplate) add(EquipmentSlot.CHEST)
            if (showLeggings) add(EquipmentSlot.LEGS)
            if (showBoots) add(EquipmentSlot.FEET)
            if (showMainHand) add(EquipmentSlot.MAINHAND)
            if (showOffhand) add(EquipmentSlot.OFFHAND)
        }
        if (reversed) list.reverse()
        return list
    }

    private fun infoText(stack: ItemStack): String = when (extraInfo) {
        DURABILITY -> if (stack.isDamageableItem) (stack.maxDamage - stack.damageValue).toString() else ""
        DURABILITY_PERCENT -> if (stack.isDamageableItem) "${durabilityPercent(stack)}%" else ""
        NAME -> stack.hoverName.string
        else -> ""
    }

    private fun durabilityPercent(stack: ItemStack): Int =
        ceil((stack.maxDamage - stack.damageValue).toFloat() / stack.maxDamage.toFloat() * 100f).toInt()

    private fun durabilityColor(percent: Int): Int {
        val p = percent.coerceIn(0, 100) / 100f
        val r = (255f - p * 170f).toInt()
        val g = (85f + p * 170f).toInt()
        return (0xFF shl 24) or (r shl 16) or (g shl 8) or 85
    }

    private fun recompute() {
        val stacks = currentItems()
        if (stacks.isEmpty()) {
            layout = emptyList()
            actualW = 0f
            actualH = 0f
            return
        }

        val font = mc.font
        val textY = ((ICON - font.lineHeight) / 2f).toInt()
        val pad = padding.toInt()
        val entries = ArrayList<Entry>(stacks.size)

        var cursorX = 0
        var cursorY = 0
        var maxCell = 0

        for (stack in stacks) {
            val text = infoText(stack)
            val textW = if (text.isEmpty()) 0 else font.width(text)
            val textPart = if (textW > 0) TEXT_GAP + textW else 0
            val cellW = ICON + textPart

            val color = if (dynamicColor && stack.isDamageableItem &&
                (extraInfo == DURABILITY || extraInfo == DURABILITY_PERCENT)
            ) {
                durabilityColor(durabilityPercent(stack))
            } else {
                textColor
            }

            val originX: Int
            val originY: Int
            if (direction == HORIZONTAL) {
                originX = cursorX
                originY = 0
            } else {
                originX = 0
                originY = cursorY
            }

            val iconX: Int
            val textX: Int
            if (textPosition == RIGHT) {
                iconX = originX
                textX = originX + ICON + TEXT_GAP
            } else {
                textX = originX
                iconX = originX + textPart
            }

            entries.add(Entry(stack, text, color, iconX, originY, textX, originY + textY))

            maxCell = max(maxCell, cellW)
            if (direction == HORIZONTAL) {
                cursorX += cellW + pad
            } else {
                cursorY += ICON + pad
            }
        }

        layout = entries
        if (direction == HORIZONTAL) {
            actualW = (cursorX - pad).toFloat()
            actualH = ICON.toFloat()
        } else {
            actualW = maxCell.toFloat()
            actualH = (cursorY - pad).toFloat()
        }
    }

    override fun render(graphics: GuiGraphics) {
        val entries = layout
        if (entries.isEmpty()) return

        if (showBackground) {
            graphics.fill(0, 0, actualW.toInt(), actualH.toInt(), bgColor)
        }

        val font = mc.font
        for (e in entries) {
            //? if < 26 {
            graphics.renderItem(e.stack, e.iconX, e.iconY)
            if (showDecorations) graphics.renderItemDecorations(font, e.stack, e.iconX, e.iconY)
            //?} else {
            /*graphics.item(e.stack, e.iconX, e.iconY)
            if (showDecorations) graphics.itemDecorations(font, e.stack, e.iconX, e.iconY)
            *///?}

            if (e.text.isNotEmpty()) {
                //? if < 26
                graphics.drawString(font, e.text, e.textX, e.textY, e.textColor)
                //? if >= 26
                //graphics.text(font, e.text, e.textX, e.textY, e.textColor)
            }
        }
    }
}
