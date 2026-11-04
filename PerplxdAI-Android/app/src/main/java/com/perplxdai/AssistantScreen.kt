package com.perplxdai

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import androidx.compose.ui.window.Dialog
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import com.perplxdai.data.AssistantCommand
import com.perplxdai.messaging.WhatsAppContact
import com.perplxdai.messaging.WhatsAppMessageRequest
import com.perplxdai.ui.theme.BackgroundDark
import com.perplxdai.ui.theme.BluePrimary
import com.perplxdai.ui.theme.PanelDark
import com.perplxdai.ui.theme.TextLight
import com.perplxdai.ui.theme.TextSoft
import com.perplxdai.voice.VoiceAssistantSession
import com.perplxdai.voice.VoiceSessionState
import com.perplxdai.viewmodel.AssistantMessage
import com.perplxdai.viewmodel.AssistantViewModel

@Composable
fun PerplxdApp(
    viewModel: AssistantViewModel = AssistantViewModel(),
    onMicTap: (() -> Unit)? = null,
    onWhatsAppReview: () -> Unit = {},
    onWhatsAppContactSelected: (WhatsAppContact) -> Unit = {},
    onWhatsAppCancel: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    val voiceSession by VoiceAssistantSession.state.collectAsState()
    val pendingWhatsAppRequest = voiceSession.pendingWhatsAppRequest
    val context = LocalContext.current
    var showSplash by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(SPLASH_DURATION_MILLIS)
        showSplash = false
    }

    if (showSplash) {
        Image(
            painter = painterResource(R.drawable.perplxd_splash),
            contentDescription = "Perplxd",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        return
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = BackgroundDark
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AssistantScreen(
                assistantName = state.assistantName,
                statusText = if (voiceSession.isActive) voiceSession.statusText else state.statusText,
                commands = state.commands,
                messages = state.messages,
                lastResponse = state.lastResponse,
                isHandsFreeActive = voiceSession.isActive,
                orbMode = when {
                    voiceSession.isProcessing -> OrbMode.Thinking
                    voiceSession.statusText.contains("speaking", ignoreCase = true) -> OrbMode.Speaking
                    voiceSession.isActive -> OrbMode.Listening
                    else -> OrbMode.Idle
                },
                onCommandClick = { command -> viewModel.activateCommand(command) },
                onVoiceCommand = { phrase -> viewModel.handleVoiceCommand(context, phrase) },
                onMicTap = onMicTap
            )

            if (pendingWhatsAppRequest != null) {
                WhatsAppReviewDialog(
                    request = pendingWhatsAppRequest,
                    contacts = voiceSession.whatsappContacts,
                    error = voiceSession.whatsappError,
                    onReview = onWhatsAppReview,
                    onContactSelected = onWhatsAppContactSelected,
                    onCancel = onWhatsAppCancel
                )
            } else if (voiceSession.error != null) {
                ListeningDialog(
                    session = voiceSession,
                    onStop = {
                        if (voiceSession.error != null) {
                            VoiceAssistantSession.clearError()
                        } else {
                            onMicTap?.invoke()
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun WhatsAppReviewDialog(
    request: WhatsAppMessageRequest,
    contacts: List<WhatsAppContact>,
    error: String?,
    onReview: () -> Unit,
    onContactSelected: (WhatsAppContact) -> Unit,
    onCancel: () -> Unit
) {
    Dialog(onDismissRequest = onCancel) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0B1F31)),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "WHATSAPP MESSAGE",
                    color = Color(0xFF8EE6FF),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text("To: ${request.recipientName}", color = TextLight, fontWeight = FontWeight.SemiBold)
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF102638)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = request.message,
                        color = TextLight,
                        modifier = Modifier.padding(14.dp),
                        fontSize = 15.sp,
                        lineHeight = 22.sp
                    )
                }
                Text(
                    text = error
                        ?: "Perplxd will open WhatsApp with this draft. Review it there and tap Send yourself.",
                    color = if (error != null) Color(0xFFFFB4A8) else TextSoft,
                    fontSize = 13.sp,
                    lineHeight = 19.sp
                )

                if (contacts.isNotEmpty()) {
                    Text("Which contact did you mean?", color = TextLight, fontWeight = FontWeight.SemiBold)
                    contacts.forEach { contact ->
                        Button(
                            onClick = { onContactSelected(contact) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF173754)),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text("${contact.name} · ${contact.phoneNumber}")
                        }
                    }
                } else {
                    Button(
                        onClick = onReview,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = if (error == null) "Find contact & open WhatsApp" else "Try again",
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Button(
                    onClick = onCancel,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF173754)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Cancel")
                }
            }
        }
    }
}

private const val SPLASH_DURATION_MILLIS = 1_100L

@Composable
fun LegacyAssistantScreen(
    assistantName: String,
    statusText: String,
    commands: List<AssistantCommand>,
    messages: List<AssistantMessage>,
    lastResponse: String,
    onCommandClick: (AssistantCommand) -> Unit,
    onVoiceCommand: (String) -> Unit,
    onMicTap: (() -> Unit)? = null,
    isHandsFreeActive: Boolean = false
) {
    var voiceInput by remember { mutableStateOf("play in my feelings by drake on spotify") }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        BackgroundDark,
                        Color(0xFF0B1A29),
                        Color(0xFF0A1622)
                    )
                )
            )
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = assistantName.uppercase(),
                    color = Color(0xFF8EE6FF),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF48E5A8))
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = "online",
                        color = Color(0xFF48E5A8),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Text(
                text = "Good evening, Pfano.",
                color = TextLight,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = statusText,
                color = TextSoft,
                fontSize = 14.sp
            )

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(commands) { command ->
                    CommandChip(
                        command = command,
                        onClick = { onCommandClick(command) }
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatusTile(title = "Core", value = "Stable", modifier = Modifier.weight(1f))
                StatusTile(title = "Audio", value = "Ready", modifier = Modifier.weight(1f))
                StatusTile(title = "Tasks", value = "4", modifier = Modifier.weight(1f))
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = PanelDark),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFF8EE6FF)
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            text = "Primary Assistant",
                            color = Color(0xFF8EE6FF),
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Text(
                        text = "How can I help you today?",
                        color = TextLight,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = lastResponse,
                        color = TextSoft,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF102638)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Active modules",
                        color = Color(0xFF8EE6FF),
                        fontWeight = FontWeight.SemiBold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Voice", color = TextLight)
                        Text("ONLINE", color = Color(0xFF48E5A8), fontWeight = FontWeight.Bold)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Intelligence", color = TextLight)
                        Text("READY", color = Color(0xFF48E5A8), fontWeight = FontWeight.Bold)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Automation", color = TextLight)
                        Text("QUEUED", color = Color(0xFF9BD9FF), fontWeight = FontWeight.Bold)
                    }
                }
            }

            Text(
                text = "Session feed",
                color = Color(0xFF8EE6FF),
                fontWeight = FontWeight.SemiBold
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF11293E)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    androidx.compose.material3.OutlinedTextField(
                        value = voiceInput,
                        onValueChange = { voiceInput = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Voice command") },
                        placeholder = { Text("play in my feelings by drake on spotify") },
                        singleLine = true,
                        colors = androidx.compose.material3.TextFieldDefaults.colors(
                            focusedTextColor = TextLight,
                            unfocusedTextColor = TextLight,
                            focusedContainerColor = Color(0xFF0B1F2D),
                            unfocusedContainerColor = Color(0xFF0B1F2D),
                            focusedIndicatorColor = BluePrimary,
                            unfocusedIndicatorColor = Color(0xFF2B475C)
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                if (onMicTap != null) {
                                    onMicTap()
                                } else {
                                    onVoiceCommand(voiceInput)
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.size(6.dp))
                            Text(
                                text = if (isHandsFreeActive) "Stop listening" else "Hands-free",
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Button(
                            onClick = { onVoiceCommand("play in my feelings by drake on spotify") },
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF173754)),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.size(6.dp))
                            Text(text = "Play", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messages) { message ->
                    MessageBubble(message = message)
                }
            }
        }
    }
}

@Composable
fun ListeningDialog(
    session: VoiceSessionState,
    onStop: () -> Unit
) {
    val pulse = rememberInfiniteTransition(label = "listening-pulse")
    val pulseScale by pulse.animateFloat(
        initialValue = 0.88f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900),
            repeatMode = RepeatMode.Reverse
        ),
        label = "listening-pulse-scale"
    )
    val pulseAlpha by pulse.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900),
            repeatMode = RepeatMode.Reverse
        ),
        label = "listening-pulse-alpha"
    )

    Dialog(onDismissRequest = onStop) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0B1F31)),
            shape = RoundedCornerShape(28.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier.size(132.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(112.dp)
                            .graphicsLayer {
                                scaleX = pulseScale
                                scaleY = pulseScale
                                alpha = pulseAlpha
                            }
                            .border(2.dp, Color(0xFF38D9FF), CircleShape)
                    )
                    Box(
                        modifier = Modifier
                            .size(78.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(Color(0xFF246BCE), Color(0xFF102D50))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = null,
                            tint = Color(0xFF9AF1FF),
                            modifier = Modifier.size(34.dp)
                        )
                    }
                }

                Text(
                    text = when {
                        session.error != null -> "VOICE INPUT UNAVAILABLE"
                        session.isProcessing -> "PERPLXD IS THINKING"
                        else -> "PERPLXD LISTENING"
                    },
                    color = Color(0xFF8EE6FF),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = session.error
                        ?: session.partialTranscript.ifBlank {
                            if (session.isProcessing) "Preparing your response..."
                            else "Speak naturally. Perplxd will respond when you pause."
                        },
                    color = TextLight,
                    fontSize = 15.sp,
                    lineHeight = 22.sp
                )

                Text(
                    text = "Microphone stays active. Recognition may use your device's configured speech service and use battery.",
                    color = TextSoft,
                    fontSize = 12.sp
                )

                Button(
                    onClick = onStop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF173754)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = if (session.error != null) "Close" else "Stop listening",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun CommandChip(
    command: AssistantCommand,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF11293E)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Icon(
            imageVector = command.icon,
            contentDescription = null
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text(text = command.title)
    }
}

@Composable
fun MessageBubble(message: AssistantMessage) {
    val bubbleColor = if (message.isUser) Color(0xFF1B4D7D) else Color(0xFF122D3A)
    val alignment = if (message.isUser) Alignment.End else Alignment.Start

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = bubbleColor),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = message.text,
                color = TextLight,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        }

        Text(
            text = message.time,
            color = TextSoft,
            fontSize = 10.sp,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF07141F)
@Composable
fun AssistantScreenPreview() {
    val sampleCommands = listOf(
        com.perplxdai.data.AssistantCommand(
            id = "status",
            title = "System",
            description = "Check devices and online systems",
            icon = Icons.Default.Star
        ),
        com.perplxdai.data.AssistantCommand(
            id = "research",
            title = "Research",
            description = "Find and summarize information",
            icon = Icons.Default.Mic
        )
    )

    val sampleMessages = listOf(
        AssistantMessage(text = "Good evening. I’m monitoring your priorities and systems.", isUser = false, time = "Now"),
        AssistantMessage(text = "Review my current tasks and keep the plan focused.", isUser = true, time = "Now")
    )

    AssistantScreen(
        assistantName = "Perplxd AI",
        statusText = "Online and ready",
        commands = sampleCommands,
        messages = sampleMessages,
        lastResponse = "System health is stable. Core services are synchronized and ready.",
        onCommandClick = {},
        onVoiceCommand = {},
        onMicTap = {}
    )
}

@Composable
fun StatusTile(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF11293E)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = title, color = TextSoft, fontSize = 12.sp)
            Text(text = value, color = TextLight, fontWeight = FontWeight.Bold)
        }
    }
}
