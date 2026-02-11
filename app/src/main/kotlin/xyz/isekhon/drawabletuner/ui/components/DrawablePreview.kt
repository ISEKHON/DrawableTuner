package xyz.isekhon.drawabletuner.ui.components

import android.graphics.drawable.Drawable
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import xyz.isekhon.drawabletuner.data.model.DrawablePropertiesInRoom

@Composable
fun DrawablePreview(
    drawable: Drawable?,
    properties: DrawablePropertiesInRoom,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Checkerboard background - fills entire preview area
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer() // Force layer for better performance
        ) {
            drawCheckerboard()
        }
        
        // Drawable preview - centered
        drawable?.let {
            val width = properties.width.dp
            val height = properties.height.dp
            
            Canvas(
                modifier = Modifier
                    .size(width, height)
                    .graphicsLayer() // Force layer for better performance
            ) {
                val canvas = drawContext.canvas.nativeCanvas
                // Convert dp to pixels for drawable bounds
                val widthPx = size.width.toInt()
                val heightPx = size.height.toInt()
                it.setBounds(0, 0, widthPx, heightPx)
                it.draw(canvas)
            }
        }
    }
}

private fun DrawScope.drawCheckerboard() {
    val checkSize = 16.dp.toPx()
    val lightColor = Color.White
    val darkColor = Color(0xFFE0E0E0)
    
    var y = 0f
    while (y < size.height) {
        var x = 0f
        var useDark = (y / checkSize).toInt() % 2 == 0
        while (x < size.width) {
            drawRect(
                color = if (useDark) darkColor else lightColor,
                topLeft = Offset(x, y),
                size = Size(checkSize, checkSize)
            )
            x += checkSize
            useDark = !useDark
        }
        y += checkSize
    }
}
