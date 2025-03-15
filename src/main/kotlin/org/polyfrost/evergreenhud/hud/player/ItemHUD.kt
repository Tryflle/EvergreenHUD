package org.polyfrost.evergreenhud.hud.player

import dev.deftu.omnicore.client.render.OmniMatrixStack
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

//#if MC>=10900
//$$ import net.minecraft.inventory.EntityEquipmentSlot
//#endif

class ItemHUD : LegacyHud() {
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
    var duraDisplay = 0

    @Switch(title = "Dynamic Durability Color")
    var dynamicDuraColor = false

    @Color(title = "Text Color")
    var textColor = rgba(255, 255, 255)

    @Checkbox(title = "Text Shadow")
    var shadow = true

    override var width = 15f
    override var height: Float
        get() = width
        set(_) {}

    private fun ItemStack.duraColor(): String {
        return if (!dynamicDuraColor) "§f"
        else when (1f - itemDamage.toFloat() / maxDamage) {
            in 0.00f..<0.10f -> "§4"
            in 0.10f..<0.25f -> "§c"
            in 0.25f..<0.40f -> "§6"
            in 0.40f..<0.60f -> "§e"
            in 0.60f..<0.80f -> "§7"
            else -> "§f"
        }
    }

    override fun category() = Category.INFO


    override fun render(stack: OmniMatrixStack, x: Float, y: Float, scaleX: Float, scaleY: Float) {
        val item = getItem(option) ?: return
        val mc = Minecraft.getMinecraft()
        RenderHelper.enableGUIStandardItemLighting()
        mc.renderItem.zLevel += 200f
        mc.renderItem.renderItemIntoGUI(item, x.toInt(), y.toInt())
        if (showDurability && duraDisplay == 0) item.renderDurabilityBar(x, y)
        RenderHelper.disableStandardItemLighting()
        mc.renderItem.zLevel -= 200f
        item.renderOverlayText(x, y)
    }

    override fun title() = "Item HUD"

    override fun update() = false

    override fun hasBackground() = false

    fun getItem(index: Int): ItemStack? {
        if (!isReal) return initial
        val player = Minecraft.getMinecraft().thePlayer ?: return initial
        return when (index) {
            //#if MC>=10900
            //$$ 0 -> player.getItemStackFromSlot(EntityEquipmentSlot.FEET).let { if(it.isEmpty) null else it }
            //$$ 1 -> player.getItemStackFromSlot(EntityEquipmentSlot.LEGS).let { if(it.isEmpty) null else it }
            //$$ 2 -> player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).let { if(it.isEmpty) null else it }
            //$$ 3 -> player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).let { if(it.isEmpty) null else it }
            //$$ 4 -> player.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND).let { if(it.isEmpty) null else it }
            //$$ 9 -> player.getItemStackFromSlot(EntityEquipmentSlot.OFFHAND).let { if(it.isEmpty) null else it }
            //#else
            in 0..3 -> player.inventory?.armorInventory?.get(index)
            4 -> player.heldItem
            //#endif

            5 -> iron
            6 -> gold
            7 -> diamond
            8 -> emerald
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
            Gui.drawRect(x, y, x + 12, y + 1, getRGB((255 - colorProp) / 4, 64, 0))
            Gui.drawRect(x, y, x + length, y + 1, getRGB(255 - colorProp, colorProp, 0))
        }
    }

    private fun ItemStack.renderOverlayText(x: Float, y: Float) {
        val fr = Minecraft.getMinecraft().fontRendererObj
        val text = if (isItemStackDamageable) when (duraDisplay) {
            1 -> duraColor() + "${maxDamage - itemDamage}"
            2 -> duraColor() + "${((1f - itemDamage.toFloat() / maxDamage) * 100f).roundToInt()}%"
            else -> null
        } else {
            val stackSize = if (isStackable) this.item.countTotal() else
                //#if MC>=11202
                //$$ this.getCount()
                //#else
                this.stackSize
                //#endif
            if (!showStackSize || stackSize == 1) null else {
                if (stackSize < 1) "§c$stackSize" else "$stackSize"
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

    private fun getRGB(r: Int, g: Int, b: Int) = (r and 0xFF shl 16) or (g and 0xFF shl 8) or (b and 0xFF shl 0) or (255 shl 24)

    private companion object Constants {
        val initial = ItemStack(Items.diamond_sword)
        val iron = ItemStack(Items.iron_ingot)
        val gold = ItemStack(Items.gold_ingot)
        val diamond = ItemStack(Items.diamond)
        val emerald = ItemStack(Items.emerald)

        init {
            initial.addEnchantment(
                //#if MC > 1.8.9
                //$$ Enchantment.getEnchantmentByLocation("minecraft:sharpness")
                //#else
                Enchantment.sharpness
                //#endif
                , 69)
        }
    }
}
