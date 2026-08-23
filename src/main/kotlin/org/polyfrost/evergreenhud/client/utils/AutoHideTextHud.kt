package org.polyfrost.evergreenhud.client.utils

import org.polyfrost.oneconfig.api.config.v1.Properties.ktProperty
import org.polyfrost.oneconfig.api.config.v1.Property
import org.polyfrost.oneconfig.api.config.v1.Tree
import org.polyfrost.oneconfig.api.event.v1.eventHandler
import org.polyfrost.oneconfig.api.event.v1.events.TickEvent

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

    private var lastAutoHideCheck = 0L

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

    override fun setup() {
        super.setup()
        if (isReal) eventHandler { _: TickEvent.End -> refreshWhileAutoHidden() }
    }

    private fun refreshWhileAutoHidden() {
        if (!autoHidden || manuallyHidden) return

        val now = System.nanoTime()
        val frequency = updateFrequency()
        if (frequency > 0L && now - lastAutoHideCheck < frequency) return
        lastAutoHideCheck = now

        updateAndRecalculate()
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
