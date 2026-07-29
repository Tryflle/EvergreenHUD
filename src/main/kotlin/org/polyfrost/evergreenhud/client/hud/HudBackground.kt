package org.polyfrost.evergreenhud.client.hud

import org.polyfrost.compose.render.PolyColor

interface HudBackground {
    var background: Boolean
    var backgroundColor: PolyColor
}

val HudBackground.backgroundArgb: Int?
    get() = if (background) backgroundColor.argb else null
