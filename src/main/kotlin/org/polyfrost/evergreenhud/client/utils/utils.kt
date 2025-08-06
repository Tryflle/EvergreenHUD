package org.polyfrost.evergreenhud.client.utils

import dev.deftu.omnicore.client.OmniClientPlayer
import dev.deftu.omnicore.common.profile
import net.minecraft.entity.Entity
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.MovingObjectPosition
import net.minecraft.util.Vec3

//#if MC >= 1.16.5
//$$ import net.minecraft.world.entity.projectile.ProjectileUtil
//#endif

// Maximum reach distance in blocks, used for ray casting
private const val MAX_REACH_DISTANCE = 6.0f

private val Entity.accurateCollisionBox: AxisAlignedBB
    get() = entityBoundingBox.expand(collisionBorderSize.toDouble(), collisionBorderSize.toDouble(), collisionBorderSize.toDouble())

val Entity.uniqueEntityId: Int
    get() {
        //#if MC >= 1.17.1
        //$$ return id
        //#else
        return entityId
        //#endif
    }

fun StringBuilder.replace(string: String, value: String): StringBuilder {
    val index = indexOf(string)
    if (index != -1) {
        replace(index, index + string.length, value)
    }

    return this
}

fun calculateReachDistanceToEntity(entity: Entity): Float {
    return profile<Float>("evergreenhud_reach_distance_calculation") {
        val player = OmniClientPlayer.getInstance()
        if (player == null || !player.isEntityAlive) {
            return@profile 0f
        }

        val collisionBox = entity.accurateCollisionBox
        val eyePos = player.getPositionEyes(1.0f)
        val lookPos = player.getLook(1.0f)
        val adjustedPos = eyePos.addVector(lookPos.xCoord * MAX_REACH_DISTANCE, lookPos.yCoord * MAX_REACH_DISTANCE, lookPos.zCoord * MAX_REACH_DISTANCE)
        val movingObjectPosition = collisionBox.castTo(entity, eyePos, adjustedPos) ?: return@profile 0f
        val otherEntityVec = movingObjectPosition.hitVec
        eyePos.distanceTo(otherEntityVec).toFloat()
    }
}

private fun AxisAlignedBB.castTo(
    entity: Entity,
    start: Vec3,
    end: Vec3,
): MovingObjectPosition? {
    //#if MC >= 1.16.5
    //$$ return ProjectileUtil.getEntityHitResult(entity, start, end, this, { true }, MAX_REACH_DISTANCE.toDouble())
    //#else
    return calculateIntercept(start, end)
    //#endif
}
