package xyz.isekhon.drawabletuner.utils

import android.content.res.Resources
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.annotation.RequiresApi

/**
 * Builder class for creating GradientDrawable instances
 * Custom implementation to replace external drawabletoolbox library
 */
class DrawableBuilder(private val density: Float = Resources.getSystem().displayMetrics.density) {
    
    private var shape: Int = GradientDrawable.RECTANGLE
    private var innerRadius: Int = -1
    private var innerRadiusRatio: Float = 9f
    private var thickness: Int = -1
    private var thicknessRatio: Float = 3f
    
    private var topLeftRadius: Int = 0
    private var topRightRadius: Int = 0
    private var bottomLeftRadius: Int = 0
    private var bottomRightRadius: Int = 0
    
    private var useGradient: Boolean = false
    private var gradientType: Int = GradientDrawable.RADIAL_GRADIENT
    private var angle: Int = 0
    private var centerX: Float = 0.5f
    private var centerY: Float = 0.5f
    private var startColor: Int = 0xFF2DCFCA.toInt()
    private var centerColor: Int? = null
    private var endColor: Int = 0x7FFFFFFF
    private var gradientRadius: Float = 200f
    
    private var width: Int = 400
    private var height: Int = 400
    private var solidColor: Int = 0xFF2DCFCA.toInt()
    
    private var strokeWidth: Int = 0
    private var strokeColor: Int = 0xFF24A5A1.toInt()
    private var dashWidth: Int = 0
    private var dashGap: Int = 0
    
    fun shape(shape: Int) = apply { this.shape = shape }
    fun innerRadius(innerRadius: Int) = apply { this.innerRadius = innerRadius }
    fun innerRadiusRatio(innerRadiusRatio: Float) = apply { this.innerRadiusRatio = innerRadiusRatio }
    fun thickness(thickness: Int) = apply { this.thickness = thickness }
    fun thicknessRatio(thicknessRatio: Float) = apply { this.thicknessRatio = thicknessRatio }
    
    fun cornerRadius(radius: Int) = apply {
        this.topLeftRadius = radius
        this.topRightRadius = radius
        this.bottomLeftRadius = radius
        this.bottomRightRadius = radius
    }
    
    fun topLeftRadius(radius: Int) = apply { this.topLeftRadius = radius }
    fun topRightRadius(radius: Int) = apply { this.topRightRadius = radius }
    fun bottomLeftRadius(radius: Int) = apply { this.bottomLeftRadius = radius }
    fun bottomRightRadius(radius: Int) = apply { this.bottomRightRadius = radius }
    
    fun useGradient(useGradient: Boolean) = apply { this.useGradient = useGradient }
    fun gradientType(gradientType: Int) = apply { this.gradientType = gradientType }
    fun angle(angle: Int) = apply { this.angle = angle }
    fun centerX(centerX: Float) = apply { this.centerX = centerX }
    fun centerY(centerY: Float) = apply { this.centerY = centerY }
    fun startColor(startColor: Int) = apply { this.startColor = startColor }
    fun centerColor(centerColor: Int?) = apply { this.centerColor = centerColor }
    fun endColor(endColor: Int) = apply { this.endColor = endColor }
    fun gradientRadius(gradientRadius: Float) = apply { this.gradientRadius = gradientRadius }
    
    fun width(width: Int) = apply { this.width = width }
    fun height(height: Int) = apply { this.height = height }
    fun solidColor(solidColor: Int) = apply { this.solidColor = solidColor }
    
    fun strokeWidth(strokeWidth: Int) = apply { this.strokeWidth = strokeWidth }
    fun strokeColor(strokeColor: Int) = apply { this.strokeColor = strokeColor }
    fun dashWidth(dashWidth: Int) = apply { this.dashWidth = dashWidth }
    fun dashGap(dashGap: Int) = apply { this.dashGap = dashGap }
    
    fun batch(properties: DrawableProperties) = apply {
        shape(properties.shape)
        innerRadius(properties.innerRadius)
        innerRadiusRatio(properties.innerRadiusRatio)
        thickness(properties.thickness)
        thicknessRatio(properties.thicknessRatio)
        
        topLeftRadius(properties.topLeftRadius)
        topRightRadius(properties.topRightRadius)
        bottomLeftRadius(properties.bottomLeftRadius)
        bottomRightRadius(properties.bottomRightRadius)
        
        useGradient(properties.useGradient)
        if (properties.useGradient) {
            gradientType(properties.gradientType)
            angle(properties.angle)
            centerX(properties.centerX)
            centerY(properties.centerY)
            startColor(properties.startColor)
            endColor(properties.endColor)
            centerColor(properties.centerColor)
            gradientRadius(properties.gradientRadius)
        }
        
        width(properties.width)
        height(properties.height)
        solidColor(properties.solidColor)
        strokeWidth(properties.strokeWidth)
        strokeColor(properties.strokeColor)
        dashWidth(properties.dashWidth)
        dashGap(properties.dashGap)
    }
    
    @RequiresApi(Build.VERSION_CODES.Q)
    fun build(): Drawable {
        val drawable = GradientDrawable()
        
        // Set shape first
        drawable.shape = shape
        
        // Ring-specific properties - must be set before other properties
        if (shape == GradientDrawable.RING) {
            drawable.useLevel = false
            drawable.innerRadiusRatio = innerRadiusRatio
            drawable.thicknessRatio = thicknessRatio
        }
        
        // Corner radii - only for rectangles
        if (shape == GradientDrawable.RECTANGLE && 
            (topLeftRadius > 0 || topRightRadius > 0 || bottomLeftRadius > 0 || bottomRightRadius > 0)) {
            drawable.cornerRadii = floatArrayOf(
                dpToPx(topLeftRadius).toFloat(), dpToPx(topLeftRadius).toFloat(),
                dpToPx(topRightRadius).toFloat(), dpToPx(topRightRadius).toFloat(),
                dpToPx(bottomRightRadius).toFloat(), dpToPx(bottomRightRadius).toFloat(),
                dpToPx(bottomLeftRadius).toFloat(), dpToPx(bottomLeftRadius).toFloat()
            )
        }
        
        // Gradient or solid color (LINE shape doesn't support fills, only strokes)
        if (useGradient && shape != GradientDrawable.LINE) {
            val colors = if (centerColor != null) {
                intArrayOf(startColor, centerColor!!, endColor)
            } else {
                intArrayOf(startColor, endColor)
            }
            
            val gradientDrawable = GradientDrawable(
                when (gradientType) {
                    GradientDrawable.LINEAR_GRADIENT -> GradientDrawable.Orientation.LEFT_RIGHT
                    else -> GradientDrawable.Orientation.TL_BR
                },
                colors
            )
            
            gradientDrawable.shape = shape
            gradientDrawable.gradientType = gradientType
            
            // Ring-specific properties for gradient drawable
            if (shape == GradientDrawable.RING) {
                gradientDrawable.useLevel = false
                gradientDrawable.innerRadiusRatio = innerRadiusRatio
                gradientDrawable.thicknessRatio = thicknessRatio
            }
            
            if (gradientType == GradientDrawable.LINEAR_GRADIENT) {
                gradientDrawable.orientation = getOrientation(angle)
            }
            
            if (gradientType == GradientDrawable.RADIAL_GRADIENT) {
                gradientDrawable.gradientRadius = dpToPx(gradientRadius)
            }
            
            gradientDrawable.setGradientCenter(centerX, centerY)
            
            // Copy corner radii to gradient drawable (only for rectangles)
            if (shape == GradientDrawable.RECTANGLE && 
                (topLeftRadius > 0 || topRightRadius > 0 || bottomLeftRadius > 0 || bottomRightRadius > 0)) {
                gradientDrawable.cornerRadii = floatArrayOf(
                    dpToPx(topLeftRadius).toFloat(), dpToPx(topLeftRadius).toFloat(),
                    dpToPx(topRightRadius).toFloat(), dpToPx(topRightRadius).toFloat(),
                    dpToPx(bottomRightRadius).toFloat(), dpToPx(bottomRightRadius).toFloat(),
                    dpToPx(bottomLeftRadius).toFloat(), dpToPx(bottomLeftRadius).toFloat()
                )
            }
            
            // Set stroke - convert dp to pixels
            if (strokeWidth > 0) {
                if (dashWidth > 0 && dashGap > 0) {
                    gradientDrawable.setStroke(dpToPx(strokeWidth), strokeColor, dpToPx(dashWidth).toFloat(), dpToPx(dashGap).toFloat())
                } else {
                    gradientDrawable.setStroke(dpToPx(strokeWidth), strokeColor)
                }
            }
            
            gradientDrawable.setSize(dpToPx(width), dpToPx(height))
            return gradientDrawable
        } else if (shape != GradientDrawable.LINE) {
            // Set solid color for all shapes except LINE
            drawable.setColor(solidColor)
        }
        
        // For LINE shape, ensure there's always a stroke (lines are only visible with stroke)
        if (shape == GradientDrawable.LINE) {
            val lineStrokeWidth = if (strokeWidth > 0) strokeWidth else 5 // Default to 5dp if no stroke
            val lineStrokeColor = if (strokeWidth > 0) strokeColor else solidColor
            drawable.setStroke(dpToPx(lineStrokeWidth), lineStrokeColor)
        } else {
            // Set stroke for other shapes if specified
            if (strokeWidth > 0) {
                if (dashWidth > 0 && dashGap > 0) {
                    drawable.setStroke(dpToPx(strokeWidth), strokeColor, dpToPx(dashWidth).toFloat(), dpToPx(dashGap).toFloat())
                } else {
                    drawable.setStroke(dpToPx(strokeWidth), strokeColor)
                }
            }
        }
        
        // Set size - convert dp to pixels
        drawable.setSize(dpToPx(width), dpToPx(height))
        
        return drawable
    }
    
    private fun dpToPx(dp: Int): Int {
        return (dp * density + 0.5f).toInt()
    }
    
    private fun dpToPx(dp: Float): Float {
        return dp * density
    }
    
    private fun getOrientation(angle: Int): GradientDrawable.Orientation {
        return when (angle) {
            0 -> GradientDrawable.Orientation.LEFT_RIGHT
            45 -> GradientDrawable.Orientation.BL_TR
            90 -> GradientDrawable.Orientation.BOTTOM_TOP
            135 -> GradientDrawable.Orientation.BR_TL
            180 -> GradientDrawable.Orientation.RIGHT_LEFT
            225 -> GradientDrawable.Orientation.TR_BL
            270 -> GradientDrawable.Orientation.TOP_BOTTOM
            315 -> GradientDrawable.Orientation.TL_BR
            else -> GradientDrawable.Orientation.LEFT_RIGHT
        }
    }
}

/**
 * Data class for drawable properties
 * Custom implementation to replace external drawabletoolbox library
 */
data class DrawableProperties(
    var shape: Int = GradientDrawable.RECTANGLE,
    var innerRadius: Int = -1,
    var innerRadiusRatio: Float = 9f,
    var thickness: Int = -1,
    var thicknessRatio: Float = 3f,
    
    var topLeftRadius: Int = 0,
    var topRightRadius: Int = 0,
    var bottomLeftRadius: Int = 0,
    var bottomRightRadius: Int = 0,
    
    var useGradient: Boolean = false,
    var gradientType: Int = GradientDrawable.RADIAL_GRADIENT,
    var angle: Int = 0,
    var centerX: Float = 0.5f,
    var centerY: Float = 0.5f,
    var startColor: Int = 0xFF2DCFCA.toInt(),
    var centerColor: Int? = null,
    var endColor: Int = 0x7FFFFFFF,
    var gradientRadiusType: Int = RADIUS_TYPE_PIXELS,
    var gradientRadius: Float = 200f,
    
    var width: Int = 150,
    var height: Int = 150,
    var solidColor: Int = 0xFF2DCFCA.toInt(),
    
    var strokeWidth: Int = 0,
    var strokeColor: Int = 0xFF24A5A1.toInt(),
    var dashWidth: Int = 0,
    var dashGap: Int = 0
) {
    companion object {
        const val RADIUS_TYPE_PIXELS = 0
        const val RADIUS_TYPE_FRACTION = 1
    }
}
