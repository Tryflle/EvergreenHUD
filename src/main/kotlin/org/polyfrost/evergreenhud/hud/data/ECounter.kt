package org.polyfrost.evergreenhud.hud.data

import org.polyfrost.evergreenhud.ECounterEvent
import org.polyfrost.oneconfig.api.config.v1.annotations.Switch
import org.polyfrost.oneconfig.api.event.v1.eventHandler
import org.polyfrost.oneconfig.api.hud.v1.TextHud

class ECounter : TextHud("E: ") {
    @Switch(title = "Show total entities")
    var showTotal = true

    private var renderedEntities: Int = 0
    private var totalEntities: Int = 0

    override fun initialize() {
        if (isReal) {
            updateWhenChanged("showTotal")
        }
        eventHandler { ev: ECounterEvent ->
            this.renderedEntities = ev.rendered
            this.totalEntities = ev.total
            updateAndRecalculate()
        }
        super.initialize()
    }

    override fun getText(): String? {
        sb.append(renderedEntities)
        if (showTotal) sb.append('/').append(totalEntities)
        return null
    }

    override fun title() = "E Counter"

    override fun id() = "entity_count.json"

    override fun category() = Category.INFO

}