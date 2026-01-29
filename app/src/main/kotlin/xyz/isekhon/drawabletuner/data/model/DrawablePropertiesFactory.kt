package xyz.isekhon.drawabletuner.data.model

import android.graphics.drawable.GradientDrawable

object DrawablePropertiesFactory {
    
    fun createDefault() = DrawablePropertiesInRoom()
    
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
