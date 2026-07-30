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
import org.polyfrost.evergreenhud.client.utils.copy
import org.polyfrost.evergreenhud.client.utils.fastRemoveIfReversed
import org.polyfrost.evergreenhud.client.utils.matchesKeyCode
import org.polyfrost.evergreenhud.client.utils.matchesMouseButton
import org.polyfrost.oneconfig.api.config.v1.Property
import org.polyfrost.oneconfig.api.config.v1.annotations.Color
import org.polyfrost.oneconfig.api.config.v1.annotations.RadioButton
import org.polyfrost.oneconfig.api.config.v1.annotations.Slider
import org.polyfrost.oneconfig.api.config.v1.annotations.Switch
import org.polyfrost.oneconfig.api.event.v1.eventHandler
import org.polyfrost.oneconfig.api.event.v1.events.KeyInputEvent
import org.polyfrost.oneconfig.api.event.v1.events.MouseInputEvent
import org.polyfrost.oneconfig.api.event.v1.invoke.EventHandler
import org.polyfrost.oneconfig.api.hud.v1.Font
import org.polyfrost.oneconfig.api.hud.v1.Hud
import org.polyfrost.oneconfig.utils.v1.dsl.mc

private const val KEY = 16f
private const val CPS_SCALE = 0.5f
private const val GAP = 2f
private const val LINE_GAP = 1f
private const val SPACE_H = 11f
private const val FONT = 8f

private const val CPS_NONE = 0
private const val CPS_SMALL = 1
private const val CPS_LARGE = 2

private const val UP = "▲"
private const val DOWN = "▼"
private const val LEFT = "◀"
private const val RIGHT = "▶"

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

    @RadioButton(
        title = "Click Counter",
        description = "Show your CPS on the attack and use keys. Small keeps the key name above the count, large replaces it.",
        options = ["None", "Small", "Large"],
    )
    var cpsMode = CPS_NONE

    @Switch(title = "Sprint", description = "Show the sprint key.")
    var showSprint = false

    @Switch(title = "Sneak", description = "Show the sneak key.")
    var showSneak = false

    @Switch(title = "Arrows", description = "Replace the movement keys with arrows.")
    var arrows = false

    @Slider(title = "Fade In Duration (ms)", description = "How long a key takes to fade from the unpressed to the pressed colors.", min = 1F, max = 250F, step = 1F)
    var fadeInMs = 150f

    @Slider(title = "Fade Out Duration (ms)", description = "How long a key takes to fade from the pressed to the unpressed colors.", min = 1F, max = 250F, step = 1F)
    var fadeOutMs = 150f

    @Color(title = "Pressed Background Color")
    var pressedBg = PolyColor(0xFFFFFFFF.toInt())

    @Color(title = "Pressed Text Color")
    var pressedText = PolyColor(0xFF000000.toInt())

    @Transient
    private var forward = mutableStateOf(0f)

    @Transient
    private var left = mutableStateOf(0f)

    @Transient
    private var back = mutableStateOf(0f)

    @Transient
    private var right = mutableStateOf(0f)

    @Transient
    private var jump = mutableStateOf(0f)

    @Transient
    private var attack = mutableStateOf(0f)

    @Transient
    private var use = mutableStateOf(0f)

    @Transient
    private var sprint = mutableStateOf(0f)

    @Transient
    private var sneak = mutableStateOf(0f)

    @Transient
    private var rev = mutableStateOf(0)

    @Transient
    private var attackCps = mutableStateOf(0)

    @Transient
    private var useCps = mutableStateOf(0)

    @Transient
    private var attackClicks: ArrayList<Long> = ArrayList(20)

    @Transient
    private var useClicks: ArrayList<Long> = ArrayList(20)

    @Transient
    private var handlers: ArrayList<EventHandler<*>> = ArrayList(2)

    @Transient
    private var lastNanos = System.nanoTime()

    override fun defaultPosition(): Pair<Float, Float> = 0f to 0f

    override fun clone(): Hud = (super.clone() as KeystrokesHud).apply {
        pressedBg = pressedBg.copy()
        pressedText = pressedText.copy()
        forward = mutableStateOf(0f)
        left = mutableStateOf(0f)
        back = mutableStateOf(0f)
        right = mutableStateOf(0f)
        jump = mutableStateOf(0f)
        attack = mutableStateOf(0f)
        use = mutableStateOf(0f)
        sprint = mutableStateOf(0f)
        sneak = mutableStateOf(0f)
        rev = mutableStateOf(0)
        attackCps = mutableStateOf(0)
        useCps = mutableStateOf(0)
        attackClicks = ArrayList(20)
        useClicks = ArrayList(20)
        handlers = ArrayList(2)
        lastNanos = System.nanoTime()
    }

    override fun setup() {
        super.setup()
        if (isReal) {
            addDependency("arrows", null) {
                if (font != Font.Minecraft) Property.Display.DISABLED else Property.Display.SHOWN
            }
            addDependency("cpsMode", "showClicks")
            for (option in listOf("showMovement", "showJump", "showClicks", "showSprint", "showSneak", "arrows", "cpsMode")) {
                addCallback(option) { rev.value++ }
            }
            handlers.add(eventHandler { (btn, state): MouseInputEvent ->
                if (state == 1) {
                    val o = mc.options ?: return@eventHandler
                    if (o.keyAttack.matchesMouseButton(btn)) onAttackClick()
                    if (o.keyUse.matchesMouseButton(btn)) onUseClick()
                }
            })
            handlers.add(eventHandler { (key, _, state): KeyInputEvent ->
                if (state == 1 && key != 0) {
                    val o = mc.options ?: return@eventHandler
                    if (o.keyAttack.matchesKeyCode(key)) onAttackClick()
                    if (o.keyUse.matchesKeyCode(key)) onUseClick()
                }
            })
        }
    }

    override fun remove() {
        for (handler in handlers) handler.unregister()
        handlers.clear()
        attackClicks.clear()
        useClicks.clear()
    }

    private fun onAttackClick() {
        if (showClicks && cpsMode != CPS_NONE) attackClicks.add(System.nanoTime())
    }

    private fun onUseClick() {
        if (showClicks && cpsMode != CPS_NONE) useClicks.add(System.nanoTime())
    }

    override fun update(): Boolean {
        val o = mc.options ?: return false
        val now = System.nanoTime()
        val dtMs = (now - lastNanos).coerceAtLeast(0L) / 1_000_000f
        lastNanos = now
        val inStep = if (fadeInMs <= 0f) 1f else dtMs / fadeInMs
        val outStep = if (fadeOutMs <= 0f) 1f else dtMs / fadeOutMs
        var changed = false
        fun poll(state: MutableState<Float>, key: KeyMapping) {
            val target = if (key.isDown) 1f else 0f
            val cur = state.value
            val next = when {
                cur < target -> (cur + inStep).coerceAtMost(target)
                cur > target -> (cur - outStep).coerceAtLeast(target)
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
        poll(sprint, o.keySprint)
        poll(sneak, o.keyShift)
        if (showClicks && cpsMode != CPS_NONE) {
            attackClicks.fastRemoveIfReversed { now - it > 1_000_000_000 }
            useClicks.fastRemoveIfReversed { now - it > 1_000_000_000 }
            if (attackCps.value != attackClicks.size) {
                attackCps.value = attackClicks.size
                changed = true
            }
            if (useCps.value != useClicks.size) {
                useCps.value = useClicks.size
                changed = true
            }
        }
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
                    ClickKey("LMB", attack.value, attackCps.value, clickW, key)
                    ClickKey("RMB", use.value, useCps.value, clickW, key)
                }
            }
            if (showSprint) {
                Key(o.keySprint.label(), sprint.value, rowW, key)
            }
            if (showSneak) {
                Key(o.keyShift.label(), sneak.value, rowW, key)
            }
        }
    }

    private fun blend(unpressed: PolyColor, pressed: PolyColor, progress: Float): PolyColor = when {
        progress <= 0f -> unpressed
        progress >= 1f -> pressed
        else -> unpressed.lerp(pressed, progress)
    }

    private fun keyFg(progress: Float): PolyColor =
        blend(PolyColor(textColor, textChroma, textChromaSpeed), pressedText, progress)

    private fun keyBg(progress: Float): PolyColor =
        blend(PolyColor(bgColor, bgChroma, bgChromaSpeed), pressedBg, progress)

    @Composable
    private fun ClickKey(label: String, progress: Float, cps: Int, w: Float, h: Float) {
        when (cpsMode) {
            CPS_SMALL -> Key(label, progress, w, h, sub = "$cps CPS")
            CPS_LARGE -> Key(if (cps > 0) cps.toString() else label, progress, w, h)
            else -> Key(label, progress, w, h)
        }
    }

    @Composable
    private fun Key(label: String, progress: Float, w: Float, h: Float, align: PolyAlign? = null, sub: String? = null) {
        val fg = keyFg(progress)
        var mod = PolyModifier.size(w, h)
        if (showBackground) {
            mod = mod.background(keyBg(progress), bgRadius)
        }
        if (align != null) mod = mod.align(align)
        PolyBox(modifier = mod) {
            val subScale = textScale * CPS_SCALE
            if (font == Font.Minecraft) {
                val lineH = McFontQueue.measureTextHeight(textScale)
                val lineGap = LINE_GAP * textScale
                val totalH = if (sub == null) lineH else lineH + lineGap + McFontQueue.measureTextHeight(subScale)
                val topPad = ((h - totalH) / 2f).coerceAtLeast(0f)
                McLine(label, fg, w, topPad, textScale)
                if (sub != null) McLine(sub, fg, w, topPad + lineH + lineGap, subScale)
            } else if (sub == null) {
                KeyText(label, fg, textScale)
            } else {
                PolyColumn(gap = LINE_GAP * textScale, modifier = PolyModifier.align(PolyAlign.Center)) {
                    KeyText(label, fg, textScale)
                    KeyText(sub, fg, subScale)
                }
            }
        }
    }

    @Composable
    private fun McLine(text: String, color: PolyColor, w: Float, topPad: Float, scale: Float) {
        val visibleW = (McFontQueue.measureTextWidth(text, scale) - scale).coerceAtLeast(0f)
        val leftPad = ((w - visibleW) / 2f).coerceAtLeast(0f)
        PolyMcText(
            text,
            color = color,
            shadow = showShadow,
            scale = scale,
            modifier = PolyModifier.align(PolyAlign.TopLeft).margin(leftPad, topPad, 0f, 0f),
        )
    }

    @Composable
    private fun KeyText(text: String, color: PolyColor, scale: Float) {
        PolyText(
            text,
            color = color,
            fontSize = FONT * scale,
            font = getPoppinsFontName(),
            modifier = PolyModifier.align(PolyAlign.Center),
        )
    }

    @Composable
    private fun SpaceKey(progress: Float, w: Float, h: Float) {
        val fg = keyFg(progress)
        var mod = PolyModifier.size(w, h)
        if (showBackground) {
            mod = mod.background(keyBg(progress), bgRadius)
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
