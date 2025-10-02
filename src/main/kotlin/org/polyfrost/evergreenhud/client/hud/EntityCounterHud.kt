package org.polyfrost.evergreenhud.client.hud

import org.polyfrost.evergreenhud.client.EntityCounterEvent
import org.polyfrost.oneconfig.api.config.v1.annotations.Switch
import org.polyfrost.oneconfig.api.event.v1.eventHandler
import org.polyfrost.oneconfig.api.hud.v1.TextHud

// CHECK OK
class EntityCounterHud : TextHud(
    id = "entity_counter.json",
    title = "Entity Counter",
    category = Category.INFO,
    prefix = "E: ",
) {
    @Switch(title = "Show total entities")
    var showTotal = true

    override fun setup() {
        super.setup()
        eventHandler { ev: EntityCounterEvent ->
            sb.append(ev.rendered)
            if (showTotal) {
                sb.append('/').append(ev.total)
            }

            updateAndRecalculate()
        }

        if (isReal) {
            updateWhenChanged("showTotal")
        }
    }

    override fun getText(): String? {
        return null
    }
}
