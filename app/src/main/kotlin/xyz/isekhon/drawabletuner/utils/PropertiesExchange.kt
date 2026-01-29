package xyz.isekhon.drawabletuner.utils

import xyz.isekhon.drawabletuner.data.model.DrawablePropertiesInRoom

object PropertiesExchange {
    
    fun fromRoom(roomProperties: DrawablePropertiesInRoom): DrawableProperties {
        return DrawableProperties().apply {
            shape = roomProperties.shape
            innerRadius = roomProperties.innerRadius
            innerRadiusRatio = roomProperties.innerRadiusRatio
            thickness = roomProperties.thickness
            thicknessRatio = roomProperties.thicknessRatio
            
            topLeftRadius = roomProperties.topLeftRadius
            topRightRadius = roomProperties.topRightRadius
            bottomLeftRadius = roomProperties.bottomLeftRadius
            bottomRightRadius = roomProperties.bottomRightRadius
            
            useGradient = roomProperties.useGradient
            if (roomProperties.useGradient) {
                gradientType = roomProperties.type
                angle = roomProperties.angle
                centerX = roomProperties.centerX
                centerY = roomProperties.centerY
                startColor = roomProperties.startColor
                endColor = roomProperties.endColor
                
                if (roomProperties.useCenterColor) {
                    centerColor = roomProperties.centerColor
                }
                
                gradientRadiusType = roomProperties.gradientRadiusType
                gradientRadius = roomProperties.gradientRadius
            }
            
            width = roomProperties.width
            height = roomProperties.height
            solidColor = roomProperties.solidColor
            strokeWidth = roomProperties.strokeWidth
            strokeColor = roomProperties.strokeColor
            dashWidth = roomProperties.dashWidth
            dashGap = roomProperties.dashGap
        }
    }
}
