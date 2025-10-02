package org.polyfrost.evergreenhud.client.hud

import dev.deftu.omnicore.api.client.player
import dev.deftu.omnicore.api.entity.currentX
import dev.deftu.omnicore.api.entity.currentY
import dev.deftu.omnicore.api.entity.currentZ
import dev.deftu.omnicore.api.entity.prevX
import dev.deftu.omnicore.api.entity.prevY
import dev.deftu.omnicore.api.entity.prevZ
import org.polyfrost.evergreenhud.client.utils.GenericNumberHud
import org.polyfrost.oneconfig.api.config.v1.annotations.Dropdown
import org.polyfrost.oneconfig.api.config.v1.annotations.Switch
import org.polyfrost.polyui.unit.milliseconds
import kotlin.math.sqrt

// CHECK OK
class SpeedHud : GenericNumberHud(
    title = "Speed",
    category = Category.INFO,
    suffix = "m/s"
) {
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
    var speedUnit = 1

    override fun setup() {
        super.setup()

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
    }

    override fun getText(): String? {
        val player = player
        if (player == null) {
            value = 0f
            return null
        }

        val dx = if (useX) (player.currentX - player.prevX).toFloat() else 0f
        val dy = if (useY) (player.currentY - player.prevY).toFloat() else 0f
        val dz = if (useZ) (player.currentZ - player.prevZ).toFloat() else 0f
        value = convertSpeed(sqrt(dx * dx + dy * dy + dz * dz))

        return super.getText()
    }

    override fun updateFrequency(): Long {
        return 50.milliseconds
    }

    private fun convertSpeed(speed: Float): Float {
        return when (speedUnit) {
            1 -> speed * 20f
            2 -> speed * 3.6f * 20f
            3 -> speed * 2.237f * 20f
            else -> speed
        }
    }
}
