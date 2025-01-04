package org.polyfrost.evergreenhud.hud.data

import net.minecraft.client.Minecraft
import net.minecraft.client.resources.ResourcePackRepository
import net.minecraftforge.client.event.TextureStitchEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.polyfrost.oneconfig.api.config.v1.annotations.Switch
import org.polyfrost.oneconfig.api.hud.v1.Hud
import org.polyfrost.polyui.component.extensions.setFont
import org.polyfrost.polyui.component.impl.Group
import org.polyfrost.polyui.component.impl.Image
import org.polyfrost.polyui.component.impl.Text
import org.polyfrost.polyui.data.PolyImage
import org.polyfrost.polyui.unit.Align
import org.polyfrost.polyui.unit.Vec2
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

class ResourcePack : Hud<Group>() {

    @Switch(title = "Ignore Overlay")
    var ignoreOverlay = true

    override fun category() = Category.INFO

    private val default = PolyImage("pack.png")

    init {
        MinecraftForge.EVENT_BUS.register(this)
    }

    @SubscribeEvent
    fun onPackChange(e: TextureStitchEvent.Post) {
        updatePack()
    }

    private fun pack2poly(pack: ResourcePackRepository.Entry): PolyImage = object : PolyImage("evergreen_pack_placeholder.png", type = Type.Raster) {
        override fun bytes(): ByteArray {
            val out = ByteArrayOutputStream(512)
            ImageIO.write(pack.resourcePack.packImage, "png", out)
            return out.toByteArray()
        }
    }


    override fun create() = Group(
        Image(default, size = Vec2(64f, 64f)),
        Group(
            Text("Default", fontSize = 18f).setFont { medium },
            Text("The classic Minecraft experience."),
        ),
        alignment = Align(pad = Vec2.ZERO)
    )

    fun updatePack() {
        val entries = Minecraft.getMinecraft().resourcePackRepository.repositoryEntries
        val pack = if (ignoreOverlay) entries.firstOrNull() else entries.lastOrNull()
        val it = get()
        if (pack == null) {
            it[0] = Image(default, size = Vec2(64f, 64f))
            (it[1][0] as Text).text = "Default"
            (it[1][1] as Text).text = "The classic Minecraft experience."
        } else {
            it[0] = Image(pack2poly(pack), size = Vec2(64f, 64f))
            (it[1][0] as Text).text = pack.resourcePackName
            (it[1][1] as Text).text = pack.texturePackDescription
        }
    }

    override fun title() = "Resource Pack"

    override fun update() = false
}