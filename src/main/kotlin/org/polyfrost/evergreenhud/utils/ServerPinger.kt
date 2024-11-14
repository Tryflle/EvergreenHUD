package org.polyfrost.evergreenhud.utils

import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ServerAddress
import net.minecraft.client.multiplayer.ServerData
import net.minecraft.network.EnumConnectionState
import net.minecraft.network.NetworkManager
import net.minecraft.network.handshake.client.C00Handshake
import net.minecraft.network.status.INetHandlerStatusClient
import net.minecraft.network.status.client.C00PacketServerQuery
import net.minecraft.network.status.client.C01PacketPing
import net.minecraft.network.status.server.S00PacketServerInfo
import net.minecraft.network.status.server.S01PacketPong
import net.minecraft.util.ChatComponentText
import net.minecraft.util.IChatComponent
import org.polyfrost.oneconfig.api.event.v1.eventHandler
import org.polyfrost.oneconfig.api.event.v1.events.TickEvent
import org.polyfrost.oneconfig.utils.v1.Multithreading
import java.net.InetAddress
import java.util.Collections

object ServerPinger {
    val pingers = Collections.synchronizedList(mutableListOf<Pinger>())

    fun createListener(interval: () -> Int, serverGetter: () -> ServerData?): Pinger {
        val pinger = Pinger(interval, serverGetter)
        pingers.add(pinger)
        return pinger
    }

    class Pinger(private val interval: () -> Int, private val serverGetter: () -> ServerData?) {
        var ping: Int? = null
            private set

        private var ticks = 0

        init {
            Multithreading.submit {
                serverGetter()?.let(this::ping)
            }
            eventHandler { event: TickEvent.Start ->
                ticks++
                if (ticks % interval() == 0) {
                    Multithreading.submit {
                        serverGetter()?.let(this::ping)
                    }
                }
            }
        }

        private fun ping(server: ServerData) {
            val serverAddress = ServerAddress.fromString(server.serverIP)
            val networkmanager = NetworkManager.createNetworkManagerAndConnect(
                InetAddress.getByName(serverAddress.ip), serverAddress.port, false
            )

            networkmanager.netHandler = object : INetHandlerStatusClient {
                private var startTime = -1L
                private var queried = false
                private var received = false

                override fun onDisconnect(reason: IChatComponent) {
                    if (!queried) {
                        error("Failed to query server: ${reason.unformattedText ?: "null"}")
                    }
                }

                override fun handleServerInfo(packetIn: S00PacketServerInfo) {
                    if (received) {
                        networkmanager.closeChannel(ChatComponentText("Received unrequested status"))
                        return
                    }
                    received = true
                    startTime = Minecraft.getSystemTime()
                    networkmanager.sendPacket(C01PacketPing(startTime))
                    queried = true
                }

                override fun handlePong(packetIn: S01PacketPong) {
                    ping = (Minecraft.getSystemTime() - startTime).toInt()
                }
            }

            networkmanager.sendPacket(
                C00Handshake(
                    //#if MC<11200
                    47,
                    //#endif
                    serverAddress.ip,
                    serverAddress.port,
                    EnumConnectionState.STATUS
                )
            )
            networkmanager.sendPacket(C00PacketServerQuery())
        }
    }
}