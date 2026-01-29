package xyz.isekhon.drawabletuner.utils

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import org.dom4j.DocumentHelper
import org.dom4j.io.OutputFormat
import org.dom4j.io.XMLWriter
import xyz.isekhon.drawabletuner.data.model.DrawablePropertiesInRoom
import xyz.isekhon.drawabletuner.utils.DrawableProperties.Companion.RADIUS_TYPE_PIXELS
import java.io.StringWriter
import java.util.Locale

object ShapeXmlGenerator {
    
    private var density = 1f
    
    fun init(context: Context) {
        density = context.resources.displayMetrics.density
    }
    
    fun shapeXmlString(properties: DrawablePropertiesInRoom?): String {
        if (properties == null) {
            return "Null properties"
        }
        
        return try {
            val document = DocumentHelper.createDocument()
            val root = document.addElement("shape")
                .addAttribute("xmlns:android", "http://schemas.android.com/apk/res/android")
                .addAttribute("android:shape", nameForShape(properties.shape))
            
            // Ring-specific attributes
            if (properties.shape == GradientDrawable.RING) {
                if (properties.innerRadius != -1) {
                    root.addAttribute("android:innerRadius", stringOf(properties.innerRadius))
                } else if (properties.innerRadiusRatio != 9f) {
                    root.addAttribute("android:innerRadiusRatio", properties.innerRadiusRatio.toString())
                }
                if (properties.thickness != -1) {
                    root.addAttribute("android:thickness", stringOf(properties.thickness))
                } else if (properties.thicknessRatio != 3f) {
                    root.addAttribute("android:thicknessRatio", properties.thicknessRatio.toString())
                }
                root.addAttribute("android:useLevel", "false")
            }
            
            // Corners
            if (hasRadius(properties)) {
                val corners = root.addElement("corners")
                if (isCornerRadiusEven(properties)) {
                    corners.addAttribute("android:radius", stringOf(properties.getCornerRadius()))
                } else {
                    if (properties.topLeftRadius > 0) {
                        corners.addAttribute("android:topLeftRadius", stringOf(properties.topLeftRadius))
                    }
                    if (properties.topRightRadius > 0) {
                        corners.addAttribute("android:topRightRadius", stringOf(properties.topRightRadius))
                    }
                    if (properties.bottomLeftRadius > 0) {
                        corners.addAttribute("android:bottomLeftRadius", stringOf(properties.bottomLeftRadius))
                    }
                    if (properties.bottomRightRadius > 0) {
                        corners.addAttribute("android:bottomRightRadius", stringOf(properties.bottomRightRadius))
                    }
                }
            }
            
            // Gradient
            if (properties.shouldEnableGradient()) {
                val gradient = root.addElement("gradient")
                gradient.addAttribute("android:type", nameForGradientType(properties.type))
                
                if (properties.angle > 0) {
                    gradient.addAttribute("android:angle", properties.angle.toString())
                }
                
                if (properties.type != GradientDrawable.LINEAR_GRADIENT) {
                    if (properties.centerX != 0.5f) {
                        gradient.addAttribute("android:centerX", properties.centerX.toString())
                    }
                    if (properties.centerY != 0.5f) {
                        gradient.addAttribute("android:centerY", properties.centerY.toString())
                    }
                }
                
                if (properties.type == GradientDrawable.RADIAL_GRADIENT) {
                    if (properties.gradientRadiusType == RADIUS_TYPE_PIXELS) {
                        gradient.addAttribute("android:gradientRadius", stringOf(properties.gradientRadius.toInt()))
                    }
                }
                
                gradient.addAttribute("android:startColor", colorHex(properties.startColor))
                gradient.addAttribute("android:endColor", colorHex(properties.endColor))
                
                if (properties.useCenterColor) {
                    gradient.addAttribute("android:centerColor", colorHex(properties.centerColor))
                }
            }
            
            // Size
            if (properties.width > 0 || properties.height > 0) {
                val size = root.addElement("size")
                if (properties.width > 0) {
                    size.addAttribute("android:width", stringOf(properties.width))
                }
                if (properties.height > 0) {
                    size.addAttribute("android:height", stringOf(properties.height))
                }
            }
            
            // Solid color
            if (!properties.shouldEnableGradient()) {
                val solid = root.addElement("solid")
                solid.addAttribute("android:color", colorHex(properties.solidColor))
            }
            
            // Stroke
            if (properties.strokeWidth > 0) {
                val stroke = root.addElement("stroke")
                stroke.addAttribute("android:width", stringOf(properties.strokeWidth))
                stroke.addAttribute("android:color", colorHex(properties.strokeColor))
                
                if (properties.dashWidth > 0 && properties.dashGap > 0) {
                    stroke.addAttribute("android:dashWidth", stringOf(properties.dashWidth))
                    stroke.addAttribute("android:dashGap", stringOf(properties.dashGap))
                }
            }
            
            prettyPrint(document)
        } catch (e: IllegalArgumentException) {
            "Invalid properties"
        }
    }
    
    private fun nameForShape(shape: Int): String = when (shape) {
        GradientDrawable.RECTANGLE -> "rectangle"
        GradientDrawable.OVAL -> "oval"
        GradientDrawable.LINE -> "line"
        GradientDrawable.RING -> "ring"
        else -> throw IllegalArgumentException("Unknown shape: $shape")
    }
    
    private fun nameForGradientType(type: Int): String = when (type) {
        GradientDrawable.LINEAR_GRADIENT -> "linear"
        GradientDrawable.RADIAL_GRADIENT -> "radial"
        GradientDrawable.SWEEP_GRADIENT -> "sweep"
        else -> throw IllegalArgumentException("Unknown gradient type: $type")
    }
    
    private fun hasRadius(properties: DrawablePropertiesInRoom): Boolean {
        return !(properties.getCornerRadius() == 0 && isCornerRadiusEven(properties))
    }
    
    private fun isCornerRadiusEven(properties: DrawablePropertiesInRoom): Boolean {
        val cornerRadius = properties.getCornerRadius()
        return cornerRadius == properties.topLeftRadius &&
                cornerRadius == properties.topRightRadius &&
                cornerRadius == properties.bottomLeftRadius &&
                cornerRadius == properties.bottomRightRadius
    }
    
    private fun stringOf(pixel: Int): String {
        if (pixel == 1) return "1px"
        // Values are already in dp from the UI sliders, no need to divide by density
        return "${pixel}dp"
    }
    
    private fun prettyPrint(document: org.dom4j.Document): String {
        val sw = StringWriter()
        val format = OutputFormat.createPrettyPrint().apply {
            setIndentSize(4)
        }
        val xw = XMLWriter(sw, format)
        xw.write(document)
        return sw.toString()
    }
    
    private fun colorHex(color: Int): String {
        val a = Color.alpha(color)
        val r = Color.red(color)
        val g = Color.green(color)
        val b = Color.blue(color)
        return String.format(Locale.getDefault(), "0x%02X%02X%02X%02X", a, r, g, b)
    }
}
