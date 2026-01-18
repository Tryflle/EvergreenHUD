package org.polyfrost.evergreenhud.client.utils

import dev.deftu.omnicore.api.client.client
import dev.deftu.omnicore.api.client.player
import dev.deftu.omnicore.api.client.profiled
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.projectile.ProjectileUtil
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec3

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
    return client.profiled<Float>("evergreenhud_reach_distance_calculation") {
        val player = player ?: return@profiled 0f
        if (!player.isAlive) {
            return@profiled 0f
        }

        val collisionBox = entity.accurateCollisionBox
        val eyePos = player.getEyePosition(1.0f)
        val lookPos = player.getViewVector(1.0f)
        val adjustedPos = eyePos.add(lookPos.x * MAX_REACH_DISTANCE, lookPos.y * MAX_REACH_DISTANCE, lookPos.z * MAX_REACH_DISTANCE)
        val movingObjectPosition = collisionBox.castTo(entity, eyePos, adjustedPos) ?: return@profiled 0f
        val otherEntityVec = movingObjectPosition.location
        eyePos.distanceTo(otherEntityVec).toFloat()
    }
}

private fun AABB.castTo(
    entity: Entity,
    start: Vec3,
    end: Vec3,
): HitResult? {
    return ProjectileUtil.getEntityHitResult(entity, start, end, this, { true }, MAX_REACH_DISTANCE.toDouble())
}
