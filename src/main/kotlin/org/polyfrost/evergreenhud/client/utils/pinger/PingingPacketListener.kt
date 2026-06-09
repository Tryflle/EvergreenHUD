package org.polyfrost.evergreenhud.client.utils.pinger

import net.minecraft.ChatFormatting
import net.minecraft.client.multiplayer.ServerData
import net.minecraft.network.Connection
import net.minecraft.network.DisconnectionDetails
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.ping.ClientboundPongResponsePacket
import net.minecraft.network.protocol.ping.ServerboundPingRequestPacket
import net.minecraft.network.protocol.status.ClientStatusPacketListener
import net.minecraft.network.protocol.status.ClientboundStatusResponsePacket
import net.minecraft.util.Util

class PingingPacketListener(
    private val server: ServerData,
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
        println("Disconnected from server ${server.ip} (${server.name}) with reason: ${reason.string}")
        if (!started) {
            throw IllegalStateException("Could not query server ${server.ip} (${server.name}) for ping status. Reason: ${reason.string}")
        }
    }

    override fun handleStatusResponse(packetIn: ClientboundStatusResponsePacket) {
        if (started) {
            networkManager.disconnect(
                Component.literal(
                    "Received unrequested status packet",
                ).withStyle(ChatFormatting.RED),
            )

            return
        }

        println("Received server info packet from ${server.ip} (${server.name})")
        started = true
        startTime = currentTime
        networkManager.send(ServerboundPingRequestPacket(startTime))
    }

    override fun handlePongResponse(packetIn: ClientboundPongResponsePacket) {
        println("Received pong packet from ${server.ip} (${server.name})")
        callback((currentTime - startTime).toInt())
    }

    override fun isAcceptingMessages(): Boolean {
        return networkManager.isConnected
    }
}