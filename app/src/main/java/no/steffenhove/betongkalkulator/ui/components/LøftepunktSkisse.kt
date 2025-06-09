package no.steffenhove.betongkalkulator.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun LøftepunktSkisse(form: String, antallFester: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .padding(top = 12.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            when (form.lowercase()) {
                "firkant" -> drawFirkant(this, antallFester)
                "kjerne" -> drawKjerne(this)
                "trekant" -> drawTrekant(this)
                "trapes" -> drawTrapes(this)
            }
        }
    }
}

private fun drawFirkant(drawScope: DrawScope, antallFester: Int) = with(drawScope) {
    val width = size.width
    val height = size.height

    drawRect(
        color = Color.Gray,
        topLeft = Offset(0f, 0f),
        size = size,
        style = Stroke(4f)
    )

    if (antallFester == 4) {
        val x1 = width * 0.25f
        val x2 = width * 0.75f
        val y1 = height * 0.25f
        val y2 = height * 0.75f
        drawX(x1, y1)
        drawX(x2, y1)
        drawX(x1, y2)
        drawX(x2, y2)
    } else if (antallFester == 1) {
        drawX(width / 2, height / 2)
    }
}

private fun drawKjerne(drawScope: DrawScope) = with(drawScope) {
    val center = Offset(size.width / 2, size.height / 2)
    drawCircle(
        color = Color.Gray,
        radius = size.minDimension / 2.2f,
        center = center,
        style = Stroke(4f)
    )
    drawX(center.x, center.y)
}

private fun drawTrekant(drawScope: DrawScope) = with(drawScope) {
    val width = size.width
    val height = size.height

    val p1 = Offset(width / 2, 0f)
    val p2 = Offset(0f, height)
    val p3 = Offset(width, height)

    drawLine(Color.Gray, p1, p2, 4f)
    drawLine(Color.Gray, p2, p3, 4f)
    drawLine(Color.Gray, p3, p1, 4f)

    val tyngdepunktX = width / 2
    val tyngdepunktY = height * 2 / 3
    drawX(tyngdepunktX, tyngdepunktY)
}

private fun drawTrapes(drawScope: DrawScope) = with(drawScope) {
    val width = size.width
    val height = size.height

    val topWidth = width * 0.6f
    val bottomWidth = width

    val topLeft = Offset((width - topWidth) / 2, 0f)
    val topRight = Offset((width + topWidth) / 2, 0f)
    val bottomLeft = Offset(0f, height)
    val bottomRight = Offset(width, height)

    drawLine(Color.Gray, topLeft, topRight, 4f)
    drawLine(Color.Gray, topLeft, bottomLeft, 4f)
    drawLine(Color.Gray, topRight, bottomRight, 4f)
    drawLine(Color.Gray, bottomLeft, bottomRight, 4f)

    val x = width / 2
    val y = height * 0.55f
    drawX(x, y)
}

private fun DrawScope.drawX(x: Float, y: Float) {
    val size = 12f
    drawLine(Color.Red, Offset(x - size, y - size), Offset(x + size, y + size), strokeWidth = 3f)
    drawLine(Color.Red, Offset(x - size, y + size), Offset(x + size, y - size), strokeWidth = 3f)
}
