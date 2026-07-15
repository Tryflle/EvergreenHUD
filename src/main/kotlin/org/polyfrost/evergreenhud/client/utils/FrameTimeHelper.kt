package org.polyfrost.evergreenhud.client.utils

//? if >= 26
//import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents as WorldRenderEvents
//? if >= 1.21.10 && < 26
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents
//? if < 1.21.10
//import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import org.polyfrost.oneconfig.api.event.v1.EventManager
import org.polyfrost.oneconfig.api.event.v1.eventHandler
import org.polyfrost.oneconfig.api.event.v1.events.Event
import org.polyfrost.oneconfig.api.event.v1.events.TickEvent

object FrameTimeHelper {
    private var lastTime = System.nanoTime()
    private val frameTimes = ArrayList<Long>(60)
    private var tickCount = 0

    fun initialize() {
        //? if >= 1.21.10
        WorldRenderEvents.END_MAIN.register {
        //? if < 1.21.10
        //WorldRenderEvents.END.register {
            frameTimes += System.nanoTime() - lastTime
            lastTime = System.nanoTime()
        }

        eventHandler { _: TickEvent.End ->
            tickCount++
            if (tickCount > 10) {
                tickCount = 0
                val frameTimes = frameTimes
                if (frameTimes.size > 1) {
                    frameTimes.sort()
                    val sum = frameTimes.sum()
                    val consistency = (frameTimes.last() - frameTimes.first()).toDouble() / frameTimes.size / sum
                    val avg = (sum.toDouble() / frameTimes.size).run { if (isFinite()) this else 1.0 }
                    val p50 = percentile(frameTimes, 0.50)
                    val p95 = percentile(frameTimes, 0.95)
                    val p99 = percentile(frameTimes, 0.99)
                    EventManager.INSTANCE.post(FrameDataEvent(consistency, avg, p50, p95, p99, frameTimes.size))
                    frameTimes.clear()
                }
            }
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

    data class FrameDataEvent(
        val consistency: Double,
        val mean: Double,
        val median: Double,
        val p95: Double,
        val p99: Double,
        val nframes: Int
    ) : Event
}
