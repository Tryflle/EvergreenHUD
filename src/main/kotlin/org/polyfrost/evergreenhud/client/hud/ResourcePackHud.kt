//package org.polyfrost.evergreenhud.client.hud
//
//import dev.deftu.omnicore.client.OmniClient
//import net.minecraft.client.resources.ResourcePackRepository
//import org.polyfrost.evergreenhud.client.ResourceReloadEvent
//import org.polyfrost.oneconfig.api.config.v1.annotations.Switch
//import org.polyfrost.oneconfig.api.event.v1.eventHandler
//import org.polyfrost.oneconfig.api.hud.v1.Hud
//import org.polyfrost.polyui.component.extensions.setFont
//import org.polyfrost.polyui.component.impl.Group
//import org.polyfrost.polyui.component.impl.Image
//import org.polyfrost.polyui.component.impl.Text
//import org.polyfrost.polyui.data.PolyImage
//import org.polyfrost.polyui.unit.Align
//import org.polyfrost.polyui.unit.Vec2
//import java.io.ByteArrayOutputStream
//import javax.imageio.ImageIO
//
//// CHECK OK
//class ResourcePackHud : Hud<Group>(
//    id = "resource_pack.json",
//    title = "Resource Pack",
//    category = Category.INFO,
//) {
//    private companion object {
//        private val default by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
//            PolyImage("pack.png")
//        }
//    }
//
//    @Switch(title = "Ignore Overlay")
//    var ignoreOverlay = true
//
//    override fun setup() {
//        super.setup()
//        eventHandler<ResourceReloadEvent> {
//            updatePack()
//        }
//    }
//
//    fun updatePack() {
//        val entries = OmniClient.getInstance().resourcePackRepository.repositoryEntries
//        val pack = if (ignoreOverlay) entries.firstOrNull() else entries.lastOrNull()
//        val it = get()
//        if (pack == null) {
//            it[0] = Image(default, size = Vec2(64f, 64f))
//            (it[1][0] as Text).text = "Default"
//            (it[1][1] as Text).text = "The classic Minecraft experience."
//        } else {
//            it[0] = Image(pack2poly(pack), size = Vec2(64f, 64f))
//            (it[1][0] as Text).text = pack.resourcePackName
//            (it[1][1] as Text).text = pack.texturePackDescription
//        }
//    }
//
//    override fun create() = Group(
//        Image(default, size = Vec2(64f, 64f)),
//        Group(
//            Text("Default", fontSize = 18f).setFont { medium },
//            Text("The classic Minecraft experience."),
//            alignment = Align()
//        ),
//        alignment = Align(pad = Vec2.ZERO)
//    )
//
//    override fun update(): Boolean {
//        return false
//    }
//
//    private fun pack2poly(pack: ResourcePackRepository.Entry): PolyImage = object : PolyImage("evergreen_pack_placeholder.png", type = Type.Raster) {
//        override fun bytes(): ByteArray {
//            val out = ByteArrayOutputStream(1024)
//            ImageIO.write(pack.resourcePack.packImage, "png", out)
//            return out.toByteArray()
//        }
//    }
//}
