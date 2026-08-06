package org.polyfrost.evergreenhud.client.utils

import org.polyfrost.oneconfig.api.config.v1.Properties.ktProperty
import org.polyfrost.oneconfig.api.config.v1.Property
import org.polyfrost.oneconfig.api.config.v1.Tree
import org.polyfrost.oneconfig.api.hud.v1.TextHud
import java.util.function.Supplier

/**
 * A [TextHud] which keeps the user's own hide toggle separate from hiding the HUD does to itself, such as
 * hiding after a period of inactivity. HUDs should never write to [hidden] for that, as it would turn the
 * user's toggle back on the next time their condition clears - use [autoHidden] instead.
 *
 * Only the user's toggle is persisted, so a HUD which happens to be auto-hidden when the config is saved does
 * not come back hidden.
 */
abstract class AutoHideTextHud(
    id: String,
    title: String,
    category: Category,
    prefix: String,
    suffix: String = "",
) : SpacedTextHud(id, title, category, prefix, suffix) {

    /** The user's hide toggle. This is the value that gets serialized. */
    var manuallyHidden = false
        set(value) {
            field = value
            syncHidden()
        }

    /** Set by the HUD itself when its own hide condition triggers. Never serialized. */
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
        // rebind the persisted property away from the combined hidden state onto the user's toggle
        tree.set(
            "hidden",
            ktProperty(this::manuallyHidden)
                .addDisplayCondition(Supplier { Property.Display.HIDDEN })
        )
    }
}
