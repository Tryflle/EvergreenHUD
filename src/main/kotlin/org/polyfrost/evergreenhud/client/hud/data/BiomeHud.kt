package org.polyfrost.evergreenhud.client.hud.data

import dev.deftu.omnicore.client.OmniClient
import dev.deftu.omnicore.common.OmniBlockPos
import org.polyfrost.evergreenhud.client.BlockPositionChangedEvent
import org.polyfrost.oneconfig.api.event.v1.eventHandler
import org.polyfrost.oneconfig.api.hud.v1.TextHud

class BiomeHud : TextHud(
    id = "biome.json",
    title = "Biome",
    category = Category.INFO,
    prefix = "",
    suffix = "Biome: "
) {

    override fun setup() {
        super.setup()
        eventHandler { (x, y, z): BlockPositionChangedEvent ->
            val world = OmniClient.world ?: return@eventHandler
            val pos = OmniBlockPos.from(x, y, z)
            if (!world.isBlockLoaded(pos)) {
                return@eventHandler
            }

            sb.append(world.getChunkFromBlockCoords(pos).getBiome(pos, world.worldChunkManager).biomeName)
        }
    }

    override fun getText(): String? {
        return null
    }

}
