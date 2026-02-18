package org.polyfrost.evergreenhud.client.utils.pinger

import dev.deftu.omnicore.api.client.network.OmniServerInfo
import dev.deftu.textile.Text
import dev.deftu.textile.minecraft.MCText
import dev.deftu.textile.minecraft.MCTextStyle
import dev.deftu.textile.minecraft.TextColors
import dev.deftu.textile.minecraft.asVanilla
import net.minecraft.network.Connection
import net.minecraft.network.DisconnectionDetails
import net.minecraft.network.protocol.ping.ClientboundPongResponsePacket
import net.minecraft.network.protocol.ping.ServerboundPingRequestPacket
import net.minecraft.network.protocol.status.ClientStatusPacketListener
import net.minecraft.network.protocol.status.ClientboundStatusResponsePacket
import net.minecraft.util.Util

class PingingPacketListener(
    private val server: OmniServerInfo,
    private val networkManager: Connection,
    private val callback: (Int) -> Unit
) : ClientStatusPacketListener {
    private var started = false
    private var startTime = -1L

    private val currentTime: Long
        get() = Util.getMillis()

    override fun onDisconnect(
        details: DisconnectionDetails,
    ) {
        val reason = details.reason
        println("Disconnected from server ${server.address} (${server.name}) with reason: ${MCText.wrap(reason).collapseToString()}")
        if (!started) {
            throw IllegalStateException("Could not query server ${server.address} (${server.name}) for ping status. Reason: ${MCText.wrap(reason).collapseToString()}")
        }
    }

    override fun handleStatusResponse(packetIn: ClientboundStatusResponsePacket) {
        if (started) {
            networkManager.disconnect(
                Text.literal(
                    "Received unrequested status packet",
                    MCTextStyle.color(TextColors.RED)
                ).asVanilla()
            )

            return
        }

        println("Received server info packet from ${server.address} (${server.name})")
        started = true
        startTime = currentTime
        networkManager.send(ServerboundPingRequestPacket(startTime))
    }

    override fun handlePongResponse(packetIn: ClientboundPongResponsePacket) {
        println("Received pong packet from ${server.address} (${server.name})")
        callback((currentTime - startTime).toInt())
    }

    override fun isAcceptingMessages(): Boolean {
        return networkManager.isConnected
    }
}