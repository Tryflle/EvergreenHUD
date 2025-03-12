package org.polyfrost.evergreenhud.hud.data

import net.minecraft.client.Minecraft
import net.minecraft.util.BlockPos
import org.polyfrost.evergreenhud.BlockPositionChangedEvent
import org.polyfrost.oneconfig.api.event.v1.eventHandler
import org.polyfrost.oneconfig.api.hud.v1.TextHud

class Biome : TextHud("Biome: ") {

    override fun initialize() {
        eventHandler { (x, y, z): BlockPositionChangedEvent ->
            val world = Minecraft.getMinecraft().theWorld ?: return@eventHandler
            val pos = BlockPos(x, y, z)
            if (!world.isBlockLoaded(pos)) return@eventHandler
            sb.append(world.getChunkFromBlockCoords(pos).getBiome(pos, world.worldChunkManager).biomeName)
        }
        super.initialize()
    }

    override fun getText() = null

    override fun id() = "biome.json"

    override fun title() = "Biome"

    override fun category() = Category.INFO
}
