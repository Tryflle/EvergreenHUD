package org.polyfrost.evergreenhud.client.utils

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import org.polyfrost.compose.render.PolyColor
import java.util.Optional

data class StyledRun(
    val text: String,
    val color: PolyColor?,
    val bold: Boolean,
    val italic: Boolean,
)

/** Runs treated as one unit when columns align so a cell can hold several colours without extra columns */
@JvmInline
value class StyledCell(val runs: List<StyledRun>)

fun StyledRun.asCell(): StyledCell = StyledCell(listOf(this))

fun Component.toStyledRuns(): List<StyledRun> {
    val runs = ArrayList<StyledRun>()
    visit({ style: Style, text: String ->
        if (text.isNotEmpty()) {
            runs.add(
                StyledRun(
                    text = text,
                    color = style.color?.let { PolyColor(0xFF000000.toInt() or it.value) },
                    bold = style.isBold,
                    italic = style.isItalic,
                )
            )
        }
        Optional.empty<Unit>()
    }, Style.EMPTY)
    return runs
}

fun List<StyledRun>.plainText(): String = joinToString("") { it.text }
