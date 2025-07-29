package org.polyfrost.evergreenhud.client.utils

import dev.deftu.omnicore.client.OmniClientPlayer
import dev.deftu.omnicore.common.OmniProfiler
import net.minecraft.entity.Entity
import net.minecraft.util.AxisAlignedBB

// Maximum reach distance in blocks, used for ray casting
private const val MAX_REACH_DISTANCE = 6.0f

private val Entity.accurateCollisionBox: AxisAlignedBB
    get() = entityBoundingBox.expand(collisionBorderSize.toDouble(), collisionBorderSize.toDouble(), collisionBorderSize.toDouble())

fun StringBuilder.replace(string: String, value: String): StringBuilder {
    val index = indexOf(string)
    if (index != -1) {
        replace(index, index + string.length, value)
    }

    return this
}

@Suppress("SENSELESS_COMPARISON")
fun calculateReachDistanceToEntity(entity: Entity): Float {
    var result = 0f
    OmniProfiler.withProfiler("reach_distance_calculation") {
        val player = OmniClientPlayer.getInstance()
        if (player == null || !player.isEntityAlive) {
            result = 0f
            return@withProfiler
        }

        val collisionBox = entity.accurateCollisionBox
        val eyePos = player.getPositionEyes(1.0f)
        val lookPos = player.getLook(1.0f)
        val adjustedPos = eyePos.addVector(lookPos.xCoord * MAX_REACH_DISTANCE, lookPos.yCoord * MAX_REACH_DISTANCE, lookPos.zCoord * MAX_REACH_DISTANCE)
        val movingObjectPosition = collisionBox.calculateIntercept(eyePos, adjustedPos)
        if (movingObjectPosition == null) {
            result = 0f
            return@withProfiler
        }

        val otherEntityVec = movingObjectPosition.hitVec
        result = eyePos.distanceTo(otherEntityVec).toFloat()
    }

    return result
}
