package org.polyfrost.evergreenhud.hud.data

import org.polyfrost.oneconfig.api.config.v1.annotations.Switch
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
        super.initialize()
    }

    fun update(renderedEntities: Int, totalEntities: Int) {
        this.renderedEntities = renderedEntities
        this.totalEntities = totalEntities
        updateAndRecalculate()
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