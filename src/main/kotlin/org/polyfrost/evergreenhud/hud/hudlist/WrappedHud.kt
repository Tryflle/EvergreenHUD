package org.polyfrost.evergreenhud.hud.hudlist

import org.polyfrost.oneconfig.api.config.v1.annotations.HUD
import org.polyfrost.oneconfig.api.config.v1.core.ConfigUtils
import org.polyfrost.oneconfig.api.config.v1.elements.OptionPage
import org.polyfrost.oneconfig.gui.OneConfigGui
import org.polyfrost.oneconfig.gui.elements.BasicButton
import org.polyfrost.oneconfig.gui.pages.ModConfigPage
import org.polyfrost.oneconfig.hud.HUDUtils
import org.polyfrost.oneconfig.hud.Hud
import org.polyfrost.oneconfig.internal.hud.HudCore
import org.polyfrost.oneconfig.renderer.asset.SVG
import org.polyfrost.oneconfig.renderer.font.Fonts
import org.polyfrost.oneconfig.utils.v1.InputHandler
import org.polyfrost.oneconfig.utils.v1.color.ColorPalette
import org.polyfrost.oneconfig.utils.v1.dsl.nanoVGHelper

private val MINUS_ICON = SVG("/assets/evergreenhud/minus.svg")
private const val WHITE_90 = 0xE5FFFFFF.toInt()

@Suppress("UnstableAPIUsage")
class WrappedHud<T : Hud>(
    private val hudListOption: HudListOption<T>,
    @HUD(name = "Options") val hud: T
) {
    private val removeButton = BasicButton(32, 32, MINUS_ICON, BasicButton.ALIGNMENT_CENTER, ColorPalette.PRIMARY_DESTRUCTIVE)
    private val pageButton = BasicButton(256, 32, "Edit", BasicButton.ALIGNMENT_CENTER, ColorPalette.PRIMARY)
    private val optionPage = OptionPage("Options", hudListOption.config.mod)
    private val page: ModConfigPage

    init {
        HUDUtils.addHudOptions(optionPage, WrappedHud::class.java.getDeclaredField("hud"), this, hudListOption.config)
        // subcategory must be generated before creating config page
        page = ModConfigPage(optionPage)

        removeButton.setClickAction {
            hudListOption.planToRemove(this)
        }

        pageButton.setClickAction {
            OneConfigGui.INSTANCE.openPage(page)
        }
    }

    fun drawInList(vg: Long, x: Int, y: Int, inputHandler: InputHandler) {
        removeButton.draw(vg, x.toFloat(), y.toFloat(), inputHandler)
        nanoVGHelper.drawText(vg, hudListOption.hudList.getHudName(hud), (x + 64).toFloat(), (y + 17).toFloat(), WHITE_90, 14f, Fonts.MEDIUM)
        pageButton.draw(vg, (x + 736).toFloat(), y.toFloat(), inputHandler)
    }

    fun remove() {
        HudCore.huds.values.remove(hud)
        val subcategory = ConfigUtils.getSubCategory(optionPage, "General", "") ?: return
        HudCore.hudOptions.removeAll(subcategory.options)
    }
}
