package xyz.isekhon.drawabletuner.data.model

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import xyz.isekhon.drawabletuner.utils.DrawableProperties

@Parcelize
@Serializable
data class DrawablePropertiesInRoom(
    var shape: Int = GradientDrawable.RECTANGLE,
    
    // Ring properties
    var innerRadius: Int = -1, // when innerRadius == -1, innerRadiusRatio becomes effective
    var innerRadiusRatio: Float = 9f,
    var thickness: Int = -1, // when thickness == -1, thicknessRatio becomes effective
    var thicknessRatio: Float = 3f,
    
    // Corner radius
    private var cornerRadius: Int = 0,
    var topLeftRadius: Int = 0,
    var topRightRadius: Int = 0,
    var bottomLeftRadius: Int = 0,
    var bottomRightRadius: Int = 0,
    
    // Gradient properties
    var type: Int = GradientDrawable.RADIAL_GRADIENT,
    var useGradient: Boolean = false,
    var useCenterColor: Boolean = true,
    var angle: Int = 0,
    var centerX: Float = 0.5f,
    var centerY: Float = 0.5f,
    var startColor: Int = 0xFF6750A4.toInt(), // Material 3 Primary
    var centerColor: Int = Color.TRANSPARENT,
    var endColor: Int = 0x80D0BCFF.toInt(), // Material 3 Primary Container with alpha
    
    var gradientRadiusType: Int = DrawableProperties.RADIUS_TYPE_PIXELS,
    var gradientRadius: Float = 200f,
    
    // Size properties
    var width: Int = 150,
    var height: Int = 150,
    
    // Solid color
    var solidColor: Int = 0xFF6750A4.toInt(), // Material 3 Primary
    
    // Stroke properties
    var strokeWidth: Int = 0,
    var strokeColor: Int = 0xFF625B71.toInt(), // Material 3 Outline
    var dashWidth: Int = 0,
    var dashGap: Int = 0
) : Parcelable {
    
    fun getCornerRadius(): Int = cornerRadius
    
    fun setCornerRadius(value: Int) {
        cornerRadius = value
        topLeftRadius = value
        topRightRadius = value
        bottomLeftRadius = value
        bottomRightRadius = value
    }
    
    fun shouldEnableGradient(): Boolean = useGradient && shape != GradientDrawable.LINE
    
    fun shouldEnableCenterColor(): Boolean = useCenterColor && shouldEnableGradient()
    
    fun shouldEnableGradientRadius(): Boolean = 
        type == GradientDrawable.RADIAL_GRADIENT && shouldEnableGradient()
    
    fun copy(): DrawablePropertiesInRoom = this.copy(
        shape = shape,
        innerRadius = innerRadius,
        innerRadiusRatio = innerRadiusRatio,
        thickness = thickness,
        thicknessRatio = thicknessRatio,
        cornerRadius = cornerRadius,
        topLeftRadius = topLeftRadius,
        topRightRadius = topRightRadius,
        bottomLeftRadius = bottomLeftRadius,
        bottomRightRadius = bottomRightRadius,
        type = type,
        useGradient = useGradient,
        useCenterColor = useCenterColor,
        angle = angle,
        centerX = centerX,
        centerY = centerY,
        startColor = startColor,
        centerColor = centerColor,
        endColor = endColor,
        gradientRadiusType = gradientRadiusType,
        gradientRadius = gradientRadius,
        width = width,
        height = height,
        solidColor = solidColor,
        strokeWidth = strokeWidth,
        strokeColor = strokeColor,
        dashWidth = dashWidth,
        dashGap = dashGap
    )
    
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DrawablePropertiesInRoom) return false
        
        return shape == other.shape &&
                innerRadius == other.innerRadius &&
                innerRadiusRatio == other.innerRadiusRatio &&
                thickness == other.thickness &&
                thicknessRatio == other.thicknessRatio &&
                cornerRadius == other.cornerRadius &&
                topLeftRadius == other.topLeftRadius &&
                topRightRadius == other.topRightRadius &&
                bottomLeftRadius == other.bottomLeftRadius &&
                bottomRightRadius == other.bottomRightRadius &&
                type == other.type &&
                useGradient == other.useGradient &&
                useCenterColor == other.useCenterColor &&
                angle == other.angle &&
                centerX == other.centerX &&
                centerY == other.centerY &&
                startColor == other.startColor &&
                centerColor == other.centerColor &&
                endColor == other.endColor &&
                gradientRadiusType == other.gradientRadiusType &&
                gradientRadius == other.gradientRadius &&
                width == other.width &&
                height == other.height &&
                solidColor == other.solidColor &&
                strokeWidth == other.strokeWidth &&
                strokeColor == other.strokeColor &&
                dashWidth == other.dashWidth &&
                dashGap == other.dashGap
    }
    
    override fun hashCode(): Int {
        var result = shape
        result = 31 * result + innerRadius
        result = 31 * result + innerRadiusRatio.hashCode()
        result = 31 * result + thickness
        result = 31 * result + thicknessRatio.hashCode()
        result = 31 * result + cornerRadius
        result = 31 * result + topLeftRadius
        result = 31 * result + topRightRadius
        result = 31 * result + bottomLeftRadius
        result = 31 * result + bottomRightRadius
        result = 31 * result + type
        result = 31 * result + useGradient.hashCode()
        result = 31 * result + useCenterColor.hashCode()
        result = 31 * result + angle
        result = 31 * result + centerX.hashCode()
        result = 31 * result + centerY.hashCode()
        result = 31 * result + startColor
        result = 31 * result + centerColor
        result = 31 * result + endColor
        result = 31 * result + gradientRadiusType
        result = 31 * result + gradientRadius.hashCode()
        result = 31 * result + width
        result = 31 * result + height
        result = 31 * result + solidColor
        result = 31 * result + strokeWidth
        result = 31 * result + strokeColor
        result = 31 * result + dashWidth
        result = 31 * result + dashGap
        return result
    }
}
