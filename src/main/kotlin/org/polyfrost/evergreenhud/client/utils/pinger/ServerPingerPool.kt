package org.polyfrost.evergreenhud.client.utils.pinger

import net.minecraft.client.multiplayer.ServerData
import java.util.Collections

object ServerPingerPool {
    private val pool = Collections.synchronizedList<ServerPinger>(mutableListOf())

    fun createPinger(
        intervalSupplier: () -> Int,
        serverSupplier: () -> ServerData?,
    ): ServerPinger {
        val pinger = ServerPinger(intervalSupplier, serverSupplier)
        pool.add(pinger)
        return pinger
    }
}
