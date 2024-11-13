package org.polyfrost.evergreenhud.hud.hudlist

import org.polyfrost.oneconfig.api.config.v1.Config
import org.polyfrost.oneconfig.api.config.v1.elements.BasicOption
import org.polyfrost.oneconfig.gui.elements.BasicButton
import org.polyfrost.oneconfig.hud.Hud
import org.polyfrost.oneconfig.renderer.asset.SVG
import org.polyfrost.oneconfig.utils.v1.InputHandler
import org.polyfrost.oneconfig.utils.v1.color.ColorPalette

private val PLUS_ICON = SVG("/assets/evergreenhud/plus.svg")

@Suppress("UnstableAPIUsage")
class HudListOption<T : Hud>(
    val hudList: HudList<T>,
    val config: Config,
    description: String,
    category: String,
    subcategory: String
) : BasicOption(null, null, "", description, category, subcategory, 2) {
    private val addButton = BasicButton(32, 32, PLUS_ICON, BasicButton.ALIGNMENT_CENTER, ColorPalette.PRIMARY)
    private val wrappedList = hudList.mapTo(ArrayList()) { hud ->
        WrappedHud(this, hud)
    }
    private var planToRemove: WrappedHud<T>? = null

    init {
        addButton.setClickAction {
            val hud = hudList.newHud()
            wrappedList.add(WrappedHud(this, hud))
            hudList.add(hud)
        }
    }

    override fun getHeight() = wrappedList.size * 48 + 32

    override fun draw(vg: Long, x: Int, y: Int, inputHandler: InputHandler) {
        var nextY = y

        for (hud in wrappedList) {
            hud.drawInList(vg, x, nextY, inputHandler)
            nextY += 48
        }

        addButton.draw(vg, x.toFloat(), nextY.toFloat(), inputHandler)

        checkToRemove()
    }

    fun planToRemove(hud: WrappedHud<T>) {
        planToRemove = hud
    }

    private fun checkToRemove() {
        val removing = (planToRemove ?: return)
        removing.remove()
        wrappedList.remove(removing)
        hudList.remove(removing.hud)
        planToRemove = null
    }
}