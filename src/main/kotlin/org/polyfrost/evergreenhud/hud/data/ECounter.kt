package org.polyfrost.evergreenhud.hud.data

import org.polyfrost.evergreenhud.ECounterEvent
import org.polyfrost.oneconfig.api.config.v1.annotations.Switch
import org.polyfrost.oneconfig.api.event.v1.eventHandler
import org.polyfrost.oneconfig.api.hud.v1.TextHud

// CHECK OK
class ECounter : TextHud("E: ") {
    @Switch(title = "Show total entities")
    var showTotal = true

    override fun initialize() {
        if (isReal) {
            updateWhenChanged("showTotal")
        }
        eventHandler { ev: ECounterEvent ->
            sb.append(ev.rendered)
            if (showTotal) sb.append('/').append(ev.total)
            updateAndRecalculate()
        }
        super.initialize()
    }

    override fun getText() = null

    override fun title() = "E Counter"

    override fun id() = "entity_count.json"

    override fun category() = Category.INFO

}