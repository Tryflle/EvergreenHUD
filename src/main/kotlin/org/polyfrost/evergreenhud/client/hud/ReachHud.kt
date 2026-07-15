package org.polyfrost.evergreenhud.client.hud

import org.polyfrost.evergreenhud.client.ClientDamageEntityEvent
import org.polyfrost.evergreenhud.client.utils.GenericNumberHud
import org.polyfrost.evergreenhud.client.utils.calculateReachDistanceToEntity
import org.polyfrost.oneconfig.api.config.v1.annotations.Slider
import org.polyfrost.oneconfig.api.config.v1.annotations.Text
import org.polyfrost.oneconfig.api.event.v1.eventHandler
import org.polyfrost.oneconfig.utils.v1.dsl.mc
import kotlin.time.Duration.Companion.seconds

class ReachHud : GenericNumberHud(
    title = "Reach",
    category = Category.COMBAT,
    suffix = "blocks"
) {
    @Slider(title = "Discard Time", min = 1000F, max = 10000F)
    var discardTime = 3000

    @Text(title = "No Hit Message")
    var noHitMessage = "0"

    private var lastTime = 0L

    override fun setup() {
        super.setup()
        eventHandler { event: ClientDamageEntityEvent ->
            if (event.attacker == mc.player) {
                val reach = calculateReachDistanceToEntity(event.target)
                if (reach == 0f) {
                    return@eventHandler false
                }

                this.value = reach
                this.lastTime = System.currentTimeMillis()
                updateWithNumber(reach)
            }

            return@eventHandler false
        }

        if (isReal) {
            updateWhenChanged("noHitMessage")
        }
    }

    override fun getText(): String? {
        if (value == 0f) {
            return noHitMessage
        }

        return super.getText()
    }

    override fun update(): Boolean {
        if (System.currentTimeMillis() - lastTime > discardTime) {
            value = 0f
        }

        return super.update()
    }

    override fun updateFrequency() = 1.seconds.inWholeNanoseconds
}
