package org.polyfrost.evergreenhud.utils

import org.polyfrost.oneconfig.api.event.v1.EventManager
import org.polyfrost.oneconfig.api.event.v1.events.event.RenderEvent
import org.polyfrost.oneconfig.api.event.v1.events.event.Stage
import org.polyfrost.oneconfig.api.event.v1.events.event.TickEvent
import org.polyfrost.oneconfig.libs.eventbus.Subscribe

object FrameTimeHelper {
    private var lastTime = System.currentTimeMillis().toDouble()
    val frameTimes = mutableListOf<Double>()

    init {
        EventManager.INSTANCE.register(this)
    }

    @Subscribe
    private fun onRenderTick(event: RenderEvent) {
        if (event.stage == Stage.END) {
            frameTimes += System.currentTimeMillis() - lastTime
            lastTime = System.currentTimeMillis().toDouble()
        }
    }

    @Subscribe
    private fun onTick(event: TickEvent) {
        if (event.stage == Stage.END) {
            frameTimes.clear()
        }
    }

}