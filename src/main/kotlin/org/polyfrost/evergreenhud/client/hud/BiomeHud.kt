//package org.polyfrost.evergreenhud.client.hud
//
//import dev.deftu.omnicore.api.client.world
//import dev.deftu.omnicore.api.data.pos.OmniBlockPos
//import dev.deftu.omnicore.api.world.isBlockLoadedAt
//import org.polyfrost.evergreenhud.client.BlockPositionChangedEvent
//import org.polyfrost.oneconfig.api.event.v1.eventHandler
//import org.polyfrost.oneconfig.api.hud.v1.TextHud
//
//class BiomeHud : TextHud(
//    id = "biome.json",
//    title = "Biome",
//    category = Category.INFO,
//    prefix = "Biome: ",
//) {
//    override fun setup() {
//        super.setup()
//        eventHandler { (x, y, z): BlockPositionChangedEvent ->
//            val world = world ?: return@eventHandler
//
//            val pos = OmniBlockPos(x, y, z)
//            if (!world.isBlockLoadedAt(pos.vanilla)) {
//                return@eventHandler
//            }
//
//            sb.append(world.getBiomeAt(pos)?.translatedName ?: "Unknown")
//            updateAndRecalculate()
//        }
//    }
//
//    override fun getText(): String? {
//        return null
//    }
//}
