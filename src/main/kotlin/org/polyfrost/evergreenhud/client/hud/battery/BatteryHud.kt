package org.polyfrost.evergreenhud.client.hud.battery

import org.polyfrost.evergreenhud.client.utils.battery.Battery
import org.polyfrost.oneconfig.api.hud.v1.Hud
import org.polyfrost.polyui.component.Drawable
import org.polyfrost.polyui.unit.seconds

class BatteryHud : Hud<BatteryDrawable>(
    id = "battery.json",
    title = "Battery",
    category = Category.INFO,
) {

    override fun create(): BatteryDrawable {
        return BatteryDrawable()
    }

    override fun update(): Boolean {
        get().battery = Battery.get()
        return false
    }

    override fun updateFrequency(): Long {
        return 1.seconds
    }

}