package com.perplxdai

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.sin

enum class OrbMode(val speed: Float, val pulse: Float) {
    Idle(1f, 0.02f),
    Listening(1.6f, 0.035f),
    Thinking(3f, 0.05f),
    Speaking(2.2f, 0.06f)
}

// Design space matches the approved mock: a 380 x 395 canvas, orb 370 square at x=5, pad centred at (190, 337).
private const val DESIGN_ORB = 370f
private const val DESIGN_W = 380f
private const val DESIGN_H = 395f

/** The Perplxd logo: rings rotating clockwise, a fixed wordmark on top, and a glowing platform beneath. */
@Composable
fun PerplxdOrb(mode: OrbMode, modifier: Modifier = Modifier, orbSize: Dp = 300.dp) {
    val rings = ImageBitmap.imageResource(R.drawable.perplxd_rings)
    val speed by animateFloatAsState(mode.speed, tween(700), label = "orb-speed")
    val pulseAmount by animateFloatAsState(mode.pulse, tween(700), label = "orb-pulse")
    val currentSpeed by rememberUpdatedState(speed)
    var rotation by remember { mutableFloatStateOf(0f) }
    var time by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) {
        var last = 0L
        while (true) {
            androidx.compose.runtime.withFrameNanos { now ->
                if (last != 0L) {
                    val dt = (now - last) / 1_000_000_000f
                    rotation = (rotation + dt * 12f * currentSpeed) % 360f
                    time += dt
                }
                last = now
            }
        }
    }

    val u = orbSize.value / DESIGN_ORB
    val width = (DESIGN_W * u).dp
    val height = (DESIGN_H * u).dp
    Box(modifier = modifier.size(width, height), contentAlignment = Alignment.TopStart) {
        Canvas(modifier = Modifier.size(width, height)) {
            val k = size.width / DESIGN_W
            drawPlatform(k, time, 1f + pulseAmount * sin(time * 1.5f))
            drawRings(rings, rotation, k)
        }
        Box(modifier = Modifier.size(width, (DESIGN_ORB * u).dp), contentAlignment = Alignment.Center) {
            Text(
                text = "P.E.R.P.L.X.D",
                color = Color.White,
                fontSize = (25f * u).sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                textAlign = TextAlign.Center,
                style = TextStyle(
                    letterSpacing = (7f * u).sp,
                    shadow = Shadow(Color(0xFF000014), blurRadius = 18f * u)
                )
            )
        }
    }
}

private fun DrawScope.drawRings(image: ImageBitmap, degrees: Float, k: Float) {
    val side = (DESIGN_ORB * k).toInt()
    val left = (5f * k).toInt()
    val c = Offset(left + side / 2f, side / 2f)
    val imageClip = Path().apply {
        addOval(Rect(left.toFloat(), 0f, (left + side).toFloat(), side.toFloat()))
    }
    drawContext.canvas.saveLayer(Rect(Offset.Zero, size), Paint())
    clipPath(imageClip) {
        rotate(degrees, pivot = c) {
            drawImage(image, dstOffset = IntOffset(left, 0), dstSize = IntSize(side, side))
        }
    }
    // Radial fade hides the image's square edges.
    drawRect(
        brush = Brush.radialGradient(
            0.5f to Color.Black,
            0.68f to Color.Transparent,
            center = c,
            radius = side * 0.7071f
        ),
        topLeft = Offset(left.toFloat(), 0f),
        size = Size(side.toFloat(), side.toFloat()),
        blendMode = BlendMode.DstIn
    )
    drawContext.canvas.restore()
}

private fun DrawScope.ellipseStroke(
    cx: Float, cy: Float, rx: Float, ry: Float, k: Float,
    color: Color, width: Float, effect: PathEffect? = null
) {
    drawOval(
        color = color,
        topLeft = Offset((cx - rx) * k, (cy - ry) * k),
        size = Size(rx * 2 * k, ry * 2 * k),
        style = Stroke(width = width * k, pathEffect = effect)
    )
}

private fun DrawScope.drawPlatform(k: Float, t: Float, pulse: Float) {
    val cx = 190f
    val cy = 337f
    val pivot = Offset(cx * k, cy * k)

    scale(1f, 0.2f, pivot) {
        drawCircle(
            brush = Brush.radialGradient(
                listOf(Color(0xB30A46FF), Color.Transparent),
                center = pivot,
                radius = 175f * k
            ),
            radius = 175f * k,
            center = pivot
        )
    }

    val beamAlpha = 0.25f + 0.1f * sin(t * 1.57f)
    val beam = Path().apply {
        moveTo(70f * k, cy * k)
        lineTo(110f * k, 190f * k)
        lineTo(270f * k, 190f * k)
        lineTo(310f * k, cy * k)
        close()
    }
    drawPath(
        beam,
        Brush.verticalGradient(
            listOf(Color.Transparent, Color(0xFF1F6BFF).copy(alpha = beamAlpha)),
            startY = 190f * k, endY = cy * k
        )
    )

    for (offset in listOf(0f, 2f)) {
        val p = ((t + offset) % 4f) / 4f
        val s = 0.55f + 0.7f * p
        ellipseStroke(cx, cy, 150f * s, 27f * s, k, Color(0xFF2FD0FF).copy(alpha = 0.8f * (1f - p)), 1.5f)
    }

    scale(pulse, pulse, pivot) {
        ellipseStroke(cx, cy, 178f, 33f, k, Color(0xFF0A3CFF).copy(alpha = 0.3f), 1f)
        ellipseStroke(
            cx, cy, 168f, 31f, k, Color(0xFF25B8FF).copy(alpha = 0.9f), 2f,
            PathEffect.dashPathEffect(floatArrayOf(3f * k, 12f * k), t / 6f * 120f * k)
        )
        ellipseStroke(cx, cy, 145f, 26f, k, Color(0xFF2FD0FF).copy(alpha = 0.2f), 11f)
        ellipseStroke(cx, cy, 145f, 26f, k, Color(0xFF2FD0FF).copy(alpha = 0.35f), 6f)
        ellipseStroke(cx, cy, 145f, 26f, k, Color(0xFF1F6BFF).copy(alpha = 0.95f), 2.5f)
        ellipseStroke(
            cx, cy, 125f, 22f, k, Color(0xFF5FE8FF).copy(alpha = 0.85f), 2f,
            PathEffect.dashPathEffect(floatArrayOf(26f * k, 10f * k), -t / 9f * 120f * k)
        )
        ellipseStroke(cx, cy, 105f, 18f, k, Color(0xFF1A7DFF).copy(alpha = 0.6f), 1f)
        drawOval(
            color = Color(0xFF0A46FF).copy(alpha = 0.25f),
            topLeft = Offset((cx - 60f) * k, (cy - 10f) * k),
            size = Size(120f * k, 20f * k)
        )
        ellipseStroke(cx, cy, 60f, 10f, k, Color(0xFF5FE8FF).copy(alpha = 0.5f), 1f)
    }

    val sparks = listOf(
        Triple(110f, 343f, 0f), Triple(165f, 355f, 0.8f), Triple(230f, 353f, 1.6f),
        Triple(275f, 339f, 2.3f), Triple(195f, 333f, 1.2f)
    )
    for ((sx, sy, delay) in sparks) {
        val p = ((t + delay) % 3.2f) / 3.2f
        val alpha = if (p < 0.15f) p / 0.15f else 1f - (p - 0.15f) / 0.85f
        drawCircle(
            Color(0xFF8FF0FF).copy(alpha = alpha.coerceIn(0f, 1f)),
            1.6f * k,
            Offset(sx * k, (sy - 70f * p) * k)
        )
    }
}
