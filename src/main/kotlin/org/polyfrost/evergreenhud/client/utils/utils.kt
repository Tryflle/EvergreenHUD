package org.polyfrost.evergreenhud.client.utils

//? if > 1.21.1
import net.minecraft.util.profiling.Profiler
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.projectile.ProjectileUtil
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec3
import org.polyfrost.compose.render.PolyColor
import org.polyfrost.oneconfig.utils.v1.dsl.mc

// Maximum reach distance in blocks, used for ray casting
private const val MAX_REACH_DISTANCE = 6.0f

private val Entity.accurateCollisionBox: AABB
    get() = boundingBox.expandTowards(pickRadius.toDouble(), pickRadius.toDouble(), pickRadius.toDouble())

val Entity.uniqueEntityId: Int
    get() = id

fun StringBuilder.replace(string: String, value: String): StringBuilder {
    val index = indexOf(string)
    if (index != -1) {
        replace(index, index + string.length, value)
    }

    return this
}

fun calculateReachDistanceToEntity(entity: Entity): Float {
    //? if > 1.21.1
    val profiler = Profiler.get()
    //? if <= 1.21.1
    /*val profiler = mc.profiler*/
    val player = mc.player ?: return 0f
    if (!player.isAlive) return 0f
    profiler.push("evergreenhud_reach_distance_calculation")

    var result = 0f
    val collisionBox = entity.accurateCollisionBox
    val eyePos = player.getEyePosition(1.0f)
    val lookPos = player.getViewVector(1.0f)
    val adjustedPos = eyePos.add(lookPos.x * MAX_REACH_DISTANCE, lookPos.y * MAX_REACH_DISTANCE, lookPos.z * MAX_REACH_DISTANCE)
    val movingObjectPosition = collisionBox.castTo(entity, eyePos, adjustedPos)
    if (movingObjectPosition != null) {
        val otherEntityVec = movingObjectPosition.location
        result = eyePos.distanceTo(otherEntityVec).toFloat()
    }
    profiler.pop()
    return result
}

private fun AABB.castTo(
    entity: Entity,
    start: Vec3,
    end: Vec3,
): HitResult? {
    return ProjectileUtil.getEntityHitResult(entity, start, end, this, { true }, MAX_REACH_DISTANCE.toDouble())
}

inline fun <L, E> L.fastRemoveIfReversed(predicate: (E) -> Boolean) where L : MutableList<E>, L : RandomAccess {
    for (i in indices.reversed()) {
        if (i > this.size - 1) {
            //PolyUI.LOGGER.error("FAST_WARN_CONCURRENT_MODIFICATION_RM_REV")
            continue
        }
        if (predicate(this[i])) {
            this.removeAt(i.coerceAtMost(size - 1))
        }
    }
}

fun PolyColor.toComposeColor(): androidx.compose.ui.graphics.Color {
    return androidx.compose.ui.graphics.Color(red, green, blue, alpha)
}

fun androidx.compose.ui.graphics.Color.toPolyColor(): PolyColor {
    return PolyColor.hex(value.toInt())
}