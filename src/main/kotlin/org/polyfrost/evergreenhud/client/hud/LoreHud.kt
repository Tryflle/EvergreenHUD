package org.polyfrost.evergreenhud.client.hud

import dev.deftu.omnicore.api.nbt.length
import net.minecraft.item.ItemStack
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

//#if MC >= 1.20.6
//$$ import net.minecraft.component.DataComponentTypes
//#endif

//#if MC >= 1.16.5
//$$ import dev.deftu.textile.minecraft.MCTextHolder
//#endif

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
        get() {
            //#if FABRIC && MC >= 1.21.5
            //$$ // Preprocessor/Remap being stubborn for no goddamn reason
            //$$ return contains(DataComponentTypes.CUSTOM_NAME)
            //#elseif MC >= 1.20.6
            //$$ return contains(DataComponentTypes.CUSTOM_NAME)
            //#else
            return hasDisplayName()
            //#endif
        }

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
            @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
            if (ItemStack.areItemStacksEqual(field, value)) return
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
            //#if MC >= 1.16.5
            //$$ val name = MCTextHolder.convertFromVanilla(item.displayName).asString()
            //$$ if (name.isNotEmpty()) {
            //$$      sb.append(name).append('\n')
            //$$ }
            //#else
            val name = item.displayName
            if (!name.isNullOrEmpty()) {
                sb.append(item.displayName).append('\n')
            }
            //#endif
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
        //#if MC >= 1.20.6
        //$$ val lore = this.get(DataComponentTypes.LORE) ?: return
        //$$ for (line in lore.comp_2401) {
        //$$     consumer(MCTextHolder.convertFromVanilla(line).asString())
        //$$ }
        //#else
        val tags = this.tagCompound?.getCompoundTag("display")?.getTagList("Lore", 8) ?: return
        for (i in 0..<tags.length) {
            consumer(tags.getStringTagAt(i))
        }
        //#endif
    }
}