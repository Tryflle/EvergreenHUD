package org.polyfrost.evergreenhud.client.hud.player

import dev.deftu.omnicore.client.OmniClientPlayer
import dev.deftu.omnicore.client.render.OmniMatrixStack
import dev.deftu.omnicore.common.OmniEquipment
import dev.deftu.omnicore.common.isActuallyEmpty
import dev.deftu.omnicore.common.stackAmount
import dev.deftu.textile.minecraft.MCTextFormat
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.enchantment.Enchantment
import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import org.polyfrost.oneconfig.api.config.v1.annotations.*
import org.polyfrost.oneconfig.api.hud.v1.LegacyHud
import org.polyfrost.polyui.color.rgba
import kotlin.math.roundToInt
import net.minecraft.client.renderer.GlStateManager as GL

//#if MC >= 1.9
//$$ import net.minecraft.inventory.EntityEquipmentSlot
//#endif

class ItemHud : LegacyHud(
    id = "item_hud.json",
    title = "Item HUD",
    category = Category.INFO
) {

    private companion object {

        val initial by lazy {
            val stack = ItemStack(Items.diamond_sword)
            stack.addEnchantment(
                //#if MC > 1.8.9
                //$$ Enchantment.getEnchantmentByLocation("minecraft:sharpness"),
                //#else
                Enchantment.sharpness,
                //#endif
                69
            )

            stack
        }

        val iron by lazy {
            ItemStack(Items.iron_ingot)
        }

        val gold by lazy {
            ItemStack(Items.gold_ingot)
        }

        val diamond by lazy {
            ItemStack(Items.diamond)
        }

        val emerald by lazy {
            ItemStack(Items.emerald)
        }

    }

    @Dropdown(
        title = "Item",
        options = [
            "Feet", "Legs", "Chest", "Head", "Main Hand",
            "Iron Ingot", "Gold Ingot", "Diamond", "Emerald",
            //#if MC>=10900
            "Off Hand"
            //#endif
        ],
    )
    var option = 4

    @Switch(title = "Show Durability Bar")
    var showDurability = true

    @Switch(title = "Show Item Amount")
    var showStackSize = true

    @Switch(title = "Show Item Name")
    var showName = false

    @RadioButton(
        title = "Show Durability Overlay",
        options = ["Off", "Absolute", "Percent"]
    )
    var durabilityDisplay = 0

    @Switch(title = "Dynamic Durability Color")
    var dynamicDurabilityColor = false

    @Color(title = "Text Color")
    var textColor = rgba(255, 255, 255)

    @Checkbox(title = "Text Shadow")
    var shadow = true

    override var width = 15f

    override var height: Float
        get() = width
        set(_) {}

    private val ItemStack.orNull: ItemStack?
        get() = if (this.isActuallyEmpty) null else this

    private val ItemStack.durabilityColor: MCTextFormat
        get() {
            if (!dynamicDurabilityColor) {
                return MCTextFormat.WHITE
            }

            return when (1f - itemDamage.toFloat() / maxDamage) {
                in 0.00f..0.10f -> MCTextFormat.DARK_RED
                in 0.10f..0.25f -> MCTextFormat.RED
                in 0.25f..0.40f -> MCTextFormat.GOLD
                in 0.40f..0.60f -> MCTextFormat.YELLOW
                in 0.60f..0.80f -> MCTextFormat.GRAY
                else -> MCTextFormat.WHITE
            }
        }

    private val Item.totalCount: Int
        get() {
            return OmniClientPlayer.getInstance()?.inventory?.mainInventory?.sumOf { stack ->
                if (stack != null && stack.item == this) {
                    stack.stackAmount
                } else {
                    0
                }
            } ?: 0
        }

    override fun render(
        stack: OmniMatrixStack,
        x: Float,
        y: Float,
        scaleX: Float,
        scaleY: Float,
        example: Boolean
    ) {
        val item = getItem(option) ?: return
        val mc = Minecraft.getMinecraft()
        RenderHelper.enableGUIStandardItemLighting()
        mc.renderItem.zLevel += 200f
        mc.renderItem.renderItemIntoGUI(item, x.toInt(), y.toInt())
        if (showDurability && durabilityDisplay == 0) {
            item.renderDurabilityBar(x, y)
        }

        RenderHelper.disableStandardItemLighting()
        mc.renderItem.zLevel -= 200f
        item.renderOverlayText(x, y)
    }

    override fun update(): Boolean {
        return false
    }

    override fun hasBackground(): Boolean {
        return false
    }

    fun getItem(index: Int): ItemStack? {
        if (!isReal || !OmniClientPlayer.hasPlayer) {
            return initial
        }

        return when (index) {
            0 -> OmniClientPlayer.getEquipment(OmniEquipment.EquipmentType.FEET)?.orNull
            1 -> OmniClientPlayer.getEquipment(OmniEquipment.EquipmentType.LEGS)?.orNull
            2 -> OmniClientPlayer.getEquipment(OmniEquipment.EquipmentType.CHEST)?.orNull
            3 -> OmniClientPlayer.getEquipment(OmniEquipment.EquipmentType.HEAD)?.orNull
            4 -> OmniClientPlayer.getEquipment(OmniEquipment.EquipmentType.MAIN_HAND)?.orNull
            5 -> iron
            6 -> gold
            7 -> diamond
            8 -> emerald
            9 -> OmniClientPlayer.getEquipment(OmniEquipment.EquipmentType.OFF_HAND)?.orNull
            else -> null
        }
    }

    private fun ItemStack.renderDurabilityBar(xIn: Float, yIn: Float) {
        if (this.isItemDamaged) {
            val health = this.itemDamage.toFloat() / this.maxDamage.toFloat()
            val length = (13f - health * 13f).roundToInt()
            val colorProp = (255f - health * 255f).roundToInt()
            GL.disableLighting()
            GL.disableDepth()
            GL.disableTexture2D()
            GL.disableAlpha()
            GL.disableBlend()
            val x = xIn.toInt() + 2
            val y = yIn.toInt() + 13
            Gui.drawRect(x, y, x + 13, y + 2, 255 shl 24)
            Gui.drawRect(x, y, x + 12, y + 1, rgba((255 - colorProp) / 4, 64, 0).rgba)
            Gui.drawRect(x, y, x + length, y + 1, rgba(255 - colorProp, colorProp, 0).rgba)
        }
    }

    private fun ItemStack.renderOverlayText(x: Float, y: Float) {
        val fr = Minecraft.getMinecraft().fontRendererObj
        val text = if (isItemStackDamageable) when (durabilityDisplay) {
            1 -> durabilityColor + "${maxDamage - itemDamage}"
            2 -> durabilityColor + "${((1f - itemDamage.toFloat() / maxDamage) * 100f).roundToInt()}%"
            else -> null
        } else {
            val stackSize = if (isStackable) this.item.totalCount else this.stackAmount
            if (!showStackSize || stackSize == 1) null else {
                if (stackSize < 1) "${MCTextFormat.RED}$stackSize" else "$stackSize"
            }
        }

        GL.disableLighting()
        GL.disableDepth()
        GL.disableBlend()
        if (text != null) {
            GL.pushMatrix()
            val width = fr.getStringWidth(text)
            val factor = (17f / width).coerceAtMost(1f)
            GL.scale(factor, factor, 1f)
            fr.drawString(text, (x + 17f) / factor - fr.getStringWidth(text), (y + 9f) / factor, 16777215, shadow)
            GL.popMatrix()
        }

        val name = if (showName) displayName else null
        if (name != null) {
            GL.pushMatrix()
            val width = fr.getStringWidth(name)
            val factor = (17f / width).coerceAtMost(1f)
            GL.scale(factor, factor, 1f)
            val offset = if (text != null) 18f else 16f
            fr.drawString(name, x / factor, (y + offset) / factor, textColor.argb, shadow)
            GL.popMatrix()
        }

        GL.enableAlpha()
        GL.enableLighting()
        GL.enableDepth()
    }

}
