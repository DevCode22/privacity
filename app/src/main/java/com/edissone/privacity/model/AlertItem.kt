package com.edissone.privacity.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class AlertItem(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val color: Color,
    val timestamp: String,
    val priority: String,
    val action: String = "",
    val actionLabel: String = "Ver detalhes"
)
