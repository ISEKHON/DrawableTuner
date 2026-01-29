package xyz.isekhon.drawabletuner.ui.screen

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import io.github.rosemoe.sora.widget.CodeEditor
import xyz.isekhon.drawabletuner.code.CodeEditorColorSchemes
import xyz.isekhon.drawabletuner.code.CodeEditorLanguages
import xyz.isekhon.drawabletuner.data.model.DrawablePropertiesInRoom
import xyz.isekhon.drawabletuner.utils.ShapeXmlGenerator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun XmlCodeViewScreen(
    properties: DrawablePropertiesInRoom,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
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
        AndroidView(
            factory = { ctx ->
                CodeEditor(ctx).apply {
                    isEditable = false
                    setPadding(16, 16, 16, 16)
                    
                    // Set up the editor with XML language and theme
                    try {
                        val language = CodeEditorLanguages
                            .loadTextMateLanguage(CodeEditorLanguages.SCOPE_NAME_XML)
                        setEditorLanguage(language)
                        
                        val isDarkTheme = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                            ctx.resources.configuration.isNightModeActive
                        } else {
                            false
                        }
                        
                        val theme = if (isDarkTheme) {
                            CodeEditorColorSchemes.THEME_DRACULA
                        } else {
                            CodeEditorColorSchemes.THEME_GITHUB
                        }
                        
                        val colorScheme = CodeEditorColorSchemes
                            .loadTextMateColorScheme(theme)
                        setColorScheme(colorScheme)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            },
            update = { editor ->
                editor.setText(xmlCode)
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        )
    }
}
