package org.polyfrost.evergreenhud.hud.player

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.renderer.RenderHelper
import org.polyfrost.oneconfig.api.config.v1.annotations.Slider
import org.polyfrost.oneconfig.api.config.v1.annotations.Switch
import org.polyfrost.oneconfig.api.hud.v1.LegacyHud
import org.polyfrost.universal.UMatrixStack
import net.minecraft.client.renderer.GlStateManager as GL

class PlayerPreview : LegacyHud() {

    @Switch(title = "Show Nametag")
    var showNametag = false

    @Slider(title = "Rotation", min = 0F, max = 360F)
    var rotation = 20f

    override var width: Float
        get() = 80f
        set(_) {}

    override var height: Float
        get() = 120f
        set(_) {}


    override fun render(stack: UMatrixStack, x: Float, y: Float, scaleX: Float, scaleY: Float) {
        // modified version of GuiInventory#drawEntityOnScreen
        // added scaling and removed mouse-based rotation, replaced with static rotation; added nametag rendering
        val ent = Minecraft.getMinecraft().thePlayer ?: return
        GL.enableColorMaterial()
        GL.pushMatrix()
        GL.translate(x + (40f * scaleX), y + (107f + if (showNametag) 26f else 0f) * scaleY, 50f)
        GL.scale(-(scaleX * 50f), scaleY * 50f, scaleX * 50f)
        GL.rotate(180.0f, 0.0f, 0.0f, 1.0f)
        val prevYawOffset = ent.renderYawOffset
        val prevYaw = ent.rotationYaw
        val prevPitch = ent.rotationPitch
        val prevPrevHeadYaw = ent.prevRotationYawHead
        val prevHeadYaw = ent.rotationYawHead
        GL.rotate(135.0f, 0.0f, 1.0f, 0.0f)
        RenderHelper.enableStandardItemLighting()
        GL.rotate(-135.0f, 0.0f, 1.0f, 0.0f)
        val actualRotation = 360F - rotation
        ent.renderYawOffset = actualRotation
        ent.rotationYaw = actualRotation
        ent.rotationYawHead = ent.rotationYaw
        ent.prevRotationYawHead = ent.rotationYaw
        val rm = Minecraft.getMinecraft().renderManager
        rm.playerViewX = 0f
        rm.setPlayerViewY(180.0f)
        rm.isRenderShadow = false
        rm.doRenderEntity(ent, 0.0, 0.0, 0.0, 0.0f, 1.0f, false)
        if (showNametag) playerRenderer.renderName(ent, 0.0, 0.0, 0.0)
        rm.isRenderShadow = true
        ent.renderYawOffset = prevYawOffset
        ent.rotationYaw = prevYaw
        ent.rotationPitch = prevPitch
        ent.prevRotationYawHead = prevPrevHeadYaw
        ent.rotationYawHead = prevHeadYaw
        GL.popMatrix()
        RenderHelper.disableStandardItemLighting()
        GL.disableRescaleNormal()
        GL.setActiveTexture(OpenGlHelper.lightmapTexUnit)
        GL.disableTexture2D()
        GL.setActiveTexture(OpenGlHelper.defaultTexUnit)
    }

    private val playerRenderer = Minecraft.getMinecraft().renderManager.skinMap["default"]!!

    override fun hasBackground() = false

    override fun title() = "Player Preview"

    override fun category() = Category.PLAYER

    override fun update() = false

}