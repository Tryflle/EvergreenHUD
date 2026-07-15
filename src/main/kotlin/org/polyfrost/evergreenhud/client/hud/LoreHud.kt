package org.polyfrost.evergreenhud.client.hud

import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.ItemStack
import org.polyfrost.evergreenhud.client.SelectedItemChangedEvent
import org.polyfrost.oneconfig.api.config.v1.annotations.Checkbox
import org.polyfrost.oneconfig.api.config.v1.annotations.Number
import org.polyfrost.oneconfig.api.config.v1.annotations.Switch
import org.polyfrost.oneconfig.api.event.v1.eventHandler
import org.polyfrost.oneconfig.api.hud.v1.TextHud

class LoreHud : TextHud(
    id = "lore.json",
    title = "Item Lore",
    category = Category.INFO,
    prefix = "",
) {
    @Switch(title = "Show Item Name")
    var showName = true

    @Checkbox(title = "Remove Empty Lines")
    var removeEmptyLines = true

    @Number(title = "Max Lines", min = 0f, max = 50f)
    var maxLines = 0

    private val ItemStack.isNameShown: Boolean
        get() = has(DataComponents.CUSTOM_NAME)

    override fun defaultPosition(): Pair<Float, Float> = 0f to 0f

    override fun setup() {
        super.setup()
        if (isReal) {
            updateWhenChanged("showName")
            updateWhenChanged("removeEmptyLines")
            updateWhenChanged("maxLines")
            eventHandler { (item): SelectedItemChangedEvent ->
                theItem = item
            }
        }
    }

    var theItem: ItemStack? = null
        set(value) {
            val old = field
            if (old == null && value == null) return
            if (old != null && value != null && ItemStack.matches(old, value)) return
            field = value
            updateAndRecalculate()
        }

    override fun getText(): String? {
        val item = theItem ?: return if (isReal) "Item Lore HUD" else null

        val out = StringBuilder()
        if (showName && item.isNameShown) {
            val name = item.hoverName.string
            if (name.isNotEmpty()) {
                out.append(name).append('\n')
            }
        }

        var i = 0
        item.forEachLore {
            if (maxLines in 1..i) return@forEachLore
            if (removeEmptyLines && it.isBlank()) return@forEachLore
            out.append(it).append('\n')
            i++
        }

        val text = out.toString().trimEnd('\n')
        return text.ifEmpty { if (isReal) "Item Lore HUD" else null }
    }

    private inline fun ItemStack.forEachLore(consumer: (String) -> Unit) {
        val lore = this.get(DataComponents.LORE) ?: return
        for (line in lore.lines) {
            consumer(line.string)
        }
    }
}
