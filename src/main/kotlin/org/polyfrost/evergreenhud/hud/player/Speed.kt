package org.polyfrost.evergreenhud.hud.player

import net.minecraft.client.Minecraft
import org.polyfrost.evergreenhud.hud.GenericHUD1f
import org.polyfrost.oneconfig.api.config.v1.annotations.Dropdown
import org.polyfrost.oneconfig.api.config.v1.annotations.Switch
import kotlin.math.sqrt

class Speed : GenericHUD1f("Speed", "m/s") {
    @Switch(title = "Use X")
    var useX = true

    @Switch(title = "Use Y")
    var useY = true

    @Switch(title = "Use Z")
    var useZ = true

    @Dropdown(
        title = "Speed Unit",
        options = ["Meters per tick", "Meters per second", "Kilometers per hour", "Miles per hour"],
    )
    var speedUnit = 0

    override fun initialize() {
        if (isReal) {
            addCallback("speedUnit") { value: Int ->
                suffix = when (value) {
                    1 -> "m/s"
                    2 -> "kph"
                    3 -> "mph"
                    else -> "m/t"
                }
                updateAndRecalculate()
                false
            }
            updateWhenChanged("useX")
            updateWhenChanged("useY")
            updateWhenChanged("useZ")
        }
        super.initialize()
    }

    private fun convertSpeed(speed: Float): Float = when (speedUnit) {
        1 -> speed * 20f
        2 -> speed * 3.6f * 20f
        3 -> speed * 2.237f * 20f
        else -> speed
    }

    override fun getText(): String? {
        val player = Minecraft.getMinecraft().thePlayer
        if (player == null) {
            value = 0f
        } else {
            val dx = if (useX) (player.posX - player.prevPosX).toFloat() else 0f
            val dy = if (useY) (player.posY - player.prevPosY).toFloat() else 0f
            val dz = if (useZ) (player.posZ - player.prevPosZ).toFloat() else 0f

            // I usually don't leave out whitespaces, but in this case it greatly improved readability
            value = convertSpeed(sqrt(dx * dx + dy * dy + dz * dz))
        }
        return super.getText()
    }

}