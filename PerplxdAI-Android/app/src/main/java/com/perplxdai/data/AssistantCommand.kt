package com.perplxdai.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assistant
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.ui.graphics.vector.ImageVector

data class AssistantCommand(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector
)

val defaultCommands = listOf(
    AssistantCommand(
        id = "status",
        title = "System",
        description = "Check devices and online systems",
        icon = Icons.Default.Bolt
    ),
    AssistantCommand(
        id = "research",
        title = "Research",
        description = "Find and summarize information",
        icon = Icons.Default.Search
    ),
    AssistantCommand(
        id = "schedule",
        title = "Schedule",
        description = "View tasks, reminders, and agenda",
        icon = Icons.Default.CalendarToday
    ),
    AssistantCommand(
        id = "security",
        title = "Security",
        description = "Review protection and system health",
        icon = Icons.Default.Security
    ),
    AssistantCommand(
        id = "assistant",
        title = "Assistant",
        description = "Activate the live AI interface",
        icon = Icons.Default.Assistant
    )
)
