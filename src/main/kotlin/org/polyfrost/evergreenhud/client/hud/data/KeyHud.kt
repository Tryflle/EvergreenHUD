package org.polyfrost.evergreenhud.client.hud.data

import org.polyfrost.oneconfig.api.config.v1.annotations.Color
import org.polyfrost.oneconfig.api.config.v1.annotations.Keybind
import org.polyfrost.oneconfig.api.config.v1.annotations.Slider
import org.polyfrost.oneconfig.api.config.v1.annotations.Text as TextOption
import org.polyfrost.oneconfig.api.hud.v1.Hud
import org.polyfrost.polyui.animate.Animations
import org.polyfrost.polyui.color.rgba
import org.polyfrost.polyui.component.impl.Block
import org.polyfrost.polyui.component.impl.Text
import org.polyfrost.polyui.input.KeyBinder
import org.polyfrost.polyui.operations.Recolor
import org.polyfrost.polyui.unit.milliseconds

//todo uh
class KeyHud : Hud<Text>(
    id = "key.json",
    title = "Key HUD",
    category = Category.INFO,
) {

    @TextOption(title = "Key Text")
    var keyText = "W"

    @Keybind(title = "Key")
    var key = KeyBinder.Bind('W') { s -> state = s; false }

    @Color(title = "Normal Text Color")
    var normalText = rgba(255, 255, 255, 1f)

    @Color(title = "Pressed Text Color")
    var pressedText = rgba(0, 0, 0, 1f)

    @Color(title = "Normal Background Color")
    var normalBackground = rgba(0, 0, 0, 0.4f)

    @Color(title = "Pressed Background Color")
    var pressedBackground = rgba(255, 255, 255, 0.4f)

    @Slider(title = "Animation Duration (ms)", min = 0f, max = 2000f)
    var pressDuration = 100f

    private var state = false
        set(value) {
            if (field == value) return
            field = value
            updateAndRecalculate()
        }

    override fun setup() {
        super.setup()
        if (isReal) {
            addCallback("key") {
                keyText = key.keysToString()
                updateAndRecalculate()
            }

            updateWhenChanged("keyText")
        }
    }

    override fun create(): Text {
        return Text(keyText)
    }

    override fun update(): Boolean {
        // double it as we use the animation twice, once for the text and once for the background
        val animation = Animations.Default.create((pressDuration * 2f).milliseconds)
        get().text = keyText
        Recolor(get(), if (state) pressedText else normalText, animation).add()
        Recolor(getBackground() as Block, if (state) pressedBackground else normalBackground, animation).add()
        return false
    }

}
