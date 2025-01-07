package org.polyfrost.evergreenhud.hud.player

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.inventory.IInventory
import net.minecraft.util.ResourceLocation
import org.polyfrost.oneconfig.api.config.v1.annotations.RadioButton
import org.polyfrost.oneconfig.api.hud.v1.LegacyHud
import org.polyfrost.universal.UMatrixStack
import net.minecraft.client.renderer.GlStateManager as GL

class Inventory : LegacyHud() {
    override var width: Float
        get() = 400f
        set(_) {}

    override var height = 200f

    @RadioButton(title = "Inventory", options = ["Player", "Ender Chest"])
    var type = 0

    private val inventory: IInventory?
        get() {
            val player = Minecraft.getMinecraft().thePlayer
            return if (type == 0) player?.inventory else player?.inventoryEnderChest
        }

    private val inventoryBackground = ResourceLocation("textures/gui/container/generic_54.png")


    @Suppress("SENSELESS_COMPARISON", "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    override fun render(stack: UMatrixStack, x: Float, y: Float, scaleX: Float, scaleY: Float) {
        val mc = Minecraft.getMinecraft()
        val fr = mc.fontRendererObj
        val ri = mc.renderItem
        val gui = mc.ingameGUI
        mc.textureManager.bindTexture(inventoryBackground)
        GL.color(1.0f, 1.0f, 1.0f, 1.0f)
        GL.enableRescaleNormal()
        // todo positioning and scaling
        gui.drawTexturedModalRect(0, 0, 0, 0, 176, 3 * 18 + 17)
        gui.drawTexturedModalRect(0, 3 * 18 + 17, 0, 215, 176, 22)
        fr.drawString("Inventory", 8, 6, 0)

        RenderHelper.enableGUIStandardItemLighting()

        val inv = inventory ?: return
        val xi = 0
        val yi = 0
        val base = if (type == 0) 9 else 0
        for (i in base until base + 27) {
            val row = i / 9
            val column = i % 9
            val item = inv.getStackInSlot(i)
            if (item != null) {
                val itemX = xi + 8 + column * 18
                val itemY = yi + row * 18
                ri.renderItemAndEffectIntoGUI(item, itemX, itemY)
                ri.renderItemOverlayIntoGUI(fr, item, itemX, itemY, null)
            }
        }
        RenderHelper.disableStandardItemLighting()
        GL.disableBlend()
        GL.disableRescaleNormal()
    }

    override fun category() = Category.PLAYER

    override fun title() = "Inventory"

    override fun update() = false
}