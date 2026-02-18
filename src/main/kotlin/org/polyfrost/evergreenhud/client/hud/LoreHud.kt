package org.polyfrost.evergreenhud.client.hud

import dev.deftu.textile.CollapseMode
import dev.deftu.textile.minecraft.MCText
import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.ItemStack
import org.polyfrost.evergreenhud.client.SelectedItemChangedEvent
import org.polyfrost.oneconfig.api.config.v1.annotations.Checkbox
import org.polyfrost.oneconfig.api.config.v1.annotations.Number
import org.polyfrost.oneconfig.api.config.v1.annotations.Slider
import org.polyfrost.oneconfig.api.config.v1.annotations.Switch
import org.polyfrost.oneconfig.api.event.v1.eventHandler
import org.polyfrost.oneconfig.api.hud.v1.TextHud
import org.polyfrost.polyui.animate.Animations
import org.polyfrost.polyui.operations.Fade
import org.polyfrost.polyui.unit.milliseconds
import org.polyfrost.polyui.utils.Clock

class LoreHud : TextHud(
    id = "lore.json",
    title = "Item Lore",
    category = Category.INFO,
    prefix = "",
) {
    @Switch(title = "Show Item Name")
    var showName = true

    @Switch(title = "Fade Out")
    var fadeOut = true

    @Checkbox(title = "Remove Empty Lines")
    var removeEmptyLines = true

    @Slider(title = "Show Duration (ms)", min = 0f, max = 10000f)
    var showDuration = 5000

    @Slider(title = "Fade Duration (ms)", min = 0f, max = 10000f)
    var fadeDuration = 1000

    @Number(title = "Max Lines", min = 0f, max = 50f)
    var maxLines = 0

    private val ItemStack.isNameShown: Boolean
        get() = has(DataComponents.CUSTOM_NAME)

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
            if (ItemStack.matches(field, value)) return
            field = value
            val it = get()
            it.alpha = 1f
            updateAndRecalculate()
            if (fadeOut) {
                it.polyUI.addExecutor(Clock.Bomb(showDuration.milliseconds) {
                    Fade(it, 0f, animation = Animations.Default.create(fadeDuration.milliseconds)).add()
                })
            }
        }

    override fun getText(): String? {
        val item = theItem ?: return if (isReal) "Item Lore HUD" else null

        if (showName && item.isNameShown) {
            val name = MCText.wrap(item.hoverName).collapseToString(CollapseMode.SCOPED)
            if (name.isNotEmpty()) {
                 sb.append(name).append('\n')
            }
        }

        var i = 0
        item.forEachLore {
            if (maxLines in 1..i) return@forEachLore
            if (removeEmptyLines && it.isBlank()) return@forEachLore
            sb.append(it).append('\n')
            i++
        }

        return null
    }

    @Suppress("UNNECESSARY_SAFE_CALL")
    private inline fun ItemStack.forEachLore(consumer: (String) -> Unit) {
        val lore = this.get(DataComponents.LORE) ?: return
        for (line in lore.lines) {
            consumer(MCText.wrap(line).collapseToString(CollapseMode.SCOPED))
        }
    }
}