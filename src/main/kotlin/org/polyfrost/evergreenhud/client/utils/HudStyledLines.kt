package org.polyfrost.evergreenhud.client.utils

import androidx.compose.runtime.Composable
import org.polyfrost.compose.composables.PolyBox
import org.polyfrost.compose.composables.PolyCanvas
import org.polyfrost.compose.composables.PolyColumn
import org.polyfrost.compose.composables.PolyModifier
import org.polyfrost.compose.composables.align
import org.polyfrost.compose.composables.background
import org.polyfrost.compose.composables.padding
import org.polyfrost.compose.composables.size
import org.polyfrost.compose.layout.PolyAlign
import org.polyfrost.compose.layout.PolyInsets
import org.polyfrost.compose.mc.McFontQueue
import org.polyfrost.compose.render.FontManager
import org.polyfrost.compose.render.PolyColor
import org.polyfrost.oneconfig.api.hud.v1.Font
import org.polyfrost.oneconfig.api.hud.v1.Hud

private const val LINE_GAP = 2f

private const val POPPINS_SIZE = 8f

@Composable
fun Hud.HudStyledLines(lines: List<List<StyledRun>>) {
    val cased = when (caseType) {
        1 -> lines.mapRunText(String::uppercase)
        2 -> lines.mapRunText(String::lowercase)
        else -> lines
    }

    val padInsets = PolyInsets(padLeft, padTop, padRight, padBottom)
    val isStaticValid = staticWidth && staticW > 0f && staticH > 0f

    val outerModifier = if (showBackground) {
        val bgModifier = PolyModifier.background(PolyColor(bgColor, bgChroma, bgChromaSpeed), bgRadius)
        if (isStaticValid) bgModifier.size(staticW, staticH).padding(padInsets)
        else bgModifier.padding(padInsets)
    } else {
        if (isStaticValid) PolyModifier.size(staticW, staticH).padding(padInsets)
        else PolyModifier.padding(padInsets)
    }

    val lineAlign = if (isStaticValid) alignment else PolyAlign.Left

    PolyBox(modifier = outerModifier) {
        PolyColumn(
            gap = LINE_GAP * textScale,
            modifier = if (isStaticValid) PolyModifier.align(alignment) else PolyModifier,
        ) {
            for (line in cased) StyledLine(line, lineAlign)
        }
    }
}

private fun List<List<StyledRun>>.mapRunText(transform: (String) -> String): List<List<StyledRun>> =
    map { line -> line.map { it.copy(text = transform(it.text)) } }

@Composable
private fun Hud.StyledLine(runs: List<StyledRun>, alignment: PolyAlign) {
    val fg = PolyColor(textColor, textChroma, textChromaSpeed)

    if (font == Font.Poppins) {
        val skiaFont = FontManager.getFont(POPPINS_SIZE * textScale, getPoppinsFontName())
        val metrics = skiaFont.metrics
        val widths = runs.map { skiaFont.measureTextWidth(it.text) }
        val width = widths.sum().coerceAtLeast(1f)
        val height = metrics.descent - metrics.ascent

        PolyCanvas(PolyModifier.size(width, height).align(alignment)) { x, y, _, _ ->
            val baseline = y - metrics.ascent
            if (showShadow) {
                val shadowCol = PolyColor(shadowColor, shadowChroma, shadowChromaSpeed)
                var offset = 0f
                for ((i, run) in runs.withIndex()) {
                    text(run.text, x + offset + shadowOffsetX, baseline + shadowOffsetY, shadowCol, skiaFont)
                    offset += widths[i]
                }
            }
            var offset = 0f
            for ((i, run) in runs.withIndex()) {
                val color = run.argb?.let { PolyColor(it) } ?: fg
                text(run.text, x + offset, baseline, color, skiaFont)
                offset += widths[i]
            }
        }
    } else {
        val texts = runs.map { run ->
            buildString {
                if (run.bold || textBold) append("§l")
                if (run.italic || textItalic) append("§o")
                append(run.text)
            }
        }
        val widths = texts.map { McFontQueue.measureTextWidth(it, textScale) }
        val width = widths.sum().coerceAtLeast(1f)
        val height = McFontQueue.measureTextHeight(textScale)

        PolyCanvas(PolyModifier.size(width, height).align(alignment)) { x, y, _, _ ->
            val renderer = McFontQueue.renderer ?: return@PolyCanvas
            var offset = 0f
            for ((i, run) in runs.withIndex()) {
                val color = run.argb ?: fg.argb
                renderer(canvas, texts[i], x + offset, y, color, showShadow, textScale)
                offset += widths[i]
            }
        }
    }
}
