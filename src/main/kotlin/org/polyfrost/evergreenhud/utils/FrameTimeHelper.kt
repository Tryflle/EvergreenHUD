package org.polyfrost.evergreenhud.utils

import org.polyfrost.oneconfig.api.event.v1.eventHandler
import org.polyfrost.oneconfig.api.event.v1.events.RenderEvent
import org.polyfrost.oneconfig.api.event.v1.events.TickEvent

object FrameTimeHelper {
    private var lastTime = System.currentTimeMillis().toFloat()
    val frameTimes = ArrayList<Float>(40)

    init {
        eventHandler { _: RenderEvent.End ->
            frameTimes += System.currentTimeMillis() - lastTime
            lastTime = System.currentTimeMillis().toFloat()
        }
        eventHandler { _: TickEvent.End ->
            frameTimes.clear()
        }
    }
}