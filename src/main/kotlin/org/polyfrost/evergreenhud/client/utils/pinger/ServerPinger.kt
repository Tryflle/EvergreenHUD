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
//$$ import net.minecraft.util.profiler.MultiValueDebugSampleLogImpl
//#endif

//#if MC == 1.20.4
//$$ import net.minecraft.util.SampleLogger
//#endif

//#if MC >= 1.17.1
//$$ import net.minecraft.client.network.Address
//$$ import net.minecraft.client.network.AllowedAddressResolver
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

        eventHandler<TickEvent.Start> {
            if (ticks++ % intervalSupplier() == 0) {
                submitTask()
            }
        }
    }

    private fun submitTask() {
        Multithreading.submit {
            val server = serverSupplier() ?: return@submit
            ping(server)
        }
    }

    private fun ping(server: OmniClientServerEntry) {
        println("Pinging server: ${server.address}")

        val address = ServerAddress.fromString(server.address)
        val connection = NetworkManager.createNetworkManagerAndConnect(
            //#if MC >= 1.17.1
            //$$ AllowedAddressResolver.DEFAULT.resolve(address).map { it.inetSocketAddress }.orElseThrow(),
            //#else
            InetAddress.getByName(address.ip),
            address.port,
            //#endif
            false,
            //#if MC >= 1.20.6
            //$$ null as MultiValueDebugSampleLogImpl?,
            //#elseif MC >= 1.20.4
            //$$ null as SampleLogger?,
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
