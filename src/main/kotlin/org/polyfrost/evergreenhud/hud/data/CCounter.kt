package org.polyfrost.evergreenhud.hud.data

import org.polyfrost.oneconfig.api.config.v1.annotations.Switch
import org.polyfrost.oneconfig.api.hud.v1.TextHud

class CCounter : TextHud("C: ") {
    @Switch(title = "Simplified")
    var simplified = true

    private var c: Int = 0

    override fun initialize() {
        if (isReal) {
            updateWhenChanged("simplified")
        }
        super.initialize()
    }

    fun update(c: Int) {
        this.c = c
    }

    override fun getText(): String? {
        sb.append(c)
        return null
    }

    override fun id() = "evergreenhud/ccounter.json"

    override fun title() = "C Counter"

    override fun category() = Category.INFO
}