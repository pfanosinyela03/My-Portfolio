package com.perplxdai

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.perplxdai.data.AssistantCommand
import com.perplxdai.viewmodel.AssistantMessage

private val OrbBackground = Brush.verticalGradient(
    listOf(Color(0xFF07164A), Color(0xFF040920), Color(0xFF020208))
)
private val SoftBlue = Color(0xFF9BB8E6)
private val GlowBlue = Color(0xFF1E6BFF)

@Composable
fun AssistantScreen(
    assistantName: String,
    statusText: String,
    commands: List<AssistantCommand>,
    messages: List<AssistantMessage>,
    lastResponse: String,
    onCommandClick: (AssistantCommand) -> Unit,
    onVoiceCommand: (String) -> Unit,
    onMicTap: (() -> Unit)? = null,
    isHandsFreeActive: Boolean = false,
    orbMode: OrbMode = OrbMode.Idle
) {
    var input by remember { mutableStateOf("") }
    val send = {
        if (input.isNotBlank()) {
            onVoiceCommand(input)
            input = ""
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OrbBackground)
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.weight(0.7f))
        PerplxdOrb(mode = orbMode)
        Text(
            text = statusText,
            color = Color(0xFFD0E2FF),
            fontSize = 15.sp,
            modifier = Modifier.padding(top = 8.dp)
        )
        Text(
            text = lastResponse,
            color = Color(0xFF7F9BC8),
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            maxLines = 3,
            modifier = Modifier
                .padding(top = 6.dp)
                .fillMaxWidth()
        )
        Spacer(Modifier.weight(1f))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(26.dp))
                .background(Color(0x14FFFFFF))
                .border(1.dp, Color(0x663C78FF), RoundedCornerShape(26.dp))
                .padding(start = 16.dp, end = 7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Mic, contentDescription = null, tint = SoftBlue, modifier = Modifier.size(20.dp))
            Box(modifier = Modifier.weight(1f).padding(horizontal = 10.dp)) {
                if (input.isEmpty()) Text("Ask anything…", color = Color(0xFF7F9BC8), fontSize = 14.sp)
                BasicTextField(
                    value = input,
                    onValueChange = { input = it },
                    singleLine = true,
                    textStyle = TextStyle(color = Color.White, fontSize = 14.sp),
                    cursorBrush = SolidColor(Color.White),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = { send() }),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(GlowBlue)
                    .clickable { send() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(18.dp))
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ActionTile(
                icon = Icons.Default.Chat,
                label = "WhatsApp",
                modifier = Modifier.weight(1f)
            ) { input = "text  on whatsapp saying " }
            ActionTile(
                icon = if (isHandsFreeActive) Icons.Default.Stop else Icons.Default.Mic,
                label = if (isHandsFreeActive) "Stop" else "Listen",
                modifier = Modifier.weight(1f)
            ) { onMicTap?.invoke() ?: Unit }
        }
    }
}

@Composable
private fun ActionTile(
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0x0DFFFFFF))
            .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(icon, contentDescription = null, tint = Color(0xFF8EC8FF), modifier = Modifier.size(22.dp))
        Text(label, color = Color(0xFFD0E2FF), fontSize = 11.sp)
    }
}
