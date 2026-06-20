package org.polyfrost.evergreenhud.client.hud

import org.polyfrost.evergreenhud.client.EntityCounterEvent
import org.polyfrost.evergreenhud.client.utils.CachedTextHud
import org.polyfrost.oneconfig.api.config.v1.annotations.Switch
import org.polyfrost.oneconfig.api.event.v1.eventHandler
import org.polyfrost.oneconfig.api.hud.v1.TextHud

class EntityCounterHud : CachedTextHud(
    title = "Entity Counter",
    category = Category.INFO,
    prefix = "E:",
    defaultText = "0"
) {
    @Switch(title = "Show total entities")
    var showTotal = true

    override fun setup() {
        super.setup()
        eventHandler { ev: EntityCounterEvent ->
            val text = buildString {
                append(ev.rendered)
                if (showTotal) {
                    append('/').append(ev.total)
                }
            }

            updateWithText(text)
        }

        if (isReal) {
            updateWhenChanged("showTotal")
        }
    }
}
