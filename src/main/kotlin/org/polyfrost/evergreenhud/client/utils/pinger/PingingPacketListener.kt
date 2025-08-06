package org.polyfrost.evergreenhud.client.utils.pinger

import dev.deftu.omnicore.client.OmniClient
import dev.deftu.omnicore.client.OmniClientServerEntry
import dev.deftu.textile.minecraft.MCSimpleTextHolder
import dev.deftu.textile.minecraft.MCTextFormat
import dev.deftu.textile.minecraft.MCTextHolder
import net.minecraft.network.NetworkManager
import net.minecraft.network.status.INetHandlerStatusClient
import net.minecraft.network.status.client.C01PacketPing
import net.minecraft.network.status.server.S00PacketServerInfo
import net.minecraft.network.status.server.S01PacketPong

//#if MC >= 1.21.1
//$$ import net.minecraft.network.DisconnectionDetails
//#else
import net.minecraft.util.IChatComponent
//#endif

//#if MC >= 1.16.5
//$$ import net.minecraft.Util
//#endif

class PingingPacketListener(
    private val server: OmniClientServerEntry,
    private val networkManager: NetworkManager,
    private val callback: (Int) -> Unit
) : INetHandlerStatusClient {

    private var started = false
    private var startTime = -1L

    private val currentTime: Long
        get() {
            //#if MC >= 1.16.5
            //$$ return Util.getMillis()
            //#else
            return OmniClient.getTimeSinceStart()
            //#endif
        }

    override fun onDisconnect(
        //#if MC >= 1.21.1
        //$$ details: DisconnectionDetails,
        //#else
        reason: IChatComponent
        //#endif
    ) {
        //#if MC >= 1.21.1
        //$$ val reason = details.reason
        //#endif
        println("Disconnected from server ${server.address} (${server.name}) with reason: ${MCTextHolder.convertFromVanilla(reason).asUnformattedString()}")
        if (!started) {
            throw IllegalStateException("Could not query server ${server.address} (${server.name}) for ping status. Reason: ${MCTextHolder.convertFromVanilla(reason).asUnformattedString()}")
        }
    }

    override fun handleServerInfo(packetIn: S00PacketServerInfo) {
        if (started) {
            networkManager.closeChannel(
                MCSimpleTextHolder("Received unrequested status packet")
                    .withFormatting(MCTextFormat.RED)
                    .asVanilla()
            )

            return
        }

        println("Received server info packet from ${server.address} (${server.name})")
        started = true
        startTime = currentTime
        networkManager.sendPacket(C01PacketPing(startTime))
    }

    override fun handlePong(packetIn: S01PacketPong) {
        println("Received pong packet from ${server.address} (${server.name})")
        callback((currentTime - startTime).toInt())
    }

    //#if MC >= 1.19.4
    //$$ override fun isAcceptingMessages(): Boolean {
    //$$     return networkManager.isConnected
    //$$ }
    //#elseif MC >= 1.16.5
    //$$ override fun getConnection(): Connection {
    //$$     return networkManager
    //$$ }
    //#endif

}
