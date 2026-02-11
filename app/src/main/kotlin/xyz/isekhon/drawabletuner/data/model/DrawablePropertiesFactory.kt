package xyz.isekhon.drawabletuner.data.model

import android.graphics.drawable.GradientDrawable
import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.toArgb

object DrawablePropertiesFactory {
    
    fun createDefault() = DrawablePropertiesInRoom()
    
    // Create default with dynamic colors from Material 3 theme
    fun createDefaultWithDynamicColors(colorScheme: ColorScheme) = DrawablePropertiesInRoom(
        startColor = colorScheme.primary.toArgb(),
        solidColor = colorScheme.primary.toArgb(),
        strokeColor = colorScheme.outline.toArgb(),
        endColor = (colorScheme.primaryContainer.toArgb() and 0x00FFFFFF) or 0x80000000.toInt() // 50% alpha
    )
    
    fun createRectangleSample() = DrawablePropertiesInRoom(
        shape = GradientDrawable.RECTANGLE,
        topLeftRadius = 60,
        topRightRadius = 60,
        useGradient = true,
        type = GradientDrawable.RADIAL_GRADIENT,
        gradientRadius = 520f,
        centerX = 0.5f,
        centerY = 1.0f,
        strokeWidth = 4,
        dashWidth = 20,
        dashGap = 12
    )
}
