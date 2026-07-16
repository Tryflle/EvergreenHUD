package org.polyfrost.evergreenhud.client.utils

import androidx.compose.runtime.Composable
import org.jetbrains.skia.Font as SkiaFont
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
import kotlin.math.max

private const val LINE_GAP = 2f

private const val POPPINS_SIZE = 8f

@Composable
fun Hud.HudStyledLines(lines: List<List<StyledRun>>, alignColumns: Boolean = false) {
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
    val skiaFont = if (font == Font.Poppins) FontManager.getFont(POPPINS_SIZE * textScale, getPoppinsFontName()) else null
    val columnOffsets = if (alignColumns) columnOffsets(cased, skiaFont) else null

    PolyBox(modifier = outerModifier) {
        PolyColumn(
            gap = LINE_GAP * textScale,
            modifier = if (isStaticValid) PolyModifier.align(alignment) else PolyModifier,
        ) {
            for (line in cased) StyledLine(line, lineAlign, skiaFont, columnOffsets)
        }
    }
}

private fun List<List<StyledRun>>.mapRunText(transform: (String) -> String): List<List<StyledRun>> =
    map { line -> line.map { it.copy(text = transform(it.text)) } }

private fun Hud.mcRunText(run: StyledRun): String = buildString {
    if (run.bold || textBold) append("§l")
    if (run.italic || textItalic) append("§o")
    append(run.text)
}

private fun Hud.runWidths(runs: List<StyledRun>, skiaFont: SkiaFont?): List<Float> =
    if (skiaFont != null) runs.map { skiaFont.measureTextWidth(it.text) }
    else runs.map { McFontQueue.measureTextWidth(mcRunText(it), textScale) }

private fun Hud.columnOffsets(lines: List<List<StyledRun>>, skiaFont: SkiaFont?): List<Float> {
    val columns = lines.maxOfOrNull { it.size } ?: return emptyList()
    val widths = FloatArray(columns)
    for (line in lines) {
        val lineWidths = runWidths(line, skiaFont)
        for (i in lineWidths.indices) widths[i] = max(widths[i], lineWidths[i])
    }

    var offset = 0f
    return List(columns) { i ->
        val start = offset
        offset += widths[i]
        start
    }
}

@Composable
private fun Hud.StyledLine(runs: List<StyledRun>, alignment: PolyAlign, skiaFont: SkiaFont?, columnOffsets: List<Float>?) {
    val fg = PolyColor(textColor, textChroma, textChromaSpeed)
    val widths = runWidths(runs, skiaFont)
    val offsets = columnOffsets ?: widths.runningFold(0f) { acc, w -> acc + w }
    val width = (runs.indices.maxOfOrNull { offsets[it] + widths[it] } ?: 0f).coerceAtLeast(1f)

    if (skiaFont != null) {
        val metrics = skiaFont.metrics
        val height = metrics.descent - metrics.ascent

        PolyCanvas(PolyModifier.size(width, height).align(alignment)) { x, y, _, _ ->
            val baseline = y - metrics.ascent
            if (showShadow) {
                val shadowCol = PolyColor(shadowColor, shadowChroma, shadowChromaSpeed)
                for ((i, run) in runs.withIndex()) {
                    text(run.text, x + offsets[i] + shadowOffsetX, baseline + shadowOffsetY, shadowCol, skiaFont)
                }
            }
            for ((i, run) in runs.withIndex()) {
                val color = run.argb?.let { PolyColor(it) } ?: fg
                text(run.text, x + offsets[i], baseline, color, skiaFont)
            }
        }
    } else {
        val texts = runs.map { mcRunText(it) }
        val height = McFontQueue.measureTextHeight(textScale)

        PolyCanvas(PolyModifier.size(width, height).align(alignment)) { x, y, _, _ ->
            val renderer = McFontQueue.renderer ?: return@PolyCanvas
            for ((i, run) in runs.withIndex()) {
                val color = run.argb ?: fg.argb
                renderer(canvas, texts[i], x + offsets[i], y, color, showShadow, textScale)
            }
        }
    }
}
