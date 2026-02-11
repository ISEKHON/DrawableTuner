package xyz.isekhon.drawabletuner.ui.screen

import android.graphics.drawable.Drawable
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import xyz.isekhon.drawabletuner.R
import xyz.isekhon.drawabletuner.data.model.DrawablePropertiesInRoom
import xyz.isekhon.drawabletuner.ui.components.DrawablePreview
import xyz.isekhon.drawabletuner.ui.components.PropertyControls
import xyz.isekhon.drawabletuner.ui.components.SaveSpecDialog
import xyz.isekhon.drawabletuner.ui.components.SpecChooserDialog
import xyz.isekhon.drawabletuner.ui.viewmodel.DrawableViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: DrawableViewModel = viewModel(),
    onNavigateToCodeView: (DrawablePropertiesInRoom) -> Unit
) {
    val properties by viewModel.properties.collectAsState()
    val drawable by viewModel.drawable.collectAsState()
    val currentSpec by viewModel.currentSpec.collectAsState()
    val isEdited by viewModel.isEdited.collectAsState()
    val savedSpecs by viewModel.savedSpecs.collectAsState()
    
    var showSaveDialog by remember { mutableStateOf(false) }
    var showSpecChooser by remember { mutableStateOf(false) }
    var showMoreMenu by remember { mutableStateOf(false) }
    
    val scrollState = rememberScrollState()
    
    // Use derivedStateOf to minimize recompositions
    val isCollapsed by remember { derivedStateOf { scrollState.value > 50 } }
    
    val previewHeight by animateDpAsState(
        targetValue = if (isCollapsed) 120.dp else 280.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "previewHeight"
    )
    
    val previewWidth by animateDpAsState(
        targetValue = if (isCollapsed) 120.dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "previewWidth"
    )
    
    val previewElevation by animateDpAsState(
        targetValue = if (isCollapsed) 8.dp else 2.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "previewElevation"
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Gradient Drawable Tuner")
                        if (currentSpec.name.isNotEmpty()) {
                            Text(
                                text = if (isEdited) "${currentSpec.name} •" else currentSpec.name,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isEdited) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { onNavigateToCodeView(properties) }) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "View Code"
                        )
                    }
                    Box {
                        IconButton(onClick = { showMoreMenu = true }) {
                            Icon(Icons.Default.MoreVert, "More options")
                        }
                        DropdownMenu(
                            expanded = showMoreMenu,
                            onDismissRequest = { showMoreMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("New Spec") },
                                onClick = {
                                    viewModel.createNewSpec()
                                    showMoreMenu = false
                                },
                                leadingIcon = { Icon(Icons.Default.Add, null) }
                            )
                            DropdownMenuItem(
                                text = { Text("Load Spec") },
                                onClick = {
                                    showSpecChooser = true
                                    showMoreMenu = false
                                },
                                leadingIcon = { Icon(Icons.Default.Menu, null) }
                            )
                            DropdownMenuItem(
                                text = { Text(if (currentSpec.id == 0) "Save Spec As..." else "Save Spec") },
                                onClick = {
                                    if (currentSpec.id == 0) {
                                        showSaveDialog = true
                                    } else {
                                        viewModel.saveCurrentSpec()
                                    }
                                    showMoreMenu = false
                                },
                                leadingIcon = { Icon(Icons.Default.Save, null) }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Controls - Scrollable
            PropertyControls(
                properties = properties,
                onPropertyChange = { name, value ->
                    viewModel.updateProperty(name, value)
                },
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = if (isCollapsed) 12.dp else previewHeight + 12.dp,
                        bottom = 12.dp
                    )
            )
            
            // Collapsible Preview - Floats on top
            Surface(
                modifier = Modifier
                    .then(
                        if (isCollapsed) {
                            Modifier
                                .size(previewWidth, previewHeight)
                                .padding(start = 16.dp, top = 8.dp)
                        } else {
                            Modifier
                                .fillMaxWidth()
                                .height(previewHeight)
                        }
                    )
                    .zIndex(1f),
                tonalElevation = previewElevation,
                shadowElevation = previewElevation,
                shape = if (isCollapsed) MaterialTheme.shapes.medium else MaterialTheme.shapes.extraSmall
            ) {
                DrawablePreview(
                    drawable = drawable,
                    properties = properties,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
    
    if (showSaveDialog) {
        SaveSpecDialog(
            onDismiss = { showSaveDialog = false },
            onSave = { name ->
                viewModel.saveCurrentSpec(name)
                showSaveDialog = false
            }
        )
    }
    
    if (showSpecChooser) {
        SpecChooserDialog(
            specs = savedSpecs,
            onDismiss = { showSpecChooser = false },
            onSelect = { spec ->
                viewModel.applySpec(spec)
                showSpecChooser = false
            }
        )
    }
}
