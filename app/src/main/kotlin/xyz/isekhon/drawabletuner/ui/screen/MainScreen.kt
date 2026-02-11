package xyz.isekhon.drawabletuner.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
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
    
    // Apply dynamic Material 3 colors on first load
    val colorScheme = MaterialTheme.colorScheme
    LaunchedEffect(Unit) {
        viewModel.applyDynamicColors(colorScheme)
    }
    
    // Collapse preview based on scroll position
    val isCollapsed by remember { derivedStateOf { scrollState.value > 50 } }
    
    val previewHeight by animateDpAsState(
        targetValue = if (isCollapsed) 120.dp else 280.dp,
        animationSpec = tween(durationMillis = 300),
        label = "previewHeight"
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(stringResource(R.string.app_name))
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
                            imageVector = Icons.Default.Code,
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
                                text = { 
                                    Text(
                                        "New Spec",
                                        style = MaterialTheme.typography.bodyLarge
                                    ) 
                                },
                                onClick = {
                                    viewModel.createNewSpec(colorScheme)
                                    showMoreMenu = false
                                },
                                leadingIcon = { 
                                    Icon(
                                        Icons.Default.Add,
                                        null,
                                        tint = MaterialTheme.colorScheme.primary
                                    ) 
                                },
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                            DropdownMenuItem(
                                text = { 
                                    Text(
                                        "Load Spec",
                                        style = MaterialTheme.typography.bodyLarge
                                    ) 
                                },
                                onClick = {
                                    showSpecChooser = true
                                    showMoreMenu = false
                                },
                                leadingIcon = { 
                                    Icon(
                                        Icons.Default.FolderOpen,
                                        null,
                                        tint = MaterialTheme.colorScheme.primary
                                    ) 
                                },
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                            DropdownMenuItem(
                                text = { 
                                    Text(
                                        if (currentSpec.id == 0) "Save Spec As..." else "Save Spec",
                                        style = MaterialTheme.typography.bodyLarge
                                    ) 
                                },
                                onClick = {
                                    if (currentSpec.id == 0) {
                                        showSaveDialog = true
                                    } else {
                                        viewModel.saveCurrentSpec()
                                    }
                                    showMoreMenu = false
                                },
                                leadingIcon = { 
                                    Icon(
                                        Icons.Default.Save,
                                        null,
                                        tint = MaterialTheme.colorScheme.primary
                                    ) 
                                },
                                modifier = Modifier.padding(vertical = 4.dp)
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
        ) {
            // Preview area that collapses vertically
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(previewHeight),
                tonalElevation = 2.dp
            ) {
                DrawablePreview(
                    drawable = drawable,
                    properties = properties,
                    modifier = Modifier.fillMaxSize()
                )
            }
            
            // Property controls
            PropertyControls(
                properties = properties,
                onPropertyChange = { name, value ->
                    viewModel.updateProperty(name, value)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            )
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
