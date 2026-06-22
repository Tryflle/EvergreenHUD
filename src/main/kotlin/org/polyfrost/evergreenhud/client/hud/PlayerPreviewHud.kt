package org.polyfrost.evergreenhud.client.hud

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.InventoryScreen
import org.polyfrost.oneconfig.api.config.v1.annotations.Slider
import org.polyfrost.oneconfig.api.hud.v1.LegacyHud
import org.polyfrost.oneconfig.utils.v1.dsl.mc

class PlayerPreviewHud : LegacyHud(
    id = "player_preview.json",
    title = "Player Preview",
    category = Category.PLAYER,
) {
    @Slider(title = "Rotation", min = 0F, max = 360F, step = 1F)
    var rotation = 180f

    override val width get() = 80f
    override val height get() = 120f

    override fun setup() {
        super.setup()
        staticWidth = true
    }

    override fun update() = false

    override fun render(graphics: GuiGraphics) {
        graphics.fill(0, 0, width.toInt(), height.toInt(), 0x90000000.toInt())

        val player = mc.player ?: return

        //? if >= 1.21.8 {
        val scale = effectiveScale
        val x1 = x.toInt()
        val y1 = y.toInt()
        val x2 = (x + width * scale).toInt()
        val y2 = (y + height * scale).toInt()
        val entityScale = (40f * scale).toInt()
        //?} else {
        /*val x1 = 0
        val y1 = 0
        val x2 = width.toInt()
        val y2 = height.toInt()
        val entityScale = 40
        *///?}

        val centerX = (x1 + x2) / 2f
        val mouseX = centerX - (rotation - 180f)
        InventoryScreen.renderEntityInInventoryFollowsMouse(
            graphics,
            x1, y1, x2, y2,
            entityScale,
            0.0625f,
            mouseX,
            0f,
            player,
        )
    }
}
