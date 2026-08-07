package org.polyfrost.evergreenhud.client.utils

import org.polyfrost.oneconfig.api.config.v1.Properties.ktProperty
import org.polyfrost.oneconfig.api.config.v1.Property
import org.polyfrost.oneconfig.api.config.v1.Tree
import org.polyfrost.oneconfig.api.hud.v1.TextHud
import java.util.function.Supplier

/**
 * Keeps the user hide toggle separate from automatic hiding so a cleared auto condition cannot
 * re-enable the toggle HUDs must write [autoHidden] rather than [hidden]
 */
abstract class AutoHideTextHud(
    id: String,
    title: String,
    category: Category,
    prefix: String,
    suffix: String = "",
) : SpacedTextHud(id, title, category, prefix, suffix) {

    /** The user toggle and the only serialized value */
    var manuallyHidden = false
        set(value) {
            field = value
            syncHidden()
        }

    /** Set by the HUD when its own hide condition triggers and never serialized */
    protected var autoHidden = false
        set(value) {
            if (field == value) return
            field = value
            syncHidden()
        }

    private var syncing = false

    override var hidden: Boolean
        get() = super.hidden
        set(value) {
            if (syncing) {
                super.hidden = value
                return
            }
            manuallyHidden = value
        }

    private fun syncHidden() {
        syncing = true
        hidden = manuallyHidden || autoHidden
        syncing = false
    }

    override fun addToSerialized(tree: Tree) {
        super.addToSerialized(tree)
        // rebind the persisted property onto the user toggle instead of the combined hidden state
        tree.set(
            "hidden",
            ktProperty(this::manuallyHidden)
                .addDisplayCondition(Supplier { Property.Display.HIDDEN })
        )
    }
}
