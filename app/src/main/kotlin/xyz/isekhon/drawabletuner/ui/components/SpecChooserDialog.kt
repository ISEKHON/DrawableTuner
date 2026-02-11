package xyz.isekhon.drawabletuner.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import xyz.isekhon.drawabletuner.data.model.DrawableSpec
import xyz.isekhon.drawabletuner.utils.DrawableBuilder
import xyz.isekhon.drawabletuner.utils.PropertiesExchange

@Composable
fun SpecChooserDialog(
    specs: List<DrawableSpec>,
    onDismiss: () -> Unit,
    onSelect: (DrawableSpec) -> Unit
) {
    val context = LocalContext.current
    
    Dialog(onDismissRequest = onDismiss) {
        Card {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .height(400.dp)
            ) {
                Text(
                    "Select Spec",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                if (specs.isEmpty()) {
                    Text(
                        "No saved specs yet",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            items = specs,
                            key = { it.name }
                        ) { spec ->
                            val drawable = remember(spec) {
                                DrawableBuilder(context.resources.displayMetrics.density)
                                    .batch(PropertiesExchange.fromRoom(spec.properties))
                                    .build()
                            }
                            
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onSelect(spec) }
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // Preview box with checkerboard background
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .aspectRatio(1f),
                                        shape = MaterialTheme.shapes.small,
                                        tonalElevation = 2.dp
                                    ) {
                                        Box {
                                            // Checkerboard background
                                            val lightColor = MaterialTheme.colorScheme.surfaceContainerHighest
                                            val darkColor = MaterialTheme.colorScheme.surfaceContainerHigh
                                            Canvas(modifier = Modifier.fillMaxSize()) {
                                                drawMiniCheckerboard(lightColor, darkColor)
                                            }
                                            
                                            // Drawable preview
                                            Canvas(modifier = Modifier.fillMaxSize()) {
                                                val canvas = drawContext.canvas.nativeCanvas
                                                val sizePx = size.width.toInt()
                                                drawable.setBounds(0, 0, sizePx, sizePx)
                                                drawable.draw(canvas)
                                            }
                                        }
                                    }
                                    
                                    // Spec name
                                    Text(
                                        spec.name,
                                        style = MaterialTheme.typography.bodyMedium,
                                        maxLines = 2,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                }
            }
        }
    }
}

private fun DrawScope.drawMiniCheckerboard(lightColor: Color, darkColor: Color) {
    val checkSize = 8.dp.toPx()
    
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
