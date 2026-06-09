package org.polyfrost.evergreenhud.client.hud

import net.minecraft.core.BlockPos
import net.minecraft.locale.Language
import net.minecraft.network.chat.Component
import org.polyfrost.evergreenhud.client.BlockPositionChangedEvent
import org.polyfrost.oneconfig.api.event.v1.eventHandler
import org.polyfrost.oneconfig.api.hud.v1.TextHud
import org.polyfrost.oneconfig.utils.v1.dsl.mc
import kotlin.jvm.optionals.getOrNull

class BiomeHud : TextHud(
    id = "biome.json",
    title = "Biome",
    category = Category.INFO,
    prefix = "Biome: ",
) {
    override fun setup() {
        super.setup()
        eventHandler { (x, y, z): BlockPositionChangedEvent ->

            val level = mc.level ?: return@eventHandler

            val pos = BlockPos(x, y, z)


            if (!level.isInValidBounds(pos)) {
                return@eventHandler
            }

            val id = level.getBiome(pos)
                .unwrapKey()
                // currently cant use '?.' as the first characters in stonecutter commented code
                // so this is a workaround. once stonecutter 0.9 releases, this can be replaced
                // with a local swap/replacement
                .map {
                    //? >= 1.21.11 {
                    it.identifier()
                    //? } else
                    /* it.location() */
                }.getOrNull()

            val translationKey = id?.toLanguageKey("biome")

            val text = if (translationKey != null && Language.getInstance().has(translationKey)) {
                Component.translatable(translationKey)
            } else Component.literal(id?.toString() ?: "Unknown")

            sb.append(text)
            updateAndRecalculate()
        }
    }

    override fun getText(): String? {
        return null
    }
}
