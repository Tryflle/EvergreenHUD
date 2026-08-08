package org.polyfrost.evergreenhud.client.hud

import androidx.compose.runtime.Composable
import net.minecraft.util.Mth
import org.polyfrost.compose.composables.PolyBox
import org.polyfrost.compose.composables.PolyCanvas
import org.polyfrost.compose.composables.PolyModifier
import org.polyfrost.compose.composables.size
//? if >= 1.21.10 {
import org.polyfrost.evergreenhud.client.hooks.PlayerPreviewOffscreen
//? } else {
/*import net.minecraft.client.gui.screens.inventory.InventoryScreen
import org.polyfrost.evergreenhud.client.hooks.HudOffscreen
import org.polyfrost.evergreenhud.client.hooks.playerPreviewPartialTick
*///? }
import org.polyfrost.evergreenhud.client.hooks.smuggledHudPartialTick
import org.polyfrost.oneconfig.api.config.v1.annotations.Slider
import org.polyfrost.oneconfig.api.config.v1.annotations.Switch
import org.polyfrost.oneconfig.api.hud.v1.Hud
import org.polyfrost.oneconfig.api.platform.v1.Platform
import org.polyfrost.oneconfig.utils.v1.dsl.mc

private const val WIDTH = 80f
private const val HEIGHT = 120f

private const val DEFAULT_ENTITY_SCALE = 40f

class PlayerPreviewHud : Hud(
    id = "player_preview.json",
    title = "Player Preview",
    category = Category.PLAYER,
) {
    @Switch(title = "Paper Doll", description = "Mirror the player's own head rotation.")
    var paperDoll = false

    @Slider(title = "Rotation", min = 0F, max = 360F, step = 1F)
    var rotation = 180f

    @Slider(title = "Pitch", min = -90F, max = 90F, step = 1F)
    var pitch = 0f

    @Slider(title = "Model Scale", description = "Size of the player inside the HUD.", min = 10F, max = 80F, step = 1F)
    var modelScale = DEFAULT_ENTITY_SCALE

    @Slider(title = "Vertical Anchor", description = "Where the player sits inside the HUD, top to bottom.", min = 0F, max = 1F, step = 0.05F)
    var verticalAnchor = 0.5f

    override fun defaultPosition(): Pair<Float, Float> = 0f to 0f

    override fun canMergeBackground(): Boolean = true

    override fun minimumSize(): Pair<Float, Float> = WIDTH to HEIGHT

    override val alwaysRedraw: Boolean get() = true

    override fun setup() {
        staticWidth = true
        staticW = WIDTH
        staticH = HEIGHT
        if (isReal) {
            hideIf("rotation") { paperDoll }
            hideIf("pitch") { paperDoll }
        }
    }

    override fun update(): Boolean {
        if (!isReal) return false
        return queue(effectiveScale)
    }

    private fun queue(scale: Float): Boolean {
        val player = mc.player ?: return false
        val partialTick = smuggledHudPartialTick

        val bodyLag = if (paperDoll) {
            Mth.wrapDegrees(Mth.rotLerp(partialTick, player.yBodyRotO, player.yBodyRot) - player.yRot)
        } else {
            0f
        }

        val bodyRot: Float
        val headRot: Float?
        val headPitch: Float?
        val modelTilt: Float
        if (paperDoll) {
            bodyRot = 180f + bodyLag
            headRot = null
            headPitch = null
            modelTilt = 0f
        } else {
            bodyRot = rotation
            headRot = 0f
            headPitch = -pitch
            modelTilt = pitch
        }

        //? if >= 1.21.10 {
        val pixelsPerGuiUnit = Platform.screen().let {
            if (it.guiWidth() > 0) it.viewportWidth().toFloat() / it.guiWidth() else 1f
        }
        val pixelScale = (scale * pixelsPerGuiUnit).coerceAtLeast(0.0001f)
        PlayerPreviewOffscreen.submit(
            this,
            PlayerPreviewOffscreen.Request(
                widthPx = (WIDTH * pixelScale).toInt().coerceAtLeast(1),
                heightPx = (HEIGHT * pixelScale).toInt().coerceAtLeast(1),
                sizePx = modelScale * pixelScale,
                bodyRot = bodyRot,
                headRot = headRot,
                headPitch = headPitch,
                modelTilt = modelTilt,
                partialTick = partialTick,
                verticalAnchor = verticalAnchor,
            ),
        )
        //? } else {
        /*val x1 = x.toInt()
        val y1 = y.toInt()
        val x2 = (x + WIDTH * scale).toInt()
        val y2 = (y + HEIGHT * scale).toInt()
        val centerX = (x1 + x2) / 2f
        val centerY = (y1 + y2) / 2f
        val entityScale = (modelScale * scale).toInt()
        val yawOffset = if (paperDoll) bodyLag else rotation - 180f

        HudOffscreen.submit { graphics ->
            playerPreviewPartialTick = partialTick
            try {
                InventoryScreen.renderEntityInInventoryFollowsMouse(
                    graphics,
                    x1, y1, x2, y2,
                    entityScale,
                    0.0625f,
                    centerX - yawOffset,
                    centerY + pitch,
                    player,
                )
            } finally {
                playerPreviewPartialTick = -1f
            }
        }
        *///? }
        return false
    }

    @Composable
    override fun Content() {
        PolyBox(modifier = hudBackground().size(WIDTH, HEIGHT)) {
            PolyCanvas(PolyModifier.size(WIDTH, HEIGHT)) { _, _, w, h ->
                //? if >= 1.21.10 {
                if (!isReal) queue(1f)
                PlayerPreviewOffscreen.drawInto(this@PlayerPreviewHud, canvas, w, h)
                //? } else {
                /*HudOffscreen.drawInto(canvas, x, y, effectiveScale, w, h)
                *///? }
            }
        }
    }
}
