package org.polyfrost.evergreenhud.utils

import org.polyfrost.oneconfig.api.event.v1.EventManager
import org.polyfrost.oneconfig.api.event.v1.eventHandler
import org.polyfrost.oneconfig.api.event.v1.events.Event
import org.polyfrost.oneconfig.api.event.v1.events.RenderEvent
import org.polyfrost.oneconfig.api.event.v1.events.TickEvent

object FrameTimeHelper {
    private var lastTime = System.nanoTime()
    val frameTimes = ArrayList<Long>(40)

    init {
        eventHandler { _: RenderEvent.End ->
            frameTimes += System.nanoTime() - lastTime
            lastTime = System.nanoTime()
        }
        eventHandler { _: TickEvent.End ->
            val ft = frameTimes
            ft.sort()
            val consistency = (ft.last() - ft.first()).toDouble() / ft.size / ft.sum()
            val avg = ft.average().run { if (isFinite()) this else 1.0 }
            val p50 = percentile(ft, 0.50)
            val p95 = percentile(ft, 0.95)
            val p99 = percentile(ft, 0.99)
            EventManager.INSTANCE.post(FrameData(consistency, avg, p50, p95, p99))
            ft.clear()
        }
    }

    private fun percentile(list: List<Long>, percentile: Double): Double {
        val idx = list.size * percentile - 1
        val l = idx.toInt().coerceAtLeast(0)
        val u = (l + 1).coerceAtMost(list.size - 1)
        val fractional = idx - l
        val lower = list[l].toDouble()
        val higher = list[u].toDouble()
        return lower + (higher - lower) * fractional
    }

    data class FrameData(val consistency: Double, val mean: Double, val median: Double, val p95: Double, val p99: Double) : Event
}