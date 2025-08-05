package org.polyfrost.evergreenhud.client.utils.pinger

import dev.deftu.omnicore.client.OmniClientServerEntry
import net.minecraft.client.multiplayer.ServerAddress
import net.minecraft.network.EnumConnectionState
import net.minecraft.network.NetworkManager
import net.minecraft.network.handshake.client.C00Handshake
import net.minecraft.network.status.client.C00PacketServerQuery
import org.polyfrost.oneconfig.api.event.v1.eventHandler
import org.polyfrost.oneconfig.api.event.v1.events.TickEvent
import org.polyfrost.oneconfig.utils.v1.Multithreading

//#if MC >= 1.20.6
//$$ import net.minecraft.network.NetworkSide
//#endif

//#if MC >= 1.17.1
//$$ import java.net.InetSocketAddress
//#else
import java.net.InetAddress
//#endif

class ServerPinger(
    private val intervalSupplier: () -> Int,
    private val serverSupplier: () -> OmniClientServerEntry?
) {

    private var ticks = 0

    var ping: Int? = null
        private set

    init {
        submitTask() // Populate initial ping

        eventHandler { _: TickEvent.Start ->
            if (ticks++ % intervalSupplier() == 0) {
                submitTask()
            }
        }
    }

    private fun submitTask() {
        Multithreading.submit {
            serverSupplier()?.let(::ping)
        }
    }

    private fun ping(server: OmniClientServerEntry) {
        val address = ServerAddress.fromString(server.address)
        //#if MC >= 1.20.6
        //$$ val connection = ClientConnection(NetworkSide.CLIENTBOUND)
        //$$ ClientConnection.connect(
        //#else
        val connection = NetworkManager.createNetworkManagerAndConnect(
        //#endif
            //#if MC >= 1.17.1
            //$$ InetSocketAddress.createUnresolved(address.address, address.port),
            //#else
            InetAddress.getByName(address.ip),
            address.port,
            //#endif
            false,
            //#if MC >= 1.20.6
            //$$ connection,
            //#elseif MC >= 1.20.4
            //$$ null,
            //#endif
        )

        connection.apply {
            //#if MC < 1.20.4
            netHandler = PingingPacketListener(server, this) { pingTime ->
                ping = pingTime
            }
            //#endif

            sendHandshake(server, address)
            sendStatusQuery()
        }
    }

    private fun NetworkManager.sendHandshake(server: OmniClientServerEntry, address: ServerAddress) {
        //#if MC >= 1.20.4
        //$$ initiateServerboundStatusConnection(
        //$$     address.host,
        //$$     address.port,
        //$$     PingingPacketListener(server, this) { pingTime ->
        //$$         ping = pingTime
        //$$     }
        //$$ )
        //#elseif MC >= 1.12.2
        //$$ sendPacket(C00Handshake(
        //$$     address.ip,
        //$$     address.port,
        //$$     EnumConnectionState.STATUS,
        //#if MC == 1.12.2
        //$$     false,
        //#endif
        //$$ ))
        //#else
        sendPacket(C00Handshake(
            0,
            address.ip,
            address.port,
            EnumConnectionState.STATUS,
        ))
        //#endif
    }

    private fun NetworkManager.sendStatusQuery() {
        //#if MC >= 1.20.6
        //$$ send(QueryRequestC2SPacket.INSTANCE)
        //#else
        sendPacket(C00PacketServerQuery())
        //#endif
    }

}
