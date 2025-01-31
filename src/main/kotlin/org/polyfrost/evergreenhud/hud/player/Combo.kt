package org.polyfrost.evergreenhud.hud.player

import net.minecraft.client.Minecraft
import net.minecraft.network.play.server.S19PacketEntityStatus
import org.polyfrost.evergreenhud.ClientDamageEntityEvent
import org.polyfrost.oneconfig.api.config.v1.annotations.Slider
import org.polyfrost.oneconfig.api.config.v1.annotations.Text
import org.polyfrost.oneconfig.api.event.v1.eventHandler
import org.polyfrost.oneconfig.api.event.v1.events.ReceivePacketEvent
import org.polyfrost.oneconfig.api.event.v1.events.TickEvent
import org.polyfrost.oneconfig.api.hud.v1.TextHud


class Combo : TextHud(prefix = "Combo: ", suffix = " blocks") {
    // you can include config options here like normal, just as if it was a config. all the methods like addDependency, loadFrom
    // etc all work here as HUD extends from Config.
    @Slider(title = "Discard Time", min = 0F, max = 10F)
    var discardTime = 3

    @Text(title = "No Hit Message")
    var noHitMessage = "0"

    // to register this HUD with the system, we can do the following:
    // note that this needs to be done from another class, e.g. the main mod class would have:
    // HudManager.register(Combo())
    // this would ensure that the HUD is registered and can be displayed.

    // note how there is no transient or exclude, as excluding is now the default behavior.
    private var sentAttackTime = 0L
    private var lastHitTime = 0L
    private var lastAttackId = 0
    private var sentAttack = 0

    private var currentCombo = 0
        // here, I am using kotlin features to make the code cleaner later in the HUD.
        // you can just call update() after each set or whatever. update() is what will make the HUD text change.
        set(value) {
            if (field == value) return
            field = value
            updateAndRecalculate()
        }

    override fun initialize() {
        // using the new kotlin syntax purely because it looks so much nicer.
        eventHandler { (attacker, target): ClientDamageEntityEvent ->
            if (attacker != Minecraft.getMinecraft().thePlayer) {
                return@eventHandler
            }
            sentAttack = target.entityId
            sentAttackTime = System.currentTimeMillis()
        }

        eventHandler { event: ReceivePacketEvent ->
            val mc = Minecraft.getMinecraft()
            val packet = event.getPacket() as? S19PacketEntityStatus ?: return@eventHandler
            if (packet.opCode.toInt() != 2) return@eventHandler
            val target = packet.getEntity(mc.theWorld) ?: return@eventHandler

            if (sentAttack != -1 && target.entityId == sentAttack) {
                sentAttack = -1
                if (System.currentTimeMillis() - sentAttackTime > 2000L) {
                    sentAttackTime = 0L
                    currentCombo = 0
                    return@eventHandler
                }
                if (lastAttackId == target.entityId) {
                    currentCombo++
                } else {
                    currentCombo = 1
                }
                lastHitTime = System.currentTimeMillis()
                lastAttackId = target.entityId
            } else if (target.entityId == mc.thePlayer.entityId) {
                currentCombo = 0
            }
        }

        eventHandler { _: TickEvent.Start ->
            if (System.currentTimeMillis() - lastHitTime >= discardTime * 1000L) {
                currentCombo = 0
            }
        }

        if(isReal) {
            // if this hud is 'real', meaning it is not the one in the hud picker screen,
            // we add a simple callback to update the HUD text when the noHitMessage option
            // is modified by the user.
            updateWhenChanged("noHitMessage")
        }

        // required for setting up callbacks.
        super.initialize()
    }

    // these are no longer fields and instead these methods as there is no point in saving them in memory
    // they are just used for the HUD manager.
    override fun category() = Category.COMBAT

    // this method is called WHENEVER update() is called. it is what supplies the text to the HUD.
    override fun getText() = if (currentCombo == 0) noHitMessage else currentCombo.toString()

    // this is the tree ID of the HUD, which is the same as the ID you supply as if it were a config.
    override fun id() = "combo.json"

    // This is the title of the HUD, it is displayed in the HUD manager.
    override fun title() = "Combo"
}