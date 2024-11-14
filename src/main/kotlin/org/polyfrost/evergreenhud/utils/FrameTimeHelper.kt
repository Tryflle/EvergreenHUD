package org.polyfrost.evergreenhud.utils

import org.polyfrost.oneconfig.api.event.v1.eventHandler
import org.polyfrost.oneconfig.api.event.v1.events.RenderEvent
import org.polyfrost.oneconfig.api.event.v1.events.TickEvent

object FrameTimeHelper {
    private var lastTime = System.currentTimeMillis().toDouble()
    val frameTimes = ArrayList<Double>(60)

    init {
        eventHandler { _: RenderEvent.End ->
            frameTimes += System.currentTimeMillis() - lastTime
            lastTime = System.currentTimeMillis().toDouble()
        }
        eventHandler { _: TickEvent.End ->
            frameTimes.clear()
        }
    }
}