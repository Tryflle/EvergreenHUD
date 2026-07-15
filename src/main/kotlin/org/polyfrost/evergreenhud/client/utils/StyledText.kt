package org.polyfrost.evergreenhud.client.utils

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import java.util.Optional

data class StyledRun(
    val text: String,
    val argb: Int?,
    val bold: Boolean,
    val italic: Boolean,
)

fun Component.toStyledRuns(): List<StyledRun> {
    val runs = ArrayList<StyledRun>()
    visit({ style: Style, text: String ->
        if (text.isNotEmpty()) {
            runs.add(
                StyledRun(
                    text = text,
                    argb = style.color?.let { 0xFF000000.toInt() or it.value },
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
