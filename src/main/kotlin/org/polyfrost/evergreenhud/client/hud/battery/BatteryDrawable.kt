package org.polyfrost.evergreenhud.client.hud.battery

import org.polyfrost.evergreenhud.client.utils.battery.Battery
import org.polyfrost.polyui.color.Colors
import org.polyfrost.polyui.color.PolyColor
import org.polyfrost.polyui.color.rgba
import org.polyfrost.polyui.component.Drawable
import org.polyfrost.polyui.unit.Vec2

class BatteryDrawable(
    var battery: Battery = Battery.get(),
    at: Vec2 = Vec2.Constants.ZERO,
    visibleSize: Vec2 = Vec2.Constants.ZERO,
    palette: Colors.Palette? = null
) : Drawable(
    at = at,
    size = Vec2(60f, 25f), // Default size, can be adjusted
    visibleSize = visibleSize,
    palette = palette
) {

    private companion object {
        const val BORDER = 2f
        const val RADIUS_OUTER = 5f
        const val RADIUS_INNER = 3f
        const val FONT_SIZE = 16f
    }

    override fun render() {
        val clampedPercentage = battery.percentage.coerceIn(0, 100)
        val font = polyUI.fonts.bold
        val text = "$clampedPercentage%"

        val (backgroundColor, innerColor) = when {
            battery.isCharging -> polyUI.colors.state.success.run { normal to hovered }
            battery.isBatterySaverEnabled -> polyUI.colors.state.warning.run { normal to hovered }
            else -> polyUI.colors.text.primary.run { normal to hovered }
        }

        renderer.hollowRect(
            x, y, width, height,
            backgroundColor,
            lineWidth = BORDER,
            radius = RADIUS_OUTER
        )

        val fillWidth = (width - 2 * BORDER) * (clampedPercentage / 100f)
        renderer.rect(
            x + BORDER, y + BORDER,
            fillWidth,
            height - 2 * BORDER,
            innerColor,
            radius = RADIUS_INNER
        )

        val (textWidth, textHeight) = renderer.textBounds(
            font = font,
            text = text,
            fontSize = FONT_SIZE
        )

        renderer.text(
            font = font,
            x = x + (width - textWidth) / 2f,
            y = y + (height - textHeight) / 2f,
            text = text,
            color = invert(polyUI.colors.text.primary.normal),
            fontSize = FONT_SIZE,
        )
    }

    private fun invert(color: PolyColor): PolyColor {
        return rgba(
            255 - color.r,
            255 - color.g,
            255 - color.b,
            color.alpha
        )
    }

}
