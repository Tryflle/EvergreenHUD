package org.polyfrost.evergreenhud.client.hooks

import com.mojang.blaze3d.pipeline.TextureTarget
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.Minecraft
//? if < 26
//import net.minecraft.client.gui.GuiGraphics
//? if >= 26
import net.minecraft.client.gui.GuiGraphicsExtractor as GuiGraphics
//? if >= 1.21.8 {
import org.polyfrost.oneconfig.internal.mixin.render.GameRendererAccessor
import org.polyfrost.oneconfig.internal.mixin.render.GuiRendererAccessor
//? }
//? if >= 26.1 {
import net.minecraft.client.renderer.state.gui.GuiRenderState
//? } else if >= 1.21.8 {
/*import net.minecraft.client.gui.render.state.GuiRenderState
*///? }
import org.jetbrains.skia.BackendRenderTarget
import org.jetbrains.skia.Canvas
import org.jetbrains.skia.ColorSpace
import org.jetbrains.skia.ContentChangeMode
import org.jetbrains.skia.FramebufferFormat
import org.jetbrains.skia.Paint
import org.jetbrains.skia.Surface
import org.jetbrains.skia.SurfaceColorFormat
import org.jetbrains.skia.SurfaceOrigin
import org.polyfrost.oneconfig.api.event.v1.eventHandler
import org.polyfrost.oneconfig.api.event.v1.events.ResizeEvent
import org.polyfrost.oneconfig.api.platform.v1.Platform
import org.polyfrost.oneconfig.internal.ui.RenderTargetFbo
import org.polyfrost.oneconfig.internal.ui.compose.SkiaCtx
import org.polyfrost.oneconfig.internal.ui.hud.GuiTargetRedirect
import org.polyfrost.oneconfig.utils.v1.dsl.mc
import org.slf4j.LoggerFactory
//? if < 1.21.8
//import org.joml.Matrix4f
//? if >= 1.21.4 && < 1.21.8
//import com.mojang.blaze3d.ProjectionType

/**
 * Draws Minecraft native HUD content into an offscreen target once per frame because those
 * renderers need a real render pass which no longer runs while a HUD is drawn
 */
object HudOffscreen {
    private val LOGGER = LoggerFactory.getLogger("EvergreenHUD/Hud Offscreen")

    private val client: Minecraft get() = mc
    private val blitPaint = Paint()

    private var target: TextureTarget? = null
    private var brt: BackendRenderTarget? = null
    private var surface: Surface? = null
    private var lastWidth = -1
    private var lastHeight = -1

    private val pending = ArrayList<(GuiGraphics) -> Unit>()
    private var hasContent = false
    private var failed = false

    /** Pixels per gui unit */
    var surfaceRatio = 1f
        private set

    private var loggedFirstFrame = false
    private var loggedNotReady = false

    fun initialize() {
        eventHandler { _: ResizeEvent -> invalidate() }
    }

    /** Queues [draw] for this frame offscreen pass in gui coordinates */
    fun submit(draw: (GuiGraphics) -> Unit) {
        pending.add(draw)
    }

    /** Must run inside the vanilla HUD render pass or there is no frame to hang off and nothing draws */
    @JvmStatic
    fun render() {
        val requests = if (pending.isEmpty()) emptyList() else ArrayList(pending)
        pending.clear()
        hasContent = false
        if (requests.isEmpty() || failed) return
        if (!SkiaCtx.isReady) {
            if (!loggedNotReady) {
                loggedNotReady = true
                LOGGER.warn("Skia is not ready; offscreen HUDs cannot be drawn yet")
            }
            return
        }

        val width = Platform.screen().viewportWidth()
        val height = Platform.screen().viewportHeight()
        val guiWidth = Platform.screen().guiWidth()
        val guiHeight = Platform.screen().guiHeight()
        if (width <= 0 || height <= 0 || guiWidth <= 0 || guiHeight <= 0) return
        // the same ratio OneConfig blits its own offscreen targets with so the two line up
        surfaceRatio = Platform.screen().surfaceRatio().coerceAtLeast(0.0001f)

        try {
            if (!resolveTarget(width, height)) return
            val rt = target ?: return
            clearTarget(rt)
            drawAll(rt, requests, guiWidth, guiHeight)
            // Minecraft just drew through raw GL so skia cached state no longer matches the driver
            SkiaCtx.directContext.resetGLAll()
            hasContent = true
            if (!loggedFirstFrame) {
                loggedFirstFrame = true
                LOGGER.info("Offscreen HUD target ready ({}x{} px, {} request(s))", width, height, requests.size)
            }
        } catch (throwable: Throwable) {
            LOGGER.warn("Offscreen HUD render failed; disabling", throwable)
            failed = true
            GuiTargetRedirect.target = null
            invalidate()
        }
    }

    /** Blits the last rendered frame into [canvas] which is already placed at the HUD origin */
    fun drawInto(canvas: Canvas, hudX: Float, hudY: Float, hudScale: Float, width: Float, height: Float) {
        if (!hasContent) return
        val s = surface ?: return
        val scale = 1f / (surfaceRatio * hudScale).coerceAtLeast(0.0001f)
        try {
            s.notifyContentWillChange(ContentChangeMode.RETAIN)
            canvas.save()
            canvas.clipRect(org.jetbrains.skia.Rect.makeXYWH(0f, 0f, width, height))
            // the target holds the whole screen so shift the HUD own patch onto the origin
            canvas.translate(-hudX / hudScale, -hudY / hudScale)
            canvas.scale(scale, scale)
            s.draw(canvas, 0, 0, blitPaint)
            canvas.restore()
        } catch (throwable: Throwable) {
            LOGGER.warn("Offscreen HUD blit failed", throwable)
        }
    }

    private fun resolveTarget(width: Int, height: Int): Boolean {
        if (target != null && surface != null && lastWidth == width && lastHeight == height) return true
        invalidate()

        val rt = createTarget(width, height)
        target = rt
        val fboId = fboId(rt)
        if (fboId <= 0) {
            // a Vulkan backend has no GL framebuffer to wrap and OneConfig keeps the wrapper internal
            LOGGER.warn("Player preview needs an OpenGL render target; disabling")
            failed = true
            invalidate()
            return false
        }

        val backend = BackendRenderTarget.makeGL(width, height, 0, 8, fboId, FramebufferFormat.GR_GL_RGBA8)
        brt = backend
        val origin = if (SkiaCtx.isDeferredComposeBackend) SurfaceOrigin.TOP_LEFT else SurfaceOrigin.BOTTOM_LEFT
        val made = Surface.makeFromBackendRenderTarget(
            SkiaCtx.directContext,
            backend,
            origin,
            SurfaceColorFormat.RGBA_8888,
            ColorSpace.sRGB,
            null,
        )
        if (made == null) {
            invalidate()
            return false
        }
        surface = made
        lastWidth = width
        lastHeight = height
        return true
    }

    private fun fboId(rt: TextureTarget): Int {
        //? if >= 1.21.5 {
        return RenderTargetFbo.getFboId(rt)
        //? } else {
        /*return rt.frameBufferId
        *///? }
    }

    private fun createTarget(width: Int, height: Int): TextureTarget {
        //? if >= 26.2 {
        return TextureTarget(null, width, height, true, com.mojang.blaze3d.GpuFormat.RGBA8_UNORM)
        //? } else if >= 1.21.5 {
        /*return TextureTarget(null, width, height, true)
        *///? } else if >= 1.21.4 {
        /*return TextureTarget(width, height, true).also { it.setClearColor(0f, 0f, 0f, 0f) }
        *///? } else {
        /*return TextureTarget(width, height, true, Minecraft.ON_OSX).also { it.setClearColor(0f, 0f, 0f, 0f) }
        *///? }
    }

    private fun clearTarget(rt: TextureTarget) {
        //? if >= 26.2 {
        val encoder = RenderSystem.getDevice().createCommandEncoder()
        rt.colorTexture?.let { encoder.clearColorTexture(it, org.joml.Vector4f(0f, 0f, 0f, 0f)) }
        rt.depthTexture?.let { encoder.clearDepthTexture(it, 1.0) }
        //? } else if >= 1.21.5 {
        /*val encoder = RenderSystem.getDevice().createCommandEncoder()
        rt.colorTexture?.let { encoder.clearColorTexture(it, 0) }
        rt.depthTexture?.let { encoder.clearDepthTexture(it, 1.0) }
        *///? } else if >= 1.21.4 {
        /*rt.clear()
        *///? } else {
        /*rt.clear(Minecraft.ON_OSX)
        *///? }
    }

    private fun drawAll(
        rt: TextureTarget,
        requests: List<(GuiGraphics) -> Unit>,
        guiWidth: Int,
        guiHeight: Int,
    ) {
        //? if >= 1.21.8 {
        val state = GuiRenderState()
        //? if >= 1.21.11 {
        val graphics = GuiGraphics(client, state, guiWidth, guiHeight)
        //? } else
        //val graphics = GuiGraphics(client, state)
        requests.forEach { it(graphics) }

        // must be the game own renderer as only it holds the picture in picture renderers
        // a private renderer silently skips entity and player face states
        val guiRenderer = (client.gameRenderer as GameRendererAccessor).`oneconfig$getGuiRenderer`()
        val accessor = guiRenderer as GuiRendererAccessor
        val previousState = accessor.`oneconfig$getRenderState`()
        val previousTarget = GuiTargetRedirect.target
        GuiTargetRedirect.target = rt
        try {
            accessor.`oneconfig$setRenderState`(state)
            //? if >= 26.2 {
            guiRenderer.render()
            //? } else {
            /*val fog = (client.gameRenderer as GameRendererAccessor)
                .`oneconfig$getFogRenderer`()
                .getBuffer(net.minecraft.client.renderer.fog.FogRenderer.FogMode.NONE)
            guiRenderer.render(fog)
            *///? }
        } finally {
            GuiTargetRedirect.target = previousTarget
            // shared renderer so restore its state and never close it
            accessor.`oneconfig$setRenderState`(previousState)
        }
        //? } else if >= 1.21.5 {
        /*val graphics = GuiGraphics(client, client.renderBuffers().bufferSource())
        val previousTarget = GuiTargetRedirect.target
        withGuiProjection(guiWidth, guiHeight) {
            GuiTargetRedirect.target = rt
            try {
                requests.forEach { it(graphics) }
                graphics.flush()
            } finally {
                GuiTargetRedirect.target = previousTarget
            }
        }
        *///? } else {
        /*val graphics = GuiGraphics(client, client.renderBuffers().bufferSource())
        val previousTarget = GuiTargetRedirect.target
        withGuiProjection(guiWidth, guiHeight) {
            GuiTargetRedirect.target = rt
            rt.bindWrite(true)
            try {
                requests.forEach { it(graphics) }
                graphics.flush()
            } finally {
                GuiTargetRedirect.target = previousTarget
                (previousTarget ?: client.mainRenderTarget).bindWrite(true)
            }
        }
        *///? }
    }

    //? if >= 1.21.4 && < 1.21.8 {
    /*private inline fun withGuiProjection(guiWidth: Int, guiHeight: Int, render: () -> Unit) {
        RenderSystem.backupProjectionMatrix()
        val modelView = RenderSystem.getModelViewStack()
        modelView.pushMatrix()
        try {
            val projection = Matrix4f().setOrtho(0f, guiWidth.toFloat(), guiHeight.toFloat(), 0f, 1000f, 21000f)
            RenderSystem.setProjectionMatrix(projection, ProjectionType.ORTHOGRAPHIC)
            modelView.translation(0f, 0f, -11000f)
            render()
        } finally {
            modelView.popMatrix()
            RenderSystem.restoreProjectionMatrix()
        }
    }
    *///? }

    //? if < 1.21.4 {
    /*private inline fun withGuiProjection(guiWidth: Int, guiHeight: Int, render: () -> Unit) {
        RenderSystem.backupProjectionMatrix()
        val modelView = RenderSystem.getModelViewStack()
        modelView.pushMatrix()
        try {
            val projection = Matrix4f().setOrtho(0f, guiWidth.toFloat(), guiHeight.toFloat(), 0f, 1000f, 21000f)
            RenderSystem.setProjectionMatrix(projection, com.mojang.blaze3d.vertex.VertexSorting.ORTHOGRAPHIC_Z)
            modelView.translation(0f, 0f, -11000f)
            RenderSystem.applyModelViewMatrix()
            render()
        } finally {
            modelView.popMatrix()
            RenderSystem.applyModelViewMatrix()
            RenderSystem.restoreProjectionMatrix()
        }
    }
    *///? }

    fun transformed(graphics: GuiGraphics, x: Float, y: Float, scale: Float, draw: () -> Unit) {
        //? if >= 1.21.8 {
        val pose = graphics.pose()
        pose.pushMatrix()
        pose.translate(x, y)
        if (scale != 1f) pose.scale(scale, scale)
        try {
            draw()
        } finally {
            pose.popMatrix()
        }
        //? } else {
        /*val pose = graphics.pose()
        pose.pushPose()
        pose.translate(x.toDouble(), y.toDouble(), 0.0)
        if (scale != 1f) pose.scale(scale, scale, 1f)
        try {
            draw()
        } finally {
            pose.popPose()
        }
        *///? }
    }

    private fun invalidate() {
        surface?.close()
        surface = null
        brt?.close()
        brt = null
        target?.destroyBuffers()
        target = null
        lastWidth = -1
        lastHeight = -1
        hasContent = false
    }
}
