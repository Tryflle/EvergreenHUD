package org.polyfrost.evergreenhud.client.utils

import org.polyfrost.oneconfig.api.config.v1.Properties.ktProperty
import org.polyfrost.oneconfig.api.config.v1.Property
import org.polyfrost.oneconfig.api.config.v1.Tree
import java.util.function.Supplier

abstract class AutoHideTextHud(
    id: String,
    title: String,
    category: Category,
    prefix: String,
    suffix: String = "",
) : SpacedTextHud(id, title, category, prefix, suffix) {

    var manuallyHidden = false
        set(value) {
            field = value
            syncHidden()
        }

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
        tree.set(
            "hidden",
            ktProperty(this::manuallyHidden)
                .addDisplayCondition { Property.Display.HIDDEN }
        )
    }
}
