package org.polyfrost.evergreenhud.hud.hudlist

import org.polyfrost.oneconfig.api.config.v1.Config
import org.polyfrost.oneconfig.api.config.v1.core.ConfigUtils
import org.polyfrost.oneconfig.api.config.v1.elements.*
import org.polyfrost.oneconfig.hud.Hud

abstract class HudList<T : Hud> : ArrayList<T>() {
    abstract fun newHud(): T
    abstract fun getHudName(hud: T): String

    fun addOptionTo(config: Config, page: OptionPage, description: String = "", category: String = "General", subcategory: String = ""): BasicOption {
        val option = HudListOption(this, config, description, category, subcategory)
        ConfigUtils.getSubCategory(page, category, subcategory).options.add(option)
        return option
    }
}