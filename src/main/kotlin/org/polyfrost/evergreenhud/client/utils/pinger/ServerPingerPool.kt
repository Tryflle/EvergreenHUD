package org.polyfrost.evergreenhud.client.utils.pinger

import dev.deftu.omnicore.api.client.network.OmniServerInfo
import java.util.Collections

object ServerPingerPool {
    private val pool = Collections.synchronizedList<ServerPinger>(mutableListOf())

    fun createPinger(
        intervalSupplier: () -> Int,
        serverSupplier: () -> OmniServerInfo?,
    ): ServerPinger {
        val pinger = ServerPinger(intervalSupplier, serverSupplier)
        pool.add(pinger)
        return pinger
    }
}
