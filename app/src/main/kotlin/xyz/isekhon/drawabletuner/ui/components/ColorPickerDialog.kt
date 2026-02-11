package xyz.isekhon.drawabletuner.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

@Composable
fun ColorPickerDialog(
    currentColor: Int,
    onDismiss: () -> Unit,
    onColorSelected: (Int) -> Unit
) {
    var selectedColor by remember { mutableIntStateOf(currentColor) }
    var showAdvanced by remember { mutableStateOf(false) }
    var hexInput by remember { mutableStateOf(String.format("%08X", currentColor)) }
    
    // Extract ARGB components
    val alpha = remember(selectedColor) { ((selectedColor shr 24) and 0xFF) / 255f }
    val red = remember(selectedColor) { ((selectedColor shr 16) and 0xFF) / 255f }
    val green = remember(selectedColor) { ((selectedColor shr 8) and 0xFF) / 255f }
    val blue = remember(selectedColor) { (selectedColor and 0xFF) / 255f }
    
    // Convert to HSV
    val hsv = remember(red, green, blue) { rgbToHsv(red, green, blue) }
    var hue by remember { mutableFloatStateOf(hsv[0]) }
    var saturation by remember { mutableFloatStateOf(hsv[1]) }
    var value by remember { mutableFloatStateOf(hsv[2]) }
    var alphaValue by remember { mutableFloatStateOf(alpha) }
    
    // Update color when HSV changes
    LaunchedEffect(hue, saturation, value, alphaValue) {
        val rgb = hsvToRgb(hue, saturation, value)
        selectedColor = argbToInt(alphaValue, rgb[0], rgb[1], rgb[2])
        hexInput = String.format("%08X", selectedColor)
    }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Color Picker",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    
                    // Advanced toggle
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "Advanced",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Switch(
                            checked = showAdvanced,
                            onCheckedChange = { showAdvanced = it }
                        )
                    }
                }
                
                // Color preview with hex input
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Preview box
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(MaterialTheme.shapes.large)
                            .background(Color(selectedColor.toLong()))
                            .border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.outline,
                                shape = MaterialTheme.shapes.large
                            )
                    )
                    
                    // Hex input
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "Hex Color",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        OutlinedTextField(
                            value = hexInput,
                            onValueChange = { newHex ->
                                hexInput = newHex.uppercase().filter { it.isLetterOrDigit() }.take(8)
                                if (hexInput.length == 8 || hexInput.length == 6) {
                                    try {
                                        val fullHex = if (hexInput.length == 6) "FF$hexInput" else hexInput
                                        val color = fullHex.toLong(16).toInt()
                                        selectedColor = color
                                        val a = ((color shr 24) and 0xFF) / 255f
                                        val r = ((color shr 16) and 0xFF) / 255f
                                        val g = ((color shr 8) and 0xFF) / 255f
                                        val b = (color and 0xFF) / 255f
                                        val newHsv = rgbToHsv(r, g, b)
                                        hue = newHsv[0]
                                        saturation = newHsv[1]
                                        value = newHsv[2]
                                        alphaValue = a
                                    } catch (e: Exception) {
                                        // Invalid hex, ignore
                                    }
                                }
                            },
                            prefix = { Text("#") },
                            textStyle = TextStyle(
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                fontSize = 16.sp
                            ),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            "Format: AARRGGBB or RRGGBB",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                // Quick preset colors
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Quick Colors",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    PresetColors(
                        currentColor = selectedColor,
                        onColorSelected = { color ->
                            selectedColor = color
                            hexInput = String.format("%08X", color)
                            val a = ((color shr 24) and 0xFF) / 255f
                            val r = ((color shr 16) and 0xFF) / 255f
                            val g = ((color shr 8) and 0xFF) / 255f
                            val b = (color and 0xFF) / 255f
                            val newHsv = rgbToHsv(r, g, b)
                            hue = newHsv[0]
                            saturation = newHsv[1]
                            value = newHsv[2]
                            alphaValue = a
                        }
                    )
                }
                
                // Advanced controls (collapsible)
                AnimatedVisibility(
                    visible = showAdvanced,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        HorizontalDivider()
                        
                        // Hue slider
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                "Hue",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            HueSlider(
                                hue = hue,
                                onHueChange = { hue = it }
                            )
                        }
                        
                        // Saturation and Value picker
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                "Saturation & Brightness",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            SaturationValuePicker(
                                hue = hue,
                                saturation = saturation,
                                value = value,
                                onSaturationValueChange = { s, v ->
                                    saturation = s
                                    value = v
                                }
                            )
                        }
                        
                        // Alpha slider
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Opacity",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    "${(alphaValue * 100).roundToInt()}%",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            AlphaSlider(
                                alpha = alphaValue,
                                color = selectedColor,
                                onAlphaChange = { alphaValue = it }
                            )
                        }
                        
                        // RGB input fields
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                "RGBA Components",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                ColorComponentInput(
                                    label = "R",
                                    value = ((selectedColor shr 16) and 0xFF),
                                    onValueChange = { r ->
                                        val g = (selectedColor shr 8) and 0xFF
                                        val b = selectedColor and 0xFF
                                        val a = (selectedColor shr 24) and 0xFF
                                        selectedColor = (a shl 24) or (r shl 16) or (g shl 8) or b
                                        hexInput = String.format("%08X", selectedColor)
                                        val newHsv = rgbToHsv(r / 255f, g / 255f, b / 255f)
                                        hue = newHsv[0]
                                        saturation = newHsv[1]
                                        value = newHsv[2]
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                                ColorComponentInput(
                                    label = "G",
                                    value = ((selectedColor shr 8) and 0xFF),
                                    onValueChange = { g ->
                                        val r = (selectedColor shr 16) and 0xFF
                                        val b = selectedColor and 0xFF
                                        val a = (selectedColor shr 24) and 0xFF
                                        selectedColor = (a shl 24) or (r shl 16) or (g shl 8) or b
                                        hexInput = String.format("%08X", selectedColor)
                                        val newHsv = rgbToHsv(r / 255f, g / 255f, b / 255f)
                                        hue = newHsv[0]
                                        saturation = newHsv[1]
                                        value = newHsv[2]
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                                ColorComponentInput(
                                    label = "B",
                                    value = (selectedColor and 0xFF),
                                    onValueChange = { b ->
                                        val r = (selectedColor shr 16) and 0xFF
                                        val g = (selectedColor shr 8) and 0xFF
                                        val a = (selectedColor shr 24) and 0xFF
                                        selectedColor = (a shl 24) or (r shl 16) or (g shl 8) or b
                                        hexInput = String.format("%08X", selectedColor)
                                        val newHsv = rgbToHsv(r / 255f, g / 255f, b / 255f)
                                        hue = newHsv[0]
                                        saturation = newHsv[1]
                                        value = newHsv[2]
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                                ColorComponentInput(
                                    label = "A",
                                    value = ((selectedColor shr 24) and 0xFF),
                                    onValueChange = { a ->
                                        alphaValue = a / 255f
                                        hexInput = String.format("%08X", selectedColor)
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
                
                HorizontalDivider()
                
                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { onColorSelected(selectedColor) }) {
                        Text("Apply")
                    }
                }
            }
        }
    }
}

@Composable
fun HueSlider(
    hue: Float,
    onHueChange: (Float) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .clip(MaterialTheme.shapes.small)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            val newHue = (offset.x / size.width).coerceIn(0f, 1f) * 360f
                            onHueChange(newHue)
                        },
                        onDrag = { change, _ ->
                            val newHue = (change.position.x / size.width).coerceIn(0f, 1f) * 360f
                            onHueChange(newHue)
                        }
                    )
                }
        ) {
            val colors = (0..360 step 10).map { h ->
                Color.hsv(h.toFloat(), 1f, 1f)
            }
            
            drawRect(
                brush = Brush.horizontalGradient(colors),
                size = size
            )
            
            // Indicator
            val x = (hue / 360f) * size.width
            drawCircle(
                color = Color.White,
                radius = 12f,
                center = Offset(x, size.height / 2),
                style = Stroke(width = 3f)
            )
            drawCircle(
                color = Color.Black,
                radius = 12f,
                center = Offset(x, size.height / 2),
                style = Stroke(width = 1f)
            )
        }
    }
}

@Composable
fun SaturationValuePicker(
    hue: Float,
    saturation: Float,
    value: Float,
    onSaturationValueChange: (Float, Float) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.5f)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .clip(MaterialTheme.shapes.small)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            val newSaturation = (offset.x / size.width).coerceIn(0f, 1f)
                            val newValue = 1f - (offset.y / size.height).coerceIn(0f, 1f)
                            onSaturationValueChange(newSaturation, newValue)
                        },
                        onDrag = { change, _ ->
                            val newSaturation = (change.position.x / size.width).coerceIn(0f, 1f)
                            val newValue = 1f - (change.position.y / size.height).coerceIn(0f, 1f)
                            onSaturationValueChange(newSaturation, newValue)
                        }
                    )
                }
        ) {
            // Draw saturation-value gradient
            val baseColor = Color.hsv(hue, 1f, 1f)
            
            // Horizontal gradient (saturation)
            drawRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color.White, baseColor)
                ),
                size = size
            )
            
            // Vertical gradient (value)
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.Transparent, Color.Black)
                ),
                size = size
            )
            
            // Indicator
            val x = saturation * size.width
            val y = (1f - value) * size.height
            drawCircle(
                color = Color.White,
                radius = 10f,
                center = Offset(x, y),
                style = Stroke(width = 3f)
            )
            drawCircle(
                color = Color.Black,
                radius = 10f,
                center = Offset(x, y),
                style = Stroke(width = 1f)
            )
        }
    }
}

@Composable
fun AlphaSlider(
    alpha: Float,
    color: Int,
    onAlphaChange: (Float) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .clip(MaterialTheme.shapes.small)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            val newAlpha = (offset.x / size.width).coerceIn(0f, 1f)
                            onAlphaChange(newAlpha)
                        },
                        onDrag = { change, _ ->
                            val newAlpha = (change.position.x / size.width).coerceIn(0f, 1f)
                            onAlphaChange(newAlpha)
                        }
                    )
                }
        ) {
            // Checkerboard background
            val checkSize = 8.dp.toPx()
            var y = 0f
            while (y < size.height) {
                var x = 0f
                var useDark = (y / checkSize).toInt() % 2 == 0
                while (x < size.width) {
                    drawRect(
                        color = if (useDark) Color(0xFFCCCCCC) else Color.White,
                        topLeft = Offset(x, y),
                        size = Size(checkSize, checkSize)
                    )
                    x += checkSize
                    useDark = !useDark
                }
                y += checkSize
            }
            
            // Alpha gradient
            val baseColor = Color(color or 0xFF000000.toInt())
            drawRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color.Transparent, baseColor)
                ),
                size = size
            )
            
            // Indicator
            val x = alpha * size.width
            drawCircle(
                color = Color.White,
                radius = 12f,
                center = Offset(x, size.height / 2),
                style = Stroke(width = 3f)
            )
            drawCircle(
                color = Color.Black,
                radius = 12f,
                center = Offset(x, size.height / 2),
                style = Stroke(width = 1f)
            )
        }
    }
}

@Composable
fun ColorComponentInput(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var text by remember(value) { mutableStateOf(value.toString()) }
    
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        OutlinedTextField(
            value = text,
            onValueChange = { newText ->
                text = newText
                newText.toIntOrNull()?.let { intValue ->
                    if (intValue in 0..255) {
                        onValueChange(intValue)
                    }
                }
            },
            textStyle = TextStyle(
                textAlign = TextAlign.Center,
                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.width(60.dp)
        )
    }
}

@Composable
fun PresetColors(
    currentColor: Int,
    onColorSelected: (Int) -> Unit
) {
    val predefinedColors = remember {
        listOf(
            0xFFFF0000.toInt(), 0xFF00FF00.toInt(), 0xFF0000FF.toInt(),
            0xFFFFFF00.toInt(), 0xFFFF00FF.toInt(), 0xFF00FFFF.toInt(),
            0xFFFF8800.toInt(), 0xFF8800FF.toInt(), 0xFF00FF88.toInt(),
            0xFF000000.toInt(), 0xFFFFFFFF.toInt(), 0xFF808080.toInt(),
            0xFF2DCFCA.toInt(), 0xFF24A5A1.toInt(), 0x00000000, 0x80FFFFFF.toInt()
        )
    }
    
    LazyVerticalGrid(
        columns = GridCells.Fixed(8),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.height(90.dp)
    ) {
        items(predefinedColors.size) { index ->
            val color = predefinedColors[index]
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(color.toLong()))
                    .border(
                        width = if (color == currentColor) 3.dp else 1.dp,
                        color = if (color == currentColor) 
                            MaterialTheme.colorScheme.primary 
                        else 
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                        shape = CircleShape
                    )
                    .clickable { onColorSelected(color) }
            )
        }
    }
}

// HSV/RGB conversion functions
private fun rgbToHsv(r: Float, g: Float, b: Float): FloatArray {
    val max = max(r, max(g, b))
    val min = min(r, min(g, b))
    val delta = max - min
    
    val hue = when {
        delta == 0f -> 0f
        max == r -> 60f * (((g - b) / delta) % 6)
        max == g -> 60f * (((b - r) / delta) + 2)
        else -> 60f * (((r - g) / delta) + 4)
    }.let { if (it < 0) it + 360f else it }
    
    val saturation = if (max == 0f) 0f else delta / max
    val value = max
    
    return floatArrayOf(hue, saturation, value)
}

private fun hsvToRgb(h: Float, s: Float, v: Float): FloatArray {
    val c = v * s
    val x = c * (1 - kotlin.math.abs((h / 60f) % 2 - 1))
    val m = v - c
    
    val (r, g, b) = when {
        h < 60 -> Triple(c, x, 0f)
        h < 120 -> Triple(x, c, 0f)
        h < 180 -> Triple(0f, c, x)
        h < 240 -> Triple(0f, x, c)
        h < 300 -> Triple(x, 0f, c)
        else -> Triple(c, 0f, x)
    }
    
    return floatArrayOf(r + m, g + m, b + m)
}

private fun argbToInt(a: Float, r: Float, g: Float, b: Float): Int {
    val alpha = (a * 255).roundToInt().coerceIn(0, 255)
    val red = (r * 255).roundToInt().coerceIn(0, 255)
    val green = (g * 255).roundToInt().coerceIn(0, 255)
    val blue = (b * 255).roundToInt().coerceIn(0, 255)
    return (alpha shl 24) or (red shl 16) or (green shl 8) or blue
}
