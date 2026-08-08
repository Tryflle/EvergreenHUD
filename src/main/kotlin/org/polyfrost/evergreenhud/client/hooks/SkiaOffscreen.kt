package org.polyfrost.evergreenhud.client.hooks

import com.mojang.blaze3d.pipeline.RenderTarget
import org.jetbrains.skia.BackendRenderTarget
import org.jetbrains.skia.ColorSpace
import org.jetbrains.skia.Surface
import org.jetbrains.skia.SurfaceOrigin
import org.polyfrost.oneconfig.internal.ui.compose.SkiaCtx
import org.polyfrost.oneconfig.internal.ui.services.VulkanService

object SkiaOffscreen {
    private var cached: VulkanService? = null

    private fun service(): VulkanService? {
        cached?.let { return it }
        if (!SkiaCtx.isReady) return null
        val resolved = runCatching {
            SkiaCtx.javaClass.getDeclaredField("vulkanService")
                .apply { isAccessible = true }
                .get(SkiaCtx) as? VulkanService
        }.getOrNull() ?: runCatching { VulkanService.detect() }.getOrNull()
        cached = resolved
        return resolved
    }

    val isVulkan: Boolean get() = SkiaCtx.isVulkanMode

    val needsPerFrameRewrap: Boolean get() = service()?.offscreenNeedsPerFrameRewrap == true

    fun makeSurface(target: RenderTarget, width: Int, height: Int): Pair<BackendRenderTarget, Surface>? {
        val service = service() ?: return null
        val (backend, colorFormat) = service.makeOffscreenBRT(target, width, height)
        val origin = if (SkiaCtx.isDeferredComposeBackend) SurfaceOrigin.TOP_LEFT else SurfaceOrigin.BOTTOM_LEFT
        val surface = Surface.makeFromBackendRenderTarget(
            SkiaCtx.directContext,
            backend,
            origin,
            colorFormat,
            ColorSpace.sRGB,
            null,
        )
        if (surface == null) {
            backend.close()
            return null
        }
        return backend to surface
    }

    fun beginRender(target: RenderTarget) {
        service()?.transitionOffscreenForRendering(target)
    }

    fun endRender(target: RenderTarget) {
        service()?.transitionOffscreenForSampling(target)
    }

    fun resetContext() {
        if (!SkiaCtx.isReady) return
        if (isVulkan) SkiaCtx.directContext.resetAll() else SkiaCtx.directContext.resetGLAll()
    }
}
