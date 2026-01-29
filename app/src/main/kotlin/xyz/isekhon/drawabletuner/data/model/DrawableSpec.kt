package xyz.isekhon.drawabletuner.data.model

import kotlinx.serialization.Serializable

@Serializable
data class DrawableSpec(
    var id: Int = 0,
    var name: String = "Temp",
    var properties: DrawablePropertiesInRoom = DrawablePropertiesInRoom()
) {
    companion object {
        fun createTemp() = DrawableSpec(
            id = 0,
            name = "Temp",
            properties = DrawablePropertiesInRoom()
        )
        
        fun createRectangleSample(name: String) = DrawableSpec(
            id = 0,
            name = name,
            properties = DrawablePropertiesFactory.createRectangleSample()
        )
    }
}
