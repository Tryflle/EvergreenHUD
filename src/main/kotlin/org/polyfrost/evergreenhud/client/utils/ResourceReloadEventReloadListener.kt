package org.polyfrost.evergreenhud.client.utils

import dev.deftu.omnicore.api.identifierOrThrow
import dev.deftu.omnicore.api.resources.SimpleResourceReloadListener
import net.minecraft.client.resources.IResourceManager
import org.polyfrost.evergreenhud.EvergreenHudConstants
import org.polyfrost.evergreenhud.client.ResourceReloadEvent
import org.polyfrost.oneconfig.api.event.v1.EventManager
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor

object ResourceReloadEventReloadListener : SimpleResourceReloadListener<Unit> {
    override val location = identifierOrThrow(EvergreenHudConstants.ID, "resource_reload_event")

    override fun reload(
        resourceManager: IResourceManager,
        executor: Executor
    ): CompletableFuture<Unit> {
        return CompletableFuture.supplyAsync({  }, executor)
    }

    override fun apply(
        data: Unit,
        resourceManager: IResourceManager,
        executor: Executor
    ): CompletableFuture<Void> {
        return CompletableFuture.runAsync({
            EventManager.INSTANCE.post(ResourceReloadEvent)
        }, executor)
    }
}
