package org.polyfrost.evergreenhud.hud.player

import net.minecraft.client.Minecraft
import net.minecraft.entity.Entity
import org.polyfrost.evergreenhud.ClientDamageEntityEvent
import org.polyfrost.evergreenhud.utils.decimalFormat
import org.polyfrost.oneconfig.api.config.v1.annotations.Checkbox
import org.polyfrost.oneconfig.api.config.v1.annotations.Slider
import org.polyfrost.oneconfig.api.config.v1.annotations.Text
import org.polyfrost.oneconfig.api.event.v1.eventHandler
import org.polyfrost.oneconfig.api.hud.v1.TextHud
import org.polyfrost.polyui.unit.seconds

class Reach : TextHud("Reach: ", " blocks") {
    @Checkbox(title = "Trailing Zeros")
    var trailingZeros = false

    @Slider(title = "Accuracy", min = 0F, max = 15F)
    var accuracy = 1

    @Slider(title = "Discard Time", min = 1000F, max = 10000F)
    var discardTime = 3000

    @Text(title = "No Hit Message")
    var noHitMessage = "0"

    private var df = decimalFormat(accuracy, trailingZeros)
    private var reach = 0.0
    private var lastTime = 0L

    override fun initialize() {
        if (isReal) {
            addCallback("accuracy") { value: Int ->
                df = decimalFormat(value, trailingZeros)
            }
            addCallback("trailingZeros") { state: Boolean ->
                df = decimalFormat(accuracy, state)
            }
            eventHandler { event: ClientDamageEntityEvent ->
                if (event.attacker == Minecraft.getMinecraft().thePlayer) {
                    val reach = getReachDistanceFromEntity(event.target)
                    if (reach == 0.0) return
                    this.reach = reach
                    lastTime = System.currentTimeMillis()
                }
            }
        }
    }

    override fun getText(): String {
        if (reach == 0.0) sb.append(noHitMessage)
        else sb.append(reach)
        return null
    }

    override fun update(): Boolean {
        if (System.currentTimeMillis() - lastTime > discardTime) reach = 0.0
        return true
    }

    override fun updateFrequency() = 1.seconds

    private fun getReachDistanceFromEntity(entity: Entity): Double {
        val mc = Minecraft.getMinecraft()
        mc.mcProfiler.startSection("Calculate Reach Dist")

        // How far will ray travel before ending
        val maxSize = 6.0 // use 6 because creative mode is 6 and any more is literally reach
        // Bounding box of entity
        val otherBB = entity.entityBoundingBox
        // This is where people found out that F3+B is not accurate for hitboxes,
        // it makes hitboxes bigger by certain amount
        val collisionBorderSize: Float = entity.collisionBorderSize
        val otherHitbox = otherBB.expand(
            collisionBorderSize.toDouble(),
            collisionBorderSize.toDouble(),
            collisionBorderSize.toDouble()
        )
        // Not quite sure what the difference is between these two vectors
        // In actual code where this is taken from, partialTicks is always 1.0
        // So this won't decrease accuracy
        val eyePos = mc.thePlayer.getPositionEyes(1.0f)
        val lookPos = mc.thePlayer.getLook(1.0f)
        // Get vector for raycast
        val adjustedPos = eyePos.addVector(lookPos.xCoord * maxSize, lookPos.yCoord * maxSize, lookPos.zCoord * maxSize)
        val movingObjectPosition = otherHitbox.calculateIntercept(eyePos, adjustedPos) ?: return 0.0
        // This will trigger if hit distance is more than maxSize
        val otherEntityVec = movingObjectPosition.hitVec
        // finally calculate distance between both vectors
        val dist = eyePos.distanceTo(otherEntityVec)
        mc.mcProfiler.endSection()
        return dist
    }

    override fun title() = "Reach"

    override fun id() = "evergreenhud/reach.json"

    override fun category() = Category.COMBAT
}