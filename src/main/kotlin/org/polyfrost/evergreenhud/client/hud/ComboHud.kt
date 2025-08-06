package org.polyfrost.evergreenhud.client.hud

import dev.deftu.omnicore.client.OmniClientPlayer
import org.polyfrost.evergreenhud.client.ServerDamageEntityEvent
import org.polyfrost.evergreenhud.client.utils.uniqueEntityId
import org.polyfrost.oneconfig.api.config.v1.annotations.Slider
import org.polyfrost.oneconfig.api.config.v1.annotations.Text
import org.polyfrost.oneconfig.api.event.v1.eventHandler
import org.polyfrost.oneconfig.api.event.v1.events.TickEvent
import org.polyfrost.oneconfig.api.hud.v1.TextHud

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

    private var lastHitTime = 0L
    private var lastAttackId = 0

    private var currentCombo = 0
        set(value) {
            if (field == value) return
            field = value
            updateAndRecalculate()
        }

    override fun setup() {
        super.setup()

        eventHandler<TickEvent.Start> {
            if (System.currentTimeMillis() - lastHitTime >= discardTime * 1000L) {
                currentCombo = 0
            }
        }

        eventHandler { (attacker, target): ServerDamageEntityEvent ->
            if (target == OmniClientPlayer.getInstance()) {
                currentCombo = 0
                return@eventHandler
            }

            if (attacker != OmniClientPlayer.getInstance()) {
                return@eventHandler
            }

            println("Shmacked ${target.name} with ID ${target.uniqueEntityId}")
            if (target.uniqueEntityId == lastAttackId) {
                currentCombo++
            } else {
                currentCombo = 1
            }

            lastHitTime = System.currentTimeMillis()
            lastAttackId = target.uniqueEntityId
        }

        if (isReal) {
            updateWhenChanged("noHitMessage")
        }
    }

    override fun getText(): String? {
        if (currentCombo == 0) {
            sb.append(noHitMessage)
        } else {
            sb.append(currentCombo)
        }

        return null
    }

}
