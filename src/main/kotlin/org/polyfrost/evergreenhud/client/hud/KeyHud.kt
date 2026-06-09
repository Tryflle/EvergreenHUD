package org.polyfrost.evergreenhud.client.hud

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animate
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import org.polyfrost.compose.composables.PolyBox
import org.polyfrost.compose.composables.PolyModifier
import org.polyfrost.compose.composables.PolyText
import org.polyfrost.compose.composables.background
import org.polyfrost.compose.composables.padding
import org.polyfrost.compose.render.PolyColor
import org.polyfrost.evergreenhud.client.utils.toComposeColor
import org.polyfrost.evergreenhud.client.utils.toPolyColor
import org.polyfrost.oneconfig.api.config.v1.annotations.Color
import org.polyfrost.oneconfig.api.config.v1.annotations.Keybind
import org.polyfrost.oneconfig.api.config.v1.annotations.Slider
import org.polyfrost.oneconfig.api.config.v1.annotations.Text as TextOption
import org.polyfrost.oneconfig.api.hud.v1.Hud
import org.polyfrost.oneconfig.api.ui.v1.keybind.KeyModifiers
import org.polyfrost.oneconfig.api.ui.v1.keybind.OneConfigKeybind

//todo uh
class KeyHud : Hud(
    id = "key.json",
    title = "Key HUD",
    category = Category.INFO,
) {
    @TextOption(title = "Key Text")
    var keyText = "W"

    @Keybind(title = "Key")
    var key = OneConfigKeybind(null, null, KeyModifiers.NONE, 0L) {
        s -> state.value = s
        false
    }

    @Color(title = "Normal Text Color")
    var normalText = PolyColor.rgba(255, 255, 255, 255)

    @Color(title = "Pressed Text Color")
    var pressedText = PolyColor.rgba(0, 0, 0, 255)

    @Color(title = "Normal Background Color")
    var normalBackground = PolyColor.rgba(0, 0, 0, 100)

    @Color(title = "Pressed Background Color")
    var pressedBackground = PolyColor.rgba(255, 255, 255, 100)

    @Slider(title = "Animation Duration (ms)", min = 0f, max = 2000f)
    var pressDuration = 100f



    private var state = mutableStateOf(true)



    @Composable
    override fun Content() {
        val textColor by animateColorAsState(
            if (state.value) pressedText.toComposeColor() else normalText.toComposeColor(),
            label = "text"
        )
        val backgroundColor by animateColorAsState(
            if (state.value) pressedBackground.toComposeColor() else normalBackground.toComposeColor(),
            label = "background"
        )

        PolyBox(
            PolyModifier.background(backgroundColor.toPolyColor(), radius = 4f).padding(6f)
        ) {
            PolyText(keyText, textColor.toPolyColor())
        }
    }

    override fun setup() {
        super.setup()
        if (isReal) {
            addCallback("key") {
                keyText = keybindDisplayName(key)
                updateAndRecalculate()
            }

            updateWhenChanged("keyText")
        }

    }

    override fun update(): Boolean {
        return true
    }

    /** Human-readable name for a GLFW key code. */
    private fun keyCodeToName(glfwCode: Int): String = when (glfwCode) {
        -1 -> "None"
        32 -> "Space"
        256 -> "Escape"
        257 -> "Enter"
        258 -> "Tab"
        259 -> "Backspace"
        260 -> "Insert"
        261 -> "Delete"
        262 -> "Right"
        263 -> "Left"
        264 -> "Down"
        265 -> "Up"
        266 -> "Page Up"
        267 -> "Page Down"
        268 -> "Home"
        269 -> "End"
        280 -> "Caps Lock"
        340 -> "Left Shift"
        344 -> "Right Shift"
        341 -> "Left Ctrl"
        345 -> "Right Ctrl"
        342 -> "Left Alt"
        346 -> "Right Alt"
        343 -> "Left Super"
        347 -> "Right Super"
        in 48..57 -> ('0' + (glfwCode - 48)).toString()
        in 65..90 -> ('A' + (glfwCode - 65)).toString()
        in 290..301 -> "F${glfwCode - 289}"
        else -> "Key $glfwCode"
    }

    private fun keybindDisplayName(keybind: OneConfigKeybind?): String {
        if (keybind == null || !keybind.isBound) return "None"
        // LinkedHashSet dedups the left/right entries that a single modifier expands into (e.g. "Shift" + "Shift").
        val parts = LinkedHashSet<String>()
        keybind.keyCodes?.forEach { parts += keyCodeToName(it) }
        keybind.mouseBtns?.forEach { parts += "Mouse ${it + 1}" }
        return parts.joinToString(" + ").ifEmpty { "None" }
    }
}
