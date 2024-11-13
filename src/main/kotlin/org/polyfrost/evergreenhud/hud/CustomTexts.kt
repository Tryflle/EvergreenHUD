package org.polyfrost.evergreenhud.hud

import org.polyfrost.evergreenhud.hud.hudlist.HudList
import org.polyfrost.oneconfig.api.config.v1.annotations.*
import org.polyfrost.oneconfig.api.config.v1.data.*
import org.polyfrost.oneconfig.api.config.v1.elements.OptionPage
import org.polyfrost.oneconfig.hud.TextHud
import org.polyfrost.evergreenhud.config.HudConfig
import java.lang.reflect.Field

class CustomTexts : HudConfig("Custom Texts", "evergreenhud/customtexts.json", false) {
    @CustomOption
    var huds = TextHudList()

    init {
        initialize()
    }

    override fun getCustomOption(
        field: Field, annotation: CustomOption, page: OptionPage, mod: Mod, migrate: Boolean
    ) = huds.addOptionTo(this, page)

    class TextHudList : HudList<CustomTextHud>() {
        override fun newHud() = CustomTextHud()
        override fun getHudName(hud: CustomTextHud) = hud.text
    }

    class CustomTextHud : TextHud(true, 180, 30) {
        @Text(name = "Text")
        var text = "Custom Text"

        override fun getLines(lines: MutableList<String>, example: Boolean) {
            lines.add(text)
        }
    }

}