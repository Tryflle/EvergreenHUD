//package org.polyfrost.evergreenhud.client.hud
//
//import dev.deftu.omnicore.client.OmniClient
//import dev.deftu.omnicore.client.render.ImmediateScreenRenderer
//import dev.deftu.omnicore.client.render.OmniGameRendering
//import dev.deftu.omnicore.client.render.OmniMatrixStack
//import dev.deftu.omnicore.client.render.OmniTextureManager
//import dev.deftu.omnicore.client.render.pipeline.DrawModes
//import dev.deftu.omnicore.client.render.pipeline.OmniRenderPipeline
//import dev.deftu.omnicore.client.render.pipeline.VertexFormats
//import dev.deftu.omnicore.client.render.state.OmniManagedBlendState
//import dev.deftu.omnicore.client.render.vertex.OmniBufferBuilder
//import dev.deftu.omnicore.common.OmniIdentifier
//import net.minecraft.client.Minecraft
//import net.minecraft.client.renderer.RenderHelper
//import net.minecraft.inventory.IInventory
//import net.minecraft.item.ItemStack
//import org.polyfrost.evergreenhud.EvergreenHudConstants
//import org.polyfrost.oneconfig.api.config.v1.annotations.RadioButton
//import org.polyfrost.oneconfig.api.hud.v1.LegacyHud
//import java.awt.Color
//
////#if MC >= 1.20.1
////$$ import net.minecraft.client.gui.DrawContext
////$$ import org.polyfrost.evergreenhud.client.hooks.smuggledHudDrawContext
////#endif
//
//// CHECK OK
//class InventoryHud : LegacyHud(
//    id = "inventory.json",
//    title = "Inventory",
//    category = Category.PLAYER,
//) {
//    companion object {
//        private val INVENTORY_BACKGROUND = OmniIdentifier.create("textures/gui/container/generic_54.png")
//        private val PIPELINE = OmniRenderPipeline.builderWithDefaultShader(
//            identifier = OmniIdentifier.create(EvergreenHudConstants.ID, "inventory_hud"),
//            vertexFormat = VertexFormats.POSITION_TEXTURE_COLOR,
//            mode = DrawModes.QUADS
//        ).apply {
//            blendState = OmniManagedBlendState.ALPHA
//        }.build()
//    }
//
//    override var width: Float = 176f
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
//    override fun render(stack: OmniMatrixStack, x: Float, y: Float, scaleX: Float, scaleY: Float, example: Boolean) {
//        if (!OmniTextureManager.INSTANCE.isTextureLoaded(INVENTORY_BACKGROUND)) {
//            OmniGameRendering.drawText(
//                stack = stack,
//                text = "Couldn't load inventory background",
//                x = (width / 2f) * scaleX + x,
//                y = (height / 2f) * scaleY + y,
//                color = Color.WHITE.rgb
//            )
//
//            return
//        }
//
//        stack.push()
//        stack.scale(scaleX, scaleY, 1f)
//        stack.translate(x * 1f / scaleX, y * 1f / scaleY, 0f)
//        drawInventoryBackground(stack)
//        OmniGameRendering.drawText(stack, if (type == 0) "Inventory" else "Ender Chest", 8f, 6f, 0)
//        drawItems(stack)
//        stack.pop()
//    }
//
//    override fun update(): Boolean {
//        return false
//    }
//
//    override fun hasBackground(): Boolean {
//        return false
//    }
//
//    @Suppress("RemoveEmptyParenthesesFromLambdaCall")
//    private fun drawInventoryBackground(stack: OmniMatrixStack) {
//        //#if MC >= 1.20.1
//        //$$ val ctx = smuggledHudDrawContext ?: return
//        //#endif
//
//        ImmediateScreenRenderer.render(
//            //#if MC >= 1.20.1
//            //$$ ctx
//            //#endif
//        ) {
//            val glId = OmniTextureManager.INSTANCE.getOrLoadId(INVENTORY_BACKGROUND)
//            drawInventorySlots(glId, stack)
//            drawInventoryLabel(glId, stack)
//        }
//    }
//
//    private fun drawInventorySlots(glId: Int, stack: OmniMatrixStack) {
//        val width = 176.0
//        val height = 166.0
//
//        val buffer = OmniBufferBuilder.create(DrawModes.QUADS, VertexFormats.POSITION_TEXTURE_COLOR)
//        buffer.vertex(stack, 0.0, height, 0.0)
//            .texture(0.0, 1.0)
//            .color(255, 255, 255, 255)
//            .next()
//        buffer.vertex(stack, width, height, 0.0)
//            .texture(1.0, 1.0)
//            .color(255, 255, 255, 255)
//            .next()
//        buffer.vertex(stack, width, 0.0, 0.0)
//            .texture(1.0, 0.0)
//            .color(255, 255, 255, 255)
//            .next()
//        buffer.vertex(stack, 0.0, 0.0, 0.0)
//            .texture(0.0, 0.0)
//            .color(255, 255, 255, 255)
//            .next()
//        buffer.build()?.drawWithCleanup(PIPELINE) {
//            texture(0, glId)
//        }
//    }
//
//    private fun drawInventoryLabel(glId: Int, stack: OmniMatrixStack) {
//        val buffer = OmniBufferBuilder.create(DrawModes.QUADS, VertexFormats.POSITION_TEXTURE_COLOR)
//        buffer.vertex(stack, 8.0, 6.0, 0.0)
//            .texture(0.0, 0.0)
//            .color(255, 255, 255, 255)
//            .next()
//        buffer.vertex(stack, 168.0, 6.0, 0.0)
//            .texture(1.0, 0.0)
//            .color(255, 255, 255, 255)
//        buffer.vertex(stack, 168.0, 26.0, 0.0)
//            .texture(1.0, 1.0)
//            .color(255, 255, 255, 255)
//            .next()
//        buffer.vertex(stack, 8.0, 26.0, 0.0)
//            .texture(0.0, 1.0)
//            .color(255, 255, 255, 255)
//        buffer.build()?.drawWithCleanup(PIPELINE) {
//            texture(0, glId)
//        }
//    }
//
//    @Suppress("RemoveEmptyParenthesesFromLambdaCall")
//    private fun drawItems(stack: OmniMatrixStack) {
//        val inventory = inventory ?: return
//        //#if MC >= 1.20.1
//        //$$ // val ctx = smuggledHudDrawContext ?: return
//        //#endif
//
////        ImmediateScreenRenderer.render(
//            //#if MC >= 1.20.1
//            //$$ //ctx
//            //#endif
////        ) {
//            RenderHelper.enableGUIStandardItemLighting()
//            val base = if (type == 0) 9 else 0
//            for (i in base until base + 27) {
//                val row = i / 9
//                val column = i % 9
//                val item = inventory.getStackInSlot(i)
//                if (item != null) {
//                    val itemX = 8 + column * 18
//                    val itemY = row * 18
//                    drawItem(
//                        stack = stack,
//                        itemStack = item,
//                        x = itemX, y = itemY
//                    )
//                }
//            }
//
//            RenderHelper.disableStandardItemLighting()
////        }
//    }
//
//    private fun drawItem(
//        stack: OmniMatrixStack,
//        itemStack: ItemStack,
//        x: Int,
//        y: Int
//    ) {
//        stack.push()
//        //#if MC >= 1.20.1
//        //$$ val fontRenderer = OmniClient.fontRenderer
//        //$$ smuggledHudDrawContext?.drawItem(itemStack, x, y)
//        //$$
//        //$$ smuggledHudDrawContext?.drawItemInSlot(
//        //$$     fontRenderer,
//        //$$     itemStack,
//        //$$     x, y
//        //$$ )
//        //#elseif MC >= 1.16.5
//        //$$ val fontRenderer = OmniClient.fontRenderer
//        //$$ val itemRenderer = OmniClient.getInstance().itemRenderer
//        //$$ itemRenderer.renderGuiItem(
//        //#if MC >= 1.19.4
//        //$$     stack.toVanillaStack(),
//        //#endif
//        //$$     itemStack,
//        //$$     x, y
//        //$$ )
//        //$$
//        //$$ itemRenderer.renderGuiItemDecorations(
//        //#if MC >= 1.19.4
//        //$$     stack.toVanillaStack(),
//        //#endif
//        //$$     fontRenderer,
//        //$$     itemStack,
//        //$$     x, y
//        //$$ )
//        //#else
//        val itemRenderer = OmniClient.getInstance().renderItem
//        itemRenderer.renderItemAndEffectIntoGUI(itemStack, x, y)
//
//        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
//        itemRenderer.renderItemOverlayIntoGUI(OmniClient.fontRenderer, itemStack, x, y, null)
//        //#endif
//        stack.pop()
//    }
//}
