package org.polyfrost.evergreenhud.client.utils

import dev.deftu.omnicore.api.locationOrThrow
import dev.deftu.omnicore.api.resources.SimpleResourceReloadListener
import net.minecraft.server.packs.resources.ResourceManager
import org.polyfrost.evergreenhud.EvergreenHudConstants
import org.polyfrost.evergreenhud.client.ResourceReloadEvent
import org.polyfrost.oneconfig.api.event.v1.EventManager
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor

object ResourceReloadEventReloadListener : SimpleResourceReloadListener<Unit> {
    override val location = locationOrThrow(EvergreenHudConstants.ID, "resource_reload_event")

    override fun reload(
        resourceManager: ResourceManager,
        executor: Executor
    ): CompletableFuture<Unit> {
        return CompletableFuture.supplyAsync({  }, executor)
    }

    override fun apply(
        data: Unit,
        resourceManager: ResourceManager,
        executor: Executor
    ): CompletableFuture<Void> {
        return CompletableFuture.runAsync({
            EventManager.INSTANCE.post(ResourceReloadEvent)
        }, executor)
    }
}
