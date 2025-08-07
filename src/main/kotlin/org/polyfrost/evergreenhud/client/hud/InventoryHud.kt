package org.polyfrost.evergreenhud.client.hud

import dev.deftu.omnicore.client.OmniClient
import dev.deftu.omnicore.client.render.OmniGameRendering
import dev.deftu.omnicore.client.render.OmniMatrixStack
import dev.deftu.omnicore.client.render.state.OmniManagedBlendState
import dev.deftu.omnicore.common.OmniIdentifier
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack
import org.polyfrost.oneconfig.api.config.v1.annotations.RadioButton
import org.polyfrost.oneconfig.api.hud.v1.LegacyHud

//#if MC >= 1.21.2
//$$ import net.minecraft.client.render.RenderLayer
//#endif

//#if MC >= 1.20.1
//$$ import net.minecraft.client.gui.DrawContext
//$$ import net.minecraft.util.Identifier
//$$ import org.polyfrost.evergreenhud.client.utils.createDrawContext
//#endif

//#if MC <= 1.12.2
import net.minecraft.client.renderer.GlStateManager
//#endif

// CHECK OK
class InventoryHud : LegacyHud(
    id = "inventory.json",
    title = "Inventory",
    category = Category.PLAYER,
) {
    companion object {
        private val INVENTORY_BACKGROUND = OmniIdentifier.create("textures/gui/container/generic_54.png")
    }

    override var width: Float = 176f
    override var height = 93f

    @RadioButton(title = "Inventory", options = ["Player", "Ender Chest"])
    var type = 0

    private val inventory: IInventory?
        get() {
            val player = Minecraft.getMinecraft().thePlayer
            return if (type == 0) player?.inventory else player?.inventoryEnderChest
        }

    @Suppress("SENSELESS_COMPARISON", "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    override fun render(stack: OmniMatrixStack, x: Float, y: Float, scaleX: Float, scaleY: Float, example: Boolean) {
        val inv = inventory ?: return
        val mc = Minecraft.getMinecraft()
        val gui = mc.ingameGUI

        stack.push()

        //#if MC <= 1.12.2
        GlStateManager.enableRescaleNormal()
        GlStateManager.color(1f, 1f, 1f, 1f)
        //#endif

        //#if MC >= 1.20.1
        //$$ val ctx = stack.createDrawContext()
        //#endif

        OmniManagedBlendState.enableBlend()
        stack.scale(scaleX, scaleY, 1f)
        stack.translate(x * 1f / scaleX, y * 1f / scaleY, 0f)

        drawTexture(
            //#if MC >= 1.20.1
            //$$ ctx,
            //$$ INVENTORY_BACKGROUND,
            //#elseif MC <= 1.19.4
            gui,
            //#endif
            stack,
            x = 0, y = 0,
            u = 0, v = 0,
            width = 176, height = 3 * 18 + 17
        )
        drawTexture(
            //#if MC >= 1.20.1
            //$$ ctx,
            //$$ INVENTORY_BACKGROUND,
            //#elseif MC <= 1.19.4
            gui,
            //#endif
            stack,
            x = 0, y = 3 * 18 + 17,
            u = 0, v = 215,
            width = 176, height = 22
        )

        OmniGameRendering.drawText(stack, if (type == 0) "Inventory" else "Ender Chest", 8f, 6f, 0)

        RenderHelper.enableGUIStandardItemLighting()
        val base = if (type == 0) 9 else 0
        for (i in base until base + 27) {
            val row = i / 9
            val column = i % 9
            val item = inv.getStackInSlot(i)
            if (item != null) {
                val itemX = 8 + column * 18
                val itemY = row * 18
                drawItem(
                    //#if MC >= 1.20.1
                    //$$ ctx = ctx,
                    //#endif
                    stack = stack,
                    itemStack = item,
                    x = itemX, y = itemY
                )
            }
        }

        RenderHelper.disableStandardItemLighting()
        OmniManagedBlendState.disableBlend()

        //#if MC <= 1.12.2
        GlStateManager.disableRescaleNormal()
        //#endif

        stack.pop()
    }

    override fun update(): Boolean {
        return false
    }

    override fun hasBackground(): Boolean {
        return false
    }

    private fun drawTexture(
        //#if MC >= 1.20.1
        //$$ ctx: DrawContext,
        //$$ identifier: Identifier,
        //#elseif MC <= 1.19.4
        gui: Gui,
        //#endif
        stack: OmniMatrixStack,
        x: Int,
        y: Int,
        u: Int,
        v: Int,
        width: Int,
        height: Int
    ) {
        //#if MC >= 1.21.2
        //$$ val function = RenderLayer::getGuiTextured
        //$$ val u = u.toFloat()
        //$$ val v = v.toFloat()
        //$$ val uWidth = width
        //$$ val vHeight = height
        //$$ ctx.drawTexture(
        //$$     function,
        //$$     identifier,
        //$$     x, y,
        //$$     u, v,
        //$$     width, height,
        //$$     uWidth, vHeight,
        //$$     0
        //$$ )
        //#elseif MC >= 1.20.1
        //$$ ctx.drawTexture(
        //$$     identifier,
        //$$     x, y,
        //$$     u, v,
        //$$     width, height
        //$$ )
        //#elseif MC >= 1.19.4
        //$$ OmniClient.textureManager.bindTexture(INVENTORY_BACKGROUND)
        //$$ net.minecraft.client.gui.GuiComponent.blit(
        //$$     stack.toVanillaStack(),
        //$$     x, y,
        //$$     u, v,
        //$$     width, height
        //$$ )
        //#elseif MC >= 1.16.5
        //$$ OmniClient.textureManager.bindTexture(INVENTORY_BACKGROUND)
        //$$ gui.blit(
        //$$     stack.toVanillaStack(),
        //$$     x, y,
        //$$     u, v,
        //$$     width, height
        //$$ )
        //#else
        gui.drawTexturedModalRect(
            x, y,
            u, v,
            width, height
        )
        //#endif
    }

    private fun drawItem(
        //#if MC >= 1.20.1
        //$$ ctx: DrawContext,
        //#endif
        stack: OmniMatrixStack,
        itemStack: ItemStack,
        x: Int,
        y: Int
    ) {
        //#if MC >= 1.20.1
        //$$ val fontRenderer = OmniClient.fontRenderer
        //$$ ctx.drawItemWithoutEntity(itemStack, x, y)
        //$$
        //$$ ctx.drawItemInSlot(
        //$$     fontRenderer,
        //$$     itemStack,
        //$$     x, y
        //$$ )
        //#elseif MC >= 1.16.5
        //$$ val fontRenderer = OmniClient.fontRenderer
        //$$ val itemRenderer = OmniClient.getInstance().itemRenderer
        //$$ itemRenderer.renderGuiItem(
        //#if MC >= 1.19.4
        //$$     stack.toVanillaStack(),
        //#endif
        //$$     itemStack,
        //$$     x, y
        //$$ )
        //$$
        //$$ itemRenderer.renderGuiItemDecorations(
        //#if MC >= 1.19.4
        //$$     stack.toVanillaStack(),
        //#endif
        //$$     fontRenderer,
        //$$     itemStack,
        //$$     x, y
        //$$ )
        //#else
        val fontRenderer = OmniClient.fontRenderer
        val itemRenderer = OmniClient.getInstance().renderItem
        itemRenderer.renderItemAndEffectIntoGUI(itemStack, x, y)

        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        itemRenderer.renderItemOverlayIntoGUI(fontRenderer, itemStack, x, y, null)
        //#endif
    }
}
