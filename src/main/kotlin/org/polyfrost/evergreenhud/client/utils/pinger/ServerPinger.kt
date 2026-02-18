package org.polyfrost.evergreenhud.client.utils.pinger

import dev.deftu.omnicore.api.client.network.OmniServerInfo
import net.minecraft.client.multiplayer.resolver.ServerAddress
import net.minecraft.client.multiplayer.resolver.ServerNameResolver
import org.polyfrost.oneconfig.api.event.v1.eventHandler
import org.polyfrost.oneconfig.api.event.v1.events.TickEvent
import org.polyfrost.oneconfig.utils.v1.Multithreading
import net.minecraft.network.Connection
import net.minecraft.network.protocol.status.ServerboundStatusRequestPacket
import net.minecraft.util.debugchart.LocalSampleLogger
import org.polyfrost.oneconfig.utils.v1.dsl.mc

//? if >= 1.21.11
import net.minecraft.server.network.EventLoopGroupHolder

class ServerPinger(
    private val intervalSupplier: () -> Int,
    private val serverSupplier: () -> OmniServerInfo?
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

    private fun ping(server: OmniServerInfo) {
        println("Pinging server: ${server.address}")

        val address = ServerAddress.parseString(server.address)
        val connection = Connection.connectToServer(
            ServerNameResolver.DEFAULT.resolveAddress(address).map { it.asInetSocketAddress()  }.orElseThrow(),
            //? if >= 1.21.11 {
            EventLoopGroupHolder.remote(mc.options.useNativeTransport()),
            //?} else
            /* false, */
            null as LocalSampleLogger?,
        )

        connection.apply {
            sendHandshake(server, address)
            sendStatusQuery()
        }
    }

    private fun Connection.sendHandshake(server: OmniServerInfo, address: ServerAddress) {
        initiateServerboundStatusConnection(
            address.host,
            address.port,
            PingingPacketListener(server, this) { pingTime ->
                ping = pingTime
            }
        )
    }

    private fun Connection.sendStatusQuery() = send(ServerboundStatusRequestPacket.INSTANCE)

}
