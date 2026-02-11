package xyz.isekhon.drawabletuner.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import xyz.isekhon.drawabletuner.data.model.DrawablePropertiesInRoom
import xyz.isekhon.drawabletuner.ui.components.XmlCodeHighlighter
import xyz.isekhon.drawabletuner.utils.ShapeXmlGenerator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun XmlCodeViewScreen(
    properties: DrawablePropertiesInRoom,
    onNavigateBack: () -> Unit
) {
    // Handle predictive back gesture
    BackHandler(onBack = onNavigateBack)
    
    val xmlCode = remember(properties) {
        ShapeXmlGenerator.shapeXmlString(properties).replace("0x", "#")
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("XML Code") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        XmlCodeHighlighter(
            xmlCode = xmlCode,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        )
    }
}
