package org.polyfrost.evergreenhud.client.utils.pinger

import dev.deftu.omnicore.client.OmniClientServerEntry
import java.util.Collections

object ServerPingerPool {
    private val pool = Collections.synchronizedList<ServerPinger>(mutableListOf())

    fun createPinger(
        intervalSupplier: () -> Int,
        serverSupplier: () -> OmniClientServerEntry?,
    ): ServerPinger {
        val pinger = ServerPinger(intervalSupplier, serverSupplier)
        pool.add(pinger)
        return pinger
    }
}
