package xyz.isekhon.drawabletuner.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun ColorPickerDialog(
    currentColor: Int,
    onDismiss: () -> Unit,
    onColorSelected: (Int) -> Unit
) {
    val predefinedColors = remember {
        listOf(
            0xFFFF0000.toInt(), 0xFF00FF00.toInt(), 0xFF0000FF.toInt(),
            0xFFFFFF00.toInt(), 0xFFFF00FF.toInt(), 0xFF00FFFF.toInt(),
            0xFF000000.toInt(), 0xFFFFFFFF.toInt(), 0xFF808080.toInt(),
            0xFFFFA500.toInt(), 0xFF800080.toInt(), 0xFF008080.toInt(),
            0xFF2DCFCA.toInt(), 0xFF24A5A1.toInt(), 0x7FFFFFFF,
            0x00000000, 0x80000000.toInt(), 0xC0000000.toInt()
        )
    }
    
    Dialog(onDismissRequest = onDismiss) {
        Card {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Select Color", style = MaterialTheme.typography.titleLarge)
                
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.height(300.dp)
                ) {
                    items(predefinedColors.size) { index ->
                        val color = predefinedColors[index]
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .background(
                                    color = Color(color.toLong()),
                                    shape = CircleShape
                                )
                                .border(
                                    width = if (color == currentColor) 3.dp else 1.dp,
                                    color = if (color == currentColor) Color.Blue else Color.Gray,
                                    shape = CircleShape
                                )
                                .clickable { onColorSelected(color) }
                        )
                    }
                }
                
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
