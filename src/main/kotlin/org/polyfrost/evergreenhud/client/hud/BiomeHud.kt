package org.polyfrost.evergreenhud.client.hud

import dev.deftu.omnicore.client.OmniClient
import dev.deftu.omnicore.common.OmniBlockPos
import org.polyfrost.evergreenhud.client.BlockPositionChangedEvent
import org.polyfrost.oneconfig.api.event.v1.eventHandler
import org.polyfrost.oneconfig.api.hud.v1.TextHud

class BiomeHud : TextHud(
    id = "biome.json",
    title = "Biome",
    category = Category.INFO,
    prefix = "Biome: ",
) {

    override fun setup() {
        super.setup()
        eventHandler { (x, y, z): BlockPositionChangedEvent ->
            val world = OmniClient.currentWorld ?: return@eventHandler
            val pos = OmniBlockPos.from(x, y, z)
            if (!world.isBlockLoaded(pos)) {
                return@eventHandler
            }

            sb.append(world.getChunkAt(pos)?.getBiomeAt(pos)?.translatedName)
        }
    }

    override fun getText(): String? {
        return null
    }

}
