package org.polyfrost.evergreenhud.client.hud

import net.minecraft.locale.Language
import net.minecraft.network.chat.Component
import org.polyfrost.evergreenhud.client.BlockPositionChangedEvent
import org.polyfrost.evergreenhud.client.utils.CachedTextHud
import org.polyfrost.oneconfig.api.event.v1.eventHandler
import org.polyfrost.oneconfig.utils.v1.dsl.mc
import kotlin.jvm.optionals.getOrNull

class BiomeHud : CachedTextHud(
    title = "Biome",
    category = Category.INFO,
    defaultText = "Unknown"
) {

    override fun setup() {
        super.setup()
        eventHandler { (pos): BlockPositionChangedEvent ->

            val level = mc.level ?: return@eventHandler

            if (!level.isInValidBounds(pos)) {
                return@eventHandler
            }

            val id = level.getBiome(pos)
                .unwrapKey()
                ?.getOrNull()
                //$ if >= 1.21.11 '?.identifier()' else '?.location()'
                ?.identifier()

            val translationKey = id?.toLanguageKey("biome")

            val text = if (translationKey != null && Language.getInstance().has(translationKey)) {
                Component.translatable(translationKey).string
            } else id?.toString() ?: defaultText

            updateWithText(text)
        }
    }
}
