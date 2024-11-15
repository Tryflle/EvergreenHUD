package org.polyfrost.evergreenhud.hud.data

import org.polyfrost.oneconfig.api.hud.v1.TextHud

class Biome : TextHud("Biome: ") {
    private var biome = "Unknown"

    fun update(biomeName: String?) {
        this.biome = biomeName ?: "Unknown"
    }

    override fun getText(): String? {
        sb.append(biome)
        return null
    }

    override fun id() = "evergreenhud/biome.json"

    override fun title() = "Biome"

    override fun category() = Category.INFO
}
