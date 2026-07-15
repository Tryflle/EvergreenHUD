package org.polyfrost.evergreenhud.client.hud

import com.mojang.blaze3d.platform.NativeImage
//? if < 26
import net.minecraft.client.gui.GuiGraphics
//? if >= 26
//import net.minecraft.client.gui.GuiGraphicsExtractor as GuiGraphics
//? if >= 1.21.4 && < 1.21.8
//import net.minecraft.client.renderer.RenderType
//? if >= 1.21.8
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.network.chat.Component
//? if < 1.21.11
//import net.minecraft.resources.ResourceLocation
//? if >= 1.21.11
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraft.server.packs.repository.Pack
import net.minecraft.util.FormattedCharSequence
import org.polyfrost.evergreenhud.EvergreenHudConstants
import org.polyfrost.oneconfig.api.config.v1.annotations.Switch
import org.polyfrost.oneconfig.api.hud.v1.LegacyHud
import org.polyfrost.oneconfig.utils.v1.dsl.mc
import org.slf4j.LoggerFactory
import java.security.MessageDigest
import kotlin.math.max

class ResourcePackHud : LegacyHud(
    id = "resource_pack.json",
    title = "Resource Pack",
    category = Category.INFO,
) {
    private companion object {
        private val LOGGER = LoggerFactory.getLogger("EvergreenHUD/Resource Pack")

        private const val ICON = 64
        private const val GAP = 6
        private const val TITLE_GAP = 2
        private const val DESCRIPTION_WIDTH = 140

        private const val DEFAULT_TITLE = "Default"
        private const val DEFAULT_DESCRIPTION = "The classic Minecraft experience."

        private val DEFAULT_ICON = ResourceLocation.withDefaultNamespace("textures/misc/unknown_pack.png")

        private val iconCache = HashMap<String, ResourceLocation>()
        private var cachedIds: List<String>? = null

        fun syncIcons(ids: List<String>) {
            if (ids == cachedIds) return
            cachedIds = ids
            if (iconCache.isEmpty()) return
            val textureManager = mc.textureManager
            for (location in iconCache.values) {
                if (location != DEFAULT_ICON) textureManager.release(location)
            }
            iconCache.clear()
        }

        fun iconFor(id: String?): ResourceLocation {
            if (id == null) return DEFAULT_ICON
            iconCache[id]?.let { return it }
            val icon = loadIcon(id) ?: DEFAULT_ICON
            iconCache[id] = icon
            return icon
        }

        private fun loadIcon(id: String): ResourceLocation? {
            val pack = mc.resourcePackRepository.getPack(id) ?: return null
            return try {
                pack.open().use { resources ->
                    val supplier = resources.getRootResource("pack.png") ?: return null
                    val location = iconLocation(id)
                    supplier.get().use { stream ->
                        val image = NativeImage.read(stream)
                        //? if < 1.21.5
                        //mc.textureManager.register(location, DynamicTexture(image))
                        //? if >= 1.21.5
                        mc.textureManager.register(location, DynamicTexture({ location.toString() }, image))
                    }
                    location
                }
            } catch (e: Exception) {
                LOGGER.warn("Failed to load icon from pack {}", id, e)
                null
            }
        }

        private fun iconLocation(id: String): ResourceLocation {
            val sanitized = buildString(id.length) {
                for (c in id.lowercase()) {
                    append(if (c in 'a'..'z' || c in '0'..'9' || c == '_' || c == '-' || c == '.') c else '_')
                }
            }
            return ResourceLocation.fromNamespaceAndPath(EvergreenHudConstants.ID, "pack/$sanitized/${sha1(id)}/icon")
        }

        private fun sha1(value: String): String {
            val digest = MessageDigest.getInstance("SHA-1").digest(value.toByteArray(Charsets.UTF_8))
            return buildString(digest.size * 2) { for (b in digest) append("%02x".format(b)) }
        }
    }

    @Switch(title = "Ignore Overlay", description = "Show the pack underneath rather than the one layered on top of it.")
    var ignoreOverlay = true

    private var lastPackId: String? = null
    private var lastIgnoreOverlay: Boolean? = null

    private var packId: String? = null
    private var packTitle = DEFAULT_TITLE
    private var descriptionLines: List<FormattedCharSequence> = emptyList()
    private var actualW = ICON.toFloat()
    private var actualH = ICON.toFloat()

    override val width get() = actualW
    override val height get() = actualH

    override fun update(): Boolean {
        val packs = mc.resourcePackRepository.selectedPacks.filter { !it.isRequired }
        syncIcons(packs.map { it.id })

        val pack = if (ignoreOverlay) packs.firstOrNull() else packs.lastOrNull()
        if (pack?.id == lastPackId && ignoreOverlay == lastIgnoreOverlay) return false
        lastPackId = pack?.id
        lastIgnoreOverlay = ignoreOverlay
        recompute(pack)
        return false
    }

    private fun recompute(pack: Pack?) {
        val font = mc.font
        packId = pack?.id
        packTitle = pack?.title?.string ?: DEFAULT_TITLE
        descriptionLines = font.split(pack?.description ?: Component.literal(DEFAULT_DESCRIPTION), DESCRIPTION_WIDTH)

        val textW = max(font.width(packTitle), descriptionLines.maxOfOrNull { font.width(it) } ?: 0)
        val textH = font.lineHeight * (1 + descriptionLines.size) + TITLE_GAP
        actualW = (ICON + GAP + textW).toFloat()
        actualH = max(ICON, textH).toFloat()
    }

    override fun render(graphics: GuiGraphics) {
        if (showBackground) {
            graphics.fill(0, 0, actualW.toInt(), actualH.toInt(), bgColor)
        }

        val icon = iconFor(packId)
        //? if < 1.21.4
        //graphics.blit(icon, 0, 0, 0f, 0f, ICON, ICON, ICON, ICON)
        //? if >= 1.21.4 && < 1.21.8
        //graphics.blit(RenderType::guiTextured, icon, 0, 0, 0f, 0f, ICON, ICON, ICON, ICON)
        //? if >= 1.21.8
        graphics.blit(RenderPipelines.GUI_TEXTURED, icon, 0, 0, 0f, 0f, ICON, ICON, ICON, ICON)

        val font = mc.font
        val textX = ICON + GAP
        //? if < 26
        graphics.drawString(font, packTitle, textX, 0, textColor)
        //? if >= 26
        //graphics.text(font, packTitle, textX, 0, textColor)

        var lineY = font.lineHeight + TITLE_GAP
        for (line in descriptionLines) {
            //? if < 26
            graphics.drawString(font, line, textX, lineY, textColor)
            //? if >= 26
            //graphics.text(font, line, textX, lineY, textColor)
            lineY += font.lineHeight
        }
    }
}
