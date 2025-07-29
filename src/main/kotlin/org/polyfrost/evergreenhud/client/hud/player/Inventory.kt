//package org.polyfrost.evergreenhud.hud.player
//
//import dev.deftu.omnicore.client.render.OmniMatrixStack
//import net.minecraft.client.Minecraft
//import net.minecraft.client.renderer.RenderHelper
//import net.minecraft.inventory.IInventory
//import net.minecraft.util.ResourceLocation
//import org.polyfrost.oneconfig.api.config.v1.annotations.RadioButton
//import org.polyfrost.oneconfig.api.hud.v1.LegacyHud
//import net.minecraft.client.renderer.GlStateManager as GL
//
//// CHECK OK
//class Inventory : LegacyHud() {
//    override var width: Float
//        get() = 176f
//        set(_) {}
//
//    override var height = 93f
//
//    @RadioButton(title = "Inventory", options = ["Player", "Ender Chest"])
//    var type = 0
//
//    private val inventory: IInventory?
//        get() {
//            val player = Minecraft.getMinecraft().thePlayer
//            return if (type == 0) player?.inventory else player?.inventoryEnderChest
//        }
//
//    private val inventoryBackground = ResourceLocation("textures/gui/container/generic_54.png")
//
//
//    @Suppress("SENSELESS_COMPARISON", "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
//    override fun render(stack: OmniMatrixStack, x: Float, y: Float, scaleX: Float, scaleY: Float) {
//        val inv = inventory ?: return
//        val mc = Minecraft.getMinecraft()
//        val fr = mc.fontRendererObj
//        val ri = mc.renderItem
//        val gui = mc.ingameGUI
//
//        GL.pushMatrix()
//        mc.textureManager.bindTexture(inventoryBackground)
//        GL.color(1f, 1f, 1f, 1f)
//        GL.enableRescaleNormal()
//        GL.enableBlend()
//        GL.scale(scaleX, scaleY, 1f)
//        GL.translate(x * 1f / scaleX, y * 1f / scaleY, 0f)
//        gui.drawTexturedModalRect(0, 0, 0, 0, 176, 3 * 18 + 17)
//        gui.drawTexturedModalRect(0, 3 * 18 + 17, 0, 215, 176, 22)
//        fr.drawString(if (type == 0) "Inventory" else "Ender Chest", 8, 6, 0)
//
//        RenderHelper.enableGUIStandardItemLighting()
//        val base = if (type == 0) 9 else 0
//        for (i in base until base + 27) {
//            val row = i / 9
//            val column = i % 9
//            val item = inv.getStackInSlot(i)
//            if (item != null) {
//                val itemX = 8 + column * 18
//                val itemY = row * 18
//                ri.renderItemAndEffectIntoGUI(item, itemX, itemY)
//                ri.renderItemOverlayIntoGUI(fr, item, itemX, itemY, null)
//            }
//        }
//        RenderHelper.disableStandardItemLighting()
//        GL.disableBlend()
//        GL.disableRescaleNormal()
//        GL.popMatrix()
//    }
//
//    override fun category() = Category.PLAYER
//
//    override fun title() = "Inventory"
//
//    override fun update() = false
//
//    override fun hasBackground() = false
//}