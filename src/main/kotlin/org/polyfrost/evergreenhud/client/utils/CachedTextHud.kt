package org.polyfrost.evergreenhud.client.utils

abstract class CachedTextHud(
    title: String,
    category: Category,
    prefix: String = "$title: ",
    suffix: String = "",
    id: String = "${title.replace(' ', '_').lowercase()}.json",
    protected open val defaultText: String = "",
) : AutoHideTextHud(id, title, category, prefix, suffix) {
    protected var currentText: String = defaultText

    fun updateWithText(text: Any?) {
        this.currentText = text?.toString() ?: defaultText
        updateAndRecalculate()
    }

    override fun getText(): String? = currentText

    override fun defaultPosition(): Pair<Float, Float> = 0f to 0f

}
