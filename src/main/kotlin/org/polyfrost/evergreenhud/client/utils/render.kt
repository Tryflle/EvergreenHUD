package org.polyfrost.evergreenhud.client.utils

//#if MC >= 1.20.1
//$$ import dev.deftu.omnicore.client.OmniClient
//$$ import dev.deftu.omnicore.client.render.OmniMatrixStack
//$$ import net.minecraft.client.MinecraftClient
//$$ import net.minecraft.client.gui.DrawContext
//$$ import net.minecraft.client.render.VertexConsumerProvider
//$$ import net.minecraft.client.util.math.MatrixStack
//#endif

//#if MC >= 1.20.1
//$$ private val CONSTRUCTOR = DrawContext::class.java.getDeclaredConstructor(
//$$     MinecraftClient::class.java,
//$$     MatrixStack::class.java,
//$$     VertexConsumerProvider.Immediate::class.java
//$$ ).also { it.isAccessible = true }
//$$
//$$ fun OmniMatrixStack.createDrawContext(): DrawContext {
//$$     val client = OmniClient.getInstance()
//$$     return CONSTRUCTOR.newInstance(
//$$         client,
//$$         toVanillaStack(),
//$$         client.bufferBuilders.entityVertexConsumers
//$$     )
//$$ }
//#endif
