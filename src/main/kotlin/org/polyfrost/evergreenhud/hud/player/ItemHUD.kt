package org.polyfrost.evergreenhud.hud.player

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.item.ItemBow
import net.minecraft.item.ItemStack
import org.polyfrost.oneconfig.api.config.v1.annotations.*
import org.polyfrost.oneconfig.api.hud.v1.LegacyHud
import org.polyfrost.oneconfig.renderer.TextRenderer
import org.polyfrost.oneconfig.utils.v1.dsl.mc
import org.polyfrost.polyui.color.rgba
import org.polyfrost.universal.UGraphics
import org.polyfrost.universal.UMatrixStack
import kotlin.math.ceil
import kotlin.math.roundToInt

//#if MC>=10900
//$$ import net.minecraft.inventory.EntityEquipmentSlot
//#endif

class ItemHUD : LegacyHud() {
    @Dropdown(
        title = "Item",
        //#if MC>=10900
        //$$ options = ["Feet", "Legs", "Chest", "Head", "Main Hand", "Off Hand"]
        //#else
        options = ["Feet", "Legs", "Chest", "Head", "Main Hand"]
        //#endif
    )
    var option = 4

    @Slider(title = "Item Padding", min = 0F, max = 10F)
    var padding = 5f

    @Slider(title = "Icon Padding", min = 0F, max = 10F)
    var iconPadding = 5f

    @RadioButton(title = "Type", options = ["Horizontal", "Vertical"])
    var type = 0

    @Switch(title = "Show Durability Bar")
    var durabilityBar = true

    @Switch(title = "Show Item Amount")
    var showStackSize = true

    @Switch(title = "Show Item Name")
    var showName = false

    @RadioButton(
        title = "Show Durability Overlay",
        options = ["Disabled", "Absolute", "Percentage"]
    )
    var duraDisplay = 0

    @Switch(title = "Dynamic Durability Color")
    var dynamicDuraColor = false

    @Color(title = "Text Color")
    var textColor = rgba(255, 255, 255)

    @Checkbox(title = "Text Shadow")
    var textType = true

    private fun ItemStack.duraColor() = when (itemDamage.toFloat() / maxDamage) {
        in 0.00f..<0.10f -> "§4"
        in 0.10f..<0.25f -> "§c"
        in 0.25f..<0.40f -> "§6"
        in 0.60f..<0.80f -> "§7"
        else -> "§f"
    }


    var item: ItemStack? = null


    override fun render(matrices: UMatrixStack, x: Float, y: Float, scaleX: Float, scaleY: Float) {

    }

    fun getItem(index: Int): ItemStack? {
        val player = Minecraft.getMinecraft().thePlayer ?: return null
        return when (index) {
            //#if MC>=10900
            //$$ 0 -> player.getItemStackFromSlot(EntityEquipmentSlot.FEET).let { if(it.isEmpty) null else it }
            //$$ 1 -> player.getItemStackFromSlot(EntityEquipmentSlot.LEGS).let { if(it.isEmpty) null else it }
            //$$ 2 -> player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).let { if(it.isEmpty) null else it }
            //$$ 3 -> player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).let { if(it.isEmpty) null else it }
            //$$ 4 -> player.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND).let { if(it.isEmpty) null else it }
            //$$ 5 -> player.getItemStackFromSlot(EntityEquipmentSlot.OFFHAND).let { if(it.isEmpty) null else it }
            //#else
            in 0..3 -> player.inventory?.armorInventory?.get(index)
            4 -> player.heldItem
            //#endif
            else -> null
        }
    }

    private fun Item?.countTotal() = Minecraft.getMinecraft().thePlayer?.inventory?.mainInventory?.sumOf {
        if (it == null || it.item != this) 0 else
        //#if MC>=11202
        //$$ it.getCount()
        //#else
            it.stackSize
        //#endif
    } ?: 0

    private fun ItemStack.getText(): String? = when (duraDisplay) {

        3 -> displayName
        else -> null
    }

    private fun String.width() = Minecraft.getMinecraft().fontRendererObj.getStringWidth(this)

    fun draw(matrices: UMatrixStack?, x: Float, y: Float, scale: Float, items: List<ItemStack>) {
        val iconSize = 16f
        val offset = iconSize + padding

        val texts = items.map {
            when (duraDisplay) {
                1 -> if (it.isItemStackDamageable) (it.maxDamage - it.itemDamage).toString() else ""
                2 -> if (it.isItemStackDamageable) "${(1f - (it.itemDamage.toFloat() / it.maxDamage.toFloat()) * 100f).toInt()}%" else ""
                3 -> it.displayName ?: ""
                else -> ""
            }.let { text ->
                text to mc.fontRendererObj.getStringWidth(text)
            }
        }

        val longestWidth = texts.maxOfOrNull { mc.fontRendererObj.getStringWidth(it.first) } ?: 0

        actualWidth = if (type) longestWidth + iconSize else (padding * (items.size - 1)).toFloat()

        actualHeight = if (type) items.size * offset - padding else offset - padding

        translation = 0F

        if (longestWidth > 0 && type) actualWidth += iconPadding

        UGraphics.GL.pushMatrix()
        UGraphics.GL.scale(scale, scale, 1f)
        UGraphics.GL.translate(x / scale, y / scale, 0f)
        items.forEachIndexed { i: Int, stack: ItemStack ->

            var (text, textWidth) = texts[i]

            if (!type) actualWidth += texts[i].second + iconSize + if (textWidth > 0) iconPadding else 0

            val width = if (type) actualWidth else textWidth + iconSize + iconPadding

            val itemY = if (type) i * offset else 0

            val itemX = when (alignment) {
                false -> width - iconSize - if (textWidth == 0 && !type) iconPadding else 0
                true -> 0
            }

            val textX = when (alignment) {
                false -> width - iconSize - textWidth - iconPadding
                true -> iconSize + iconPadding
            }

            if (!type && i > 0) translation += offset + texts[i - 1].second + if (texts[i - 1].second > 0) iconPadding else 0

            val amount = getItemAmount(if (stack.item is ItemBow) Items.arrow else stack.item).let {
                if (it != 0) it.toString() else null
            }
            RenderHelper.enableGUIStandardItemLighting()
            mc.renderItem.zLevel = 200f
            mc.renderItem.renderItemAndEffectIntoGUI(stack, itemX.toInt() + translation.toInt(), itemY.toInt())
            renderDurabilityBar(mc.fontRendererObj, stack, itemX.toInt() + translation.toInt(), itemY.toInt(), amount)
            RenderHelper.disableStandardItemLighting()
            val renderColor = if (dynamicDuraColor) java.awt.Color(255, 255, 255).rgb else textColor.rgb

            if (dynamicDuraColor && stack.isItemStackDamageable) {
                val percentage = ceil((stack.maxDamage - stack.itemDamage).toFloat() / stack.maxDamage.toFloat() * 100f).toInt()
                for (color in COLORS) {
                    if (percentage <= color.key) {
                        text = "§" + color.value + text
                        break
                    }
                }
            }


            UGraphics.GL.pushMatrix()
            TextRenderer.drawScaledString(
                text,
                textX + translation,
                itemY.toFloat() + mc.fontRendererObj.FONT_HEIGHT / 2f,
                renderColor,
                TextRenderer.TextType.toType(textType),
                1f
            )
            UGraphics.GL.popMatrix()
        }
        UGraphics.GL.popMatrix()
    }

    fun renderDurabilityBar(stack: ItemStack, xPosition: Int, yPosition: Int) {
        if (stack.isItemDamaged) {
            val health = stack.itemDamage.toFloat() / stack.maxDamage.toFloat()
            val length = (13f - health * 13f).roundToInt()
            val colorProp = (255f - health * 255f).roundToInt()
            GlStateManager.disableLighting()
            GlStateManager.disableDepth()
            GlStateManager.disableTexture2D()
            GlStateManager.disableAlpha()
            GlStateManager.disableBlend()
            val x = xPosition + 2
            val y = yPosition + 13
            Gui.drawRect(x, y, x + 13, y + 2, 0)
            Gui.drawRect(x, y, x + 12, y + 1, getRGB((255 - colorProp) / 4, 64, 0))
            Gui.drawRect(x, y, x + length, y + 1, getRGB(255 - colorProp, colorProp, 0))
        }
    }

    fun renderStackCount(fr: FontRenderer, stack: ItemStack, x: Float, y: Float, text: String?) {

        val stackSize =
        //#if MC>=11202
        //$$ stack.getCount()
            //#else
            stack.stackSize
        //#endif

        if (text != null) {
            fr.drawStringWithShadow(text, x + 19f - 2f - fr.getStringWidth(text), y + 6f + 3f, 16777215)
        } else if (stackSize != 1) {
            val s = if (stackSize < 1) "§c$stackSize" else "$stackSize"
        }
        if (showStackSize && (stackSize != 1 || text != null)) {
            var s = text ?: stackSize.toString()
            if (text == null && stackSize < 1) {
                s = "§c$stackSize"
            }

        }
    }

    fun ItemStack.renderOverlayText(fr: FontRenderer, x: Float, y: Float, color: Int = 16777215, shadow: Boolean) {
        val stackSize = if (isStackable) this.item.countTotal() else this.stackSize
        val text = when (duraDisplay) {
            1 -> if (isItemStackDamageable) duraColor() + "${maxDamage - itemDamage}" else null
            2 -> if (isItemStackDamageable) duraColor() + "${((1f - (itemDamage / maxDamage)) * 100f).roundToInt()}%" else null
            else -> if (!showStackSize || stackSize == 1) null else {
                if (stackSize < 1) "§c$stackSize" else "$stackSize"
            }
        }
        if (text == null) return

        GlStateManager.disableLighting()
        GlStateManager.disableDepth()
        GlStateManager.disableBlend()
        fr.drawString(text, x + 17f - fr.getStringWidth(text), y + 9f, color, shadow)
        GlStateManager.enableAlpha()
        GlStateManager.enableLighting()
        GlStateManager.enableDepth()
    }

    private fun getRGB(r: Int, g: Int, b: Int) = (r and 0xFF shl 16) or (g and 0xFF shl 8) or (b and 0xFF shl 0)

    override fun getWidth(scale: Float, example: Boolean): Float = actualWidth * scale

    override fun getHeight(scale: Float, example: Boolean): Float = actualHeight * scale
}
