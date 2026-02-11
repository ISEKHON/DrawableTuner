package xyz.isekhon.drawabletuner.ui.components

import android.graphics.drawable.GradientDrawable
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import xyz.isekhon.drawabletuner.data.model.DrawablePropertiesInRoom

@Composable
fun PropertyControls(
    properties: DrawablePropertiesInRoom,
    onPropertyChange: (String, Any) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Basic Properties Card
        PropertySection(title = "Basic", initiallyExpanded = true) {
            ShapeSelector(
                currentShape = properties.shape,
                onShapeChange = { onPropertyChange("shape", it) }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            SliderControl(
                label = "Width",
                value = properties.width.toFloat(),
                onValueChange = { onPropertyChange("width", it.toInt()) },
                valueRange = 50f..250f
            )
            
            SliderControl(
                label = "Height",
                value = properties.height.toFloat(),
                onValueChange = { onPropertyChange("height", it.toInt()) },
                valueRange = 50f..250f
            )
            
            AnimatedVisibility(
                visible = properties.shape == GradientDrawable.RECTANGLE,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                val maxCornerRadius = kotlin.math.min(properties.width, properties.height) / 2f
                SliderControl(
                    label = "Corner Radius",
                    value = properties.getCornerRadius().toFloat(),
                    onValueChange = { onPropertyChange("cornerRadius", it.toInt()) },
                    valueRange = 0f..maxCornerRadius
                )
            }
        }
        
        // Fill/Gradient Card
        PropertySection(title = "Fill & Gradient", initiallyExpanded = true) {
            SwitchControl(
                label = "Use Gradient",
                checked = properties.useGradient,
                onCheckedChange = { onPropertyChange("useGradient", it) }
            )
            
            AnimatedVisibility(
                visible = properties.shouldEnableGradient(),
                enter = fadeIn(animationSpec = tween(300)) + expandVertically(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)),
                exit = fadeOut(animationSpec = tween(200)) + shrinkVertically()
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    GradientTypeSelector(
                        currentType = properties.type,
                        onTypeChange = { onPropertyChange("type", it) }
                    )
                    
                    ColorPickerControl(
                        label = "Start Color",
                        color = properties.startColor,
                        onColorChange = { onPropertyChange("startColor", it) }
                    )
                    
                    ColorPickerControl(
                        label = "End Color",
                        color = properties.endColor,
                        onColorChange = { onPropertyChange("endColor", it) }
                    )
                    
                    SwitchControl(
                        label = "Use Center Color",
                        checked = properties.useCenterColor,
                        onCheckedChange = { onPropertyChange("useCenterColor", it) }
                    )
                    
                    AnimatedVisibility(
                        visible = properties.shouldEnableCenterColor(),
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        ColorPickerControl(
                            label = "Center Color",
                            color = properties.centerColor,
                            onColorChange = { onPropertyChange("centerColor", it) }
                        )
                    }
                    
                    if (properties.type == GradientDrawable.LINEAR_GRADIENT) {
                        SliderControl(
                            label = "Angle",
                            value = properties.angle.toFloat(),
                            onValueChange = { onPropertyChange("angle", it.toInt()) },
                            valueRange = 0f..360f,
                            steps = 7
                        )
                    }
                    
                    if (properties.shouldEnableGradientRadius()) {
                        SliderControl(
                            label = "Gradient Radius",
                            value = properties.gradientRadius,
                            onValueChange = { onPropertyChange("gradientRadius", it) },
                            valueRange = 50f..1000f
                        )
                    }
                    
                    if (properties.type == GradientDrawable.RADIAL_GRADIENT || properties.type == GradientDrawable.SWEEP_GRADIENT) {
                        SliderControl(
                            label = "Center X",
                            value = properties.centerX,
                            onValueChange = { onPropertyChange("centerX", it) },
                            valueRange = 0f..1f,
                            steps = 99
                        )
                        
                        SliderControl(
                            label = "Center Y",
                            value = properties.centerY,
                            onValueChange = { onPropertyChange("centerY", it) },
                            valueRange = 0f..1f,
                            steps = 99
                        )
                    }
                }
            }
            
            AnimatedVisibility(
                visible = !properties.shouldEnableGradient(),
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                ColorPickerControl(
                    label = "Solid Color",
                    color = properties.solidColor,
                    onColorChange = { onPropertyChange("solidColor", it) }
                )
            }
        }
        
        // Stroke Card
        PropertySection(title = "Stroke", initiallyExpanded = false) {
            SliderControl(
                label = "Stroke Width",
                value = properties.strokeWidth.toFloat(),
                onValueChange = { onPropertyChange("strokeWidth", it.toInt()) },
                valueRange = 0f..50f
            )
            
            AnimatedVisibility(
                visible = properties.strokeWidth > 0,
                enter = fadeIn(animationSpec = tween(300)) + expandVertically(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)),
                exit = fadeOut(animationSpec = tween(200)) + shrinkVertically()
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    ColorPickerControl(
                        label = "Stroke Color",
                        color = properties.strokeColor,
                        onColorChange = { onPropertyChange("strokeColor", it) }
                    )
                    
                    SliderControl(
                        label = "Dash Width",
                        value = properties.dashWidth.toFloat(),
                        onValueChange = { onPropertyChange("dashWidth", it.toInt()) },
                        valueRange = 0f..100f
                    )
                    
                    AnimatedVisibility(
                        visible = properties.dashWidth > 0,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        SliderControl(
                            label = "Dash Gap",
                            value = properties.dashGap.toFloat(),
                            onValueChange = { onPropertyChange("dashGap", it.toInt()) },
                            valueRange = 0f..100f
                        )
                    }
                }
            }
        }
        
        // Add spacer to ensure content is always scrollable
        Spacer(modifier = Modifier.height(200.dp))
    }
}

@Composable
fun PropertySection(
    title: String,
    icon: String = "",
    initiallyExpanded: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    var expanded by remember { mutableStateOf(initiallyExpanded) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
            .graphicsLayer {
                // Use graphicsLayer for better performance
                shadowElevation = if (expanded) 4.dp.toPx() else 1.dp.toPx()
            },
        colors = CardDefaults.cardColors(
            //containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(),
                color = androidx.compose.ui.graphics.Color.Transparent,
                onClick = { expanded = !expanded }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (icon.isNotEmpty()) {
                            Text(
                                text = icon,
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ExpandMore,
                        contentDescription = if (expanded) "Collapse" else "Expand",
                        modifier = Modifier.graphicsLayer {
                            rotationZ = if (expanded) 180f else 0f
                        },
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn(animationSpec = tween(300)) + 
                        expandVertically(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)),
                exit = fadeOut(animationSpec = tween(200)) + 
                       shrinkVertically(animationSpec = tween(200))
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    content()
                }
            }
        }
    }
}

@Composable
fun ShapeSelector(
    currentShape: Int,
    onShapeChange: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Shape", style = MaterialTheme.typography.labelLarge)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ShapeChip("Rectangle", GradientDrawable.RECTANGLE, currentShape, onShapeChange, Modifier.weight(1f))
            ShapeChip("Oval", GradientDrawable.OVAL, currentShape, onShapeChange, Modifier.weight(1f))
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ShapeChip("Line", GradientDrawable.LINE, currentShape, onShapeChange, Modifier.weight(1f))
            ShapeChip("Ring", GradientDrawable.RING, currentShape, onShapeChange, Modifier.weight(1f))
        }
    }
}

@Composable
fun ShapeChip(
    label: String,
    value: Int,
    currentValue: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val isSelected = currentValue == value
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )
    
    FilterChip(
        selected = isSelected,
        onClick = { onSelect(value) },
        label = { 
            Text(
                label, 
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.labelMedium
            ) 
        },
        modifier = modifier.scale(scale),
        border = if (isSelected) {
            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        } else {
            FilterChipDefaults.filterChipBorder(
                enabled = true,
                selected = false
            )
        }
    )
}

@Composable
fun GradientTypeSelector(
    currentType: Int,
    onTypeChange: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Gradient Type", style = MaterialTheme.typography.labelLarge)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            GradientTypeChip("Linear", GradientDrawable.LINEAR_GRADIENT, currentType, onTypeChange, Modifier.weight(1f))
            GradientTypeChip("Radial", GradientDrawable.RADIAL_GRADIENT, currentType, onTypeChange, Modifier.weight(1f))
            GradientTypeChip("Sweep", GradientDrawable.SWEEP_GRADIENT, currentType, onTypeChange, Modifier.weight(1f))
        }
    }
}

@Composable
fun GradientTypeChip(
    label: String,
    value: Int,
    currentValue: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val isSelected = currentValue == value
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )
    
    FilterChip(
        selected = isSelected,
        onClick = { onSelect(value) },
        label = { 
            Text(
                label,
                style = MaterialTheme.typography.labelMedium
            ) 
        },
        modifier = modifier.scale(scale),
        border = if (isSelected) {
            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        } else {
            FilterChipDefaults.filterChipBorder(
                enabled = true,
                selected = false
            )
        }
    )
}

@Composable
fun SliderControl(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int = 0
) {
    var isInteracting by remember { mutableStateOf(false) }
    val badgeScale by animateFloatAsState(
        targetValue = if (isInteracting) 1.15f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "badgeScale"
    )
    
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                label,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Surface(
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.primaryContainer,
                tonalElevation = if (isInteracting) 6.dp else 2.dp,
                modifier = Modifier.scale(badgeScale)
            ) {
                Text(
                    "${value.toInt()}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }
        }
        Slider(
            value = value,
            onValueChange = { 
                isInteracting = true
                onValueChange(it)
            },
            onValueChangeFinished = { isInteracting = false },
            valueRange = valueRange,
            steps = steps
        )
    }
}

@Composable
fun SwitchControl(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
fun ColorPickerControl(
    label: String,
    color: Int,
    onColorChange: (Int) -> Unit
) {
    var showColorPicker by remember { mutableStateOf(false) }
    val buttonScale by animateFloatAsState(
        targetValue = if (showColorPicker) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "buttonScale"
    )
    
    // Calculate contrasting text color based on background luminance
    val textColor = remember(color) {
        val red = ((color shr 16) and 0xFF) / 255f
        val green = ((color shr 8) and 0xFF) / 255f
        val blue = (color and 0xFF) / 255f
        
        // Calculate relative luminance (WCAG formula)
        val luminance = 0.2126f * red + 0.7152f * green + 0.0722f * blue
        
        // Use white text for dark backgrounds, black for light backgrounds
        if (luminance > 0.5f) androidx.compose.ui.graphics.Color.Black 
        else androidx.compose.ui.graphics.Color.White
    }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        FilledTonalButton(
            onClick = { showColorPicker = true },
            colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = androidx.compose.ui.graphics.Color(color),
                contentColor = textColor
            ),
            modifier = Modifier.scale(buttonScale),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 4.dp,
                pressedElevation = 8.dp
            )
        ) {
            Text(
                String.format("#%08X", color),
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
    
    if (showColorPicker) {
        ColorPickerDialog(
            currentColor = color,
            onDismiss = { showColorPicker = false },
            onColorSelected = {
                onColorChange(it)
                showColorPicker = false
            }
        )
    }
}
