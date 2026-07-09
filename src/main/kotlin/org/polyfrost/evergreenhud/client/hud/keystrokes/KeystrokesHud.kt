package org.polyfrost.evergreenhud.client.hud.keystrokes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import net.minecraft.client.KeyMapping
import org.polyfrost.compose.composables.PolyBox
import org.polyfrost.compose.composables.PolyCanvas
import org.polyfrost.compose.composables.PolyColumn
import org.polyfrost.compose.composables.PolyMcText
import org.polyfrost.compose.composables.PolyModifier
import org.polyfrost.compose.composables.PolyRow
import org.polyfrost.compose.composables.PolyText
import org.polyfrost.compose.composables.align
import org.polyfrost.compose.composables.background
import org.polyfrost.compose.composables.margin
import org.polyfrost.compose.composables.size
import org.polyfrost.compose.layout.PolyAlign
import org.polyfrost.compose.mc.McFontQueue
import org.polyfrost.compose.render.PolyColor
import org.polyfrost.oneconfig.api.config.v1.Property
import org.polyfrost.oneconfig.api.config.v1.annotations.Color
import org.polyfrost.oneconfig.api.config.v1.annotations.Slider
import org.polyfrost.oneconfig.api.config.v1.annotations.Switch
import org.polyfrost.oneconfig.api.hud.v1.Font
import org.polyfrost.oneconfig.api.hud.v1.Hud
import org.polyfrost.oneconfig.utils.v1.dsl.mc

private const val KEY = 16f
private const val GAP = 2f
private const val SPACE_H = 11f
private const val FONT = 8f

private const val UP = "▲"    // ▲
private const val DOWN = "▼"  // ▼
private const val LEFT = "◀"  // ◀
private const val RIGHT = "▶" // ▶

class KeystrokesHud : Hud(
    id = "keystrokes.json",
    title = "Keystrokes",
    category = Category.INFO,
) {

    @Switch(title = "Movement Keys")
    var showMovement = true

    @Switch(title = "Spacebar")
    var showJump = true

    @Switch(title = "Attack & Use")
    var showClicks = true

    @Switch(title = "Arrows", description = "Replace the movement keys with arrows.")
    var arrows = false

    @Slider(title = "Fade Duration (ms)", description = "How long a key takes to fade between the unpressed and pressed colours.", min = 1F, max = 250F, step = 1F)
    var fadeMs = 150f

    @Color(title = "Unpressed Background Color")
    var unpressedBg = PolyColor(0x6E000000)

    @Color(title = "Unpressed Text Color")
    var unpressedText = PolyColor(0xFFFFFFFF.toInt())

    @Color(title = "Pressed Background Color")
    var pressedBg = PolyColor(0xFFFFFFFF.toInt())

    @Color(title = "Pressed Text Color")
    var pressedText = PolyColor(0xFF000000.toInt())

    private val forward = mutableStateOf(0f)
    private val left = mutableStateOf(0f)
    private val back = mutableStateOf(0f)
    private val right = mutableStateOf(0f)
    private val jump = mutableStateOf(0f)
    private val attack = mutableStateOf(0f)
    private val use = mutableStateOf(0f)

    private val rev = mutableStateOf(0)

    private var lastNanos = System.nanoTime()

    override fun multipleInstancesAllowed() = false

    override fun setup() {
        super.setup()
        if (isReal) {
            addDependency("arrows", null) {
                if (font != Font.Minecraft) Property.Display.DISABLED else Property.Display.SHOWN
            }
            for (option in listOf("showMovement", "showJump", "showClicks", "arrows")) {
                addCallback(option) { rev.value++ }
            }
        }
    }

    override fun update(): Boolean {
        val o = mc.options ?: return false
        val now = System.nanoTime()
        val dtMs = (now - lastNanos).coerceAtLeast(0L) / 1_000_000f
        lastNanos = now
        val step = if (fadeMs <= 0f) 1f else dtMs / fadeMs
        var changed = false
        fun poll(state: MutableState<Float>, key: KeyMapping) {
            val target = if (key.isDown) 1f else 0f
            val cur = state.value
            val next = when {
                cur < target -> (cur + step).coerceAtMost(target)
                cur > target -> (cur - step).coerceAtLeast(target)
                else -> cur
            }
            if (next != cur) {
                state.value = next
                changed = true
            }
        }
        poll(forward, o.keyUp)
        poll(left, o.keyLeft)
        poll(back, o.keyDown)
        poll(right, o.keyRight)
        poll(jump, o.keyJump)
        poll(attack, o.keyAttack)
        poll(use, o.keyUse)
        return changed
    }

    @Composable
    override fun Content() {
        rev.value
        val o = mc.options ?: return
        val s = textScale.coerceAtLeast(0.01f)
        val key = KEY * s
        val gap = GAP * s
        val rowW = key * 3 + gap * 2
        val clickW = (rowW - gap) / 2f
        val spaceH = SPACE_H * s
        val useArrows = arrows && font == Font.Minecraft

        PolyColumn(gap = gap) {
            if (showMovement) {
                PolyBox(modifier = PolyModifier.size(rowW, key)) {
                    Key(if (useArrows) UP else o.keyUp.label(), forward.value, key, key, PolyAlign.Center)
                }
                PolyRow(gap = gap) {
                    Key(if (useArrows) LEFT else o.keyLeft.label(), left.value, key, key)
                    Key(if (useArrows) DOWN else o.keyDown.label(), back.value, key, key)
                    Key(if (useArrows) RIGHT else o.keyRight.label(), right.value, key, key)
                }
            }
            if (showJump) {
                SpaceKey(jump.value, rowW, spaceH)
            }
            if (showClicks) {
                PolyRow(gap = gap) {
                    Key("LMB", attack.value, clickW, key)
                    Key("RMB", use.value, clickW, key)
                }
            }
        }
    }

    @Composable
    private fun Key(label: String, progress: Float, w: Float, h: Float, align: PolyAlign? = null) {
        val fg = unpressedText.lerp(pressedText, progress)
        var mod = PolyModifier.size(w, h)
        if (showBackground) {
            val bg = unpressedBg.lerp(pressedBg, progress)
            mod = mod.background(bg, bgRadius)
        }
        if (align != null) mod = mod.align(align)
        PolyBox(modifier = mod) {
            if (font == Font.Minecraft) {
                val visibleW = (McFontQueue.measureTextWidth(label, textScale) - textScale).coerceAtLeast(0f)
                val visibleH = McFontQueue.measureTextHeight(textScale)
                val leftPad = ((w - visibleW) / 2f).coerceAtLeast(0f)
                val topPad = ((h - visibleH) / 2f).coerceAtLeast(0f)
                PolyMcText(
                    label,
                    color = fg,
                    shadow = showShadow,
                    scale = textScale,
                    modifier = PolyModifier.align(PolyAlign.TopLeft).margin(leftPad, topPad, 0f, 0f),
                )
            } else {
                PolyText(
                    label,
                    color = fg,
                    fontSize = FONT * textScale,
                    font = getPoppinsFontName(),
                    modifier = PolyModifier.align(PolyAlign.Center),
                )
            }
        }
    }

    @Composable
    private fun SpaceKey(progress: Float, w: Float, h: Float) {
        val fg = unpressedText.lerp(pressedText, progress)
        var mod = PolyModifier.size(w, h)
        if (showBackground) {
            val bg = unpressedBg.lerp(pressedBg, progress)
            mod = mod.background(bg, bgRadius)
        }
        PolyBox(modifier = mod) {
            PolyCanvas(PolyModifier.size(w, h)) { x, y, cw, ch ->
                val s = textScale
                val cx = x + cw / 2f
                val cy = y + ch / 2f
                if (showShadow && font == Font.Minecraft) {
                    line(cx - 5f * s, cy + 0.5f * s, cx + 6f * s, cy + 0.5f * s, fg.darken(0.75f), s)
                }
                line(cx - 6f * s, cy - 0.5f * s, cx + 5f * s, cy - 0.5f * s, fg, s)
            }
        }
    }

    private fun KeyMapping.label(): String = translatedKeyMessage.string
}
