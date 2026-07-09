package org.polyfrost.evergreenhud.client.hud

//? if < 26
import net.minecraft.client.gui.GuiGraphics
//? if >= 26
/*import net.minecraft.client.gui.GuiGraphicsExtractor as GuiGraphics*/
import net.minecraft.client.gui.screens.inventory.InventoryScreen
import net.minecraft.util.Mth
import org.polyfrost.oneconfig.api.config.v1.annotations.Slider
import org.polyfrost.oneconfig.api.config.v1.annotations.Switch
import org.polyfrost.oneconfig.api.hud.v1.LegacyHud
import org.polyfrost.oneconfig.utils.v1.dsl.mc

class PlayerPreviewHud : LegacyHud(
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

    override val width get() = 80f
    override val height get() = 120f

    override fun setup() {
        super.setup()
        staticWidth = true
        if (isReal) {
            hideIf("rotation") { paperDoll }
            hideIf("pitch") { paperDoll }
        }
    }

    override fun update() = false

    override fun render(graphics: GuiGraphics) {
        graphics.fill(0, 0, width.toInt(), height.toInt(), 0x90000000.toInt())

        val player = mc.player ?: return

        val scale = effectiveScale
        val x1 = x.toInt()
        val y1 = y.toInt()
        val x2 = (x + width * scale).toInt()
        val y2 = (y + height * scale).toInt()
        val entityScale = (40f * scale).toInt()

        val centerX = (x1 + x2) / 2f
        val centerY = (y1 + y2) / 2f

        val yawOffset: Float
        val pitchOffset: Float
        if (paperDoll) {
            yawOffset = Mth.wrapDegrees(player.yHeadRot - player.yBodyRot)
            pitchOffset = player.xRot
        } else {
            yawOffset = rotation - 180f
            pitchOffset = pitch
        }

        //? if < 1.21.8 {
        /*
        graphics.pose().pushPose()
        graphics.pose().last().pose().identity()*/
        //?}
        //? if < 26
        InventoryScreen.renderEntityInInventoryFollowsMouse(
        //? if >= 26
        /*InventoryScreen.extractEntityInInventoryFollowsMouse(*/
            graphics,
            x1, y1, x2, y2,
            entityScale,
            0.0625f,
            centerX - yawOffset,
            centerY - pitchOffset,
            player,
        )
        //? if < 1.21.8
        /*graphics.pose().popPose()*/
    }
}
