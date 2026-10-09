package com.greader.neo.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke

/**
 * Glifo RSS da marca — desenhado em Canvas (sem dependência de ícones),
 * usado no drawer, estados vazios e login.
 */
@Composable
fun RssGlyph(
    modifier: Modifier = Modifier,
    color: Color = LocalContentColor.current,
) {
    Canvas(modifier = modifier) {
        val s = size.minDimension
        val strokeWidth = s * 0.085f
        val dotX = s * 0.20f
        val dotY = s * 0.80f

        drawCircle(
            color = color,
            radius = s * 0.085f,
            center = Offset(dotX, dotY),
        )

        val style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        val smallRadius = s * 0.27f
        val largeRadius = s * 0.54f

        drawArc(
            color = color,
            startAngle = 270f,
            sweepAngle = 90f,
            useCenter = false,
            topLeft = Offset(dotX - smallRadius, dotY - smallRadius),
            size = Size(smallRadius * 2, smallRadius * 2),
            style = style,
        )
        drawArc(
            color = color,
            startAngle = 270f,
            sweepAngle = 90f,
            useCenter = false,
            topLeft = Offset(dotX - largeRadius, dotY - largeRadius),
            size = Size(largeRadius * 2, largeRadius * 2),
            style = style,
        )
    }
}
