package org.polyfrost.evergreenhud.hud

import net.minecraft.client.Minecraft
import org.polyfrost.oneconfig.api.hud.v1.TextHud
import org.polyfrost.polyui.unit.seconds

class Day : TextHud("Day: ", "") {
    override fun getText(): String {
        return Minecraft.getMinecraft().theWorld?.worldTime?.div(24000L)?.toString() ?: "0"
    }

    override fun updateFrequency() = 5.seconds

    override fun title() = "Day"

    override fun id() = "evergreenhud/day.json"

    override fun category() = Category.INFO
}