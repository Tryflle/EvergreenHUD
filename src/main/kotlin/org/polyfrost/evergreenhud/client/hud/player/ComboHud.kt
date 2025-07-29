package org.polyfrost.evergreenhud.client.hud.player

import dev.deftu.omnicore.client.OmniClientPlayer
import net.minecraft.client.Minecraft
import net.minecraft.network.play.server.S19PacketEntityStatus
import org.polyfrost.evergreenhud.client.ClientDamageEntityEvent
import org.polyfrost.oneconfig.api.config.v1.annotations.Slider
import org.polyfrost.oneconfig.api.config.v1.annotations.Text
import org.polyfrost.oneconfig.api.event.v1.eventHandler
import org.polyfrost.oneconfig.api.event.v1.events.PacketEvent
import org.polyfrost.oneconfig.api.hud.v1.TextHud
import org.polyfrost.polyui.unit.seconds

// CHECK OK
class ComboHud : TextHud(
    id = "combo.json",
    title = "Combo",
    category = Category.COMBAT,
    prefix = "Combo: ",
    suffix = " hits"
) {

    @Slider(title = "Discard Time", min = 1f, max = 10f)
    var discardTime = 2f

    @Text(title = "No Hit Message")
    var noHitMessage = "0"

    private var sentAttackTime = 0L
    private var lastHitTime = 0L
    private var lastAttackId = 0
    private var sentAttack = 0

    private var currentCombo = 0
        set(value) {
            if (field == value) return
            field = value
            updateAndRecalculate()
        }

    override fun setup() {
        super.setup()
        eventHandler { (attacker, target): ClientDamageEntityEvent ->
            if (attacker != OmniClientPlayer.getInstance()) {
                return@eventHandler
            }

            sentAttack = target.entityId
            sentAttackTime = System.currentTimeMillis()
        }

        eventHandler { (packet): PacketEvent.Receive ->
            if (packet !is S19PacketEntityStatus || packet.opCode.toInt() != 2) {
                return@eventHandler
            }

            val mc = Minecraft.getMinecraft()
            val target = packet.getEntity(mc.theWorld) ?: return@eventHandler

            if (sentAttack != -1 && target.entityId == sentAttack) {
                sentAttack = -1
                val time = System.currentTimeMillis()
                if (time - sentAttackTime > discardTime * 1000L) {
                    sentAttackTime = 0L
                    currentCombo = 0
                    return@eventHandler
                }

                if (lastAttackId == target.entityId) {
                    currentCombo++
                } else {
                    currentCombo = 1
                }

                lastHitTime = time
                lastAttackId = target.entityId
            } else if (target.entityId == mc.thePlayer.entityId) {
                currentCombo = 0
            }

            updateAndRecalculate()
        }

        if (isReal) {
            updateWhenChanged("noHitMessage")
        }
    }

    override fun updateFrequency() = 1.seconds

    override fun getText(): String? {
        if (System.currentTimeMillis() - lastHitTime >= discardTime * 1000L) {
            currentCombo = 0
        }

        if (currentCombo == 0) {
            sb.append(noHitMessage)
        } else {
            sb.append(currentCombo)
        }

        return null
    }

}
