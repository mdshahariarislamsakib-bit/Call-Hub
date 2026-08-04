package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.InCallChatMessage
import com.example.data.model.StrangerProfile
import com.example.ui.components.AvatarCircle
import com.example.ui.components.getFlagEmoji
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CallScreen(
    peer: StrangerProfile,
    onSkipCall: () -> Unit,
    onEndCall: () -> Unit,
    onAddFriend: (StrangerProfile) -> Unit,
    onReportUser: (reason: String, details: String, isChildSafetyEscalated: Boolean) -> Unit,
    onBlockUser: (StrangerProfile) -> Unit,
    modifier: Modifier = Modifier
) {
    // Call States
    var callDurationSeconds by remember { mutableIntStateOf(0) }
    var isMicMuted by remember { mutableStateOf(false) }
    var isCameraOff by remember { mutableStateOf(false) }
    var isFrontCam by remember { mutableStateOf(true) }
    var isFlashlightOn by remember { mutableStateOf(false) }
    var isBeautyFilterOn by remember { mutableStateOf(true) }
    var isBackgroundBlurOn by remember { mutableStateOf(false) }

    // Chat Drawer State
    var isChatOpen by remember { mutableStateOf(false) }
    var chatMessageInput by remember { mutableStateOf("") }
    var autoTranslateChat by remember { mutableStateOf(true) }
    var friendAdded by remember { mutableStateOf(false) }

    val chatMessages = remember {
        mutableStateListOf(
            InCallChatMessage(
                senderUid = "system",
                senderName = "Call Hub Safety",
                text = "Connected with ${peer.displayName}! Keep conversations friendly.",
                isSystem = true
            ),
            InCallChatMessage(
                senderUid = peer.uid,
                senderName = peer.displayName,
                text = "Hi there! 👋 How are you doing?",
                translatedText = "Hi there! 👋 How are you doing? (Translated)"
            )
        )
    }

    // Report Dialog State
    var showReportDialog by remember { mutableStateOf(false) }
    var selectedReportReason by remember { mutableStateOf("Nudity / Inappropriate Content") }
    var reportDetailsInput by remember { mutableStateOf("") }
    var isChildSafetyPriority by remember { mutableStateOf(false) }

    // Safety Alert Overlay
    var showScreenshotToast by remember { mutableStateOf(false) }

    // Call duration timer tick
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            callDurationSeconds++
        }
    }

    val formattedTimer = remember(callDurationSeconds) {
        val mins = callDurationSeconds / 60
        val secs = callDurationSeconds % 60
        String.format("%02d:%02d", mins, secs)
    }

    val cannedMessages = listOf("Hi! 👋", "Where are you from?", "Nice to meet you!", "Add me as friend!", "Great chatting with you! 😊")

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Main Remote Video Viewport (Simulated Live Remote Stream)
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Video Background Gradient Viewport
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(peer.avatarGradientStart).copy(alpha = 0.8f),
                                DarkBackground,
                                Color(peer.avatarGradientEnd).copy(alpha = 0.9f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AvatarCircle(
                        name = peer.displayName,
                        gradientStart = peer.avatarGradientStart,
                        gradientEnd = peer.avatarGradientEnd,
                        size = 120.dp,
                        borderWidth = 3.dp,
                        borderColor = NeonCyan
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = peer.displayName,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Text(
                        text = "${peer.gender} • ${peer.age} yrs • ${getFlagEmoji(peer.country)} ${peer.country}",
                        fontSize = 14.sp,
                        color = TextPrimaryDark
                    )

                    if (peer.bio.isNotEmpty()) {
                        Text(
                            text = "\"${peer.bio}\"",
                            fontSize = 12.sp,
                            color = TextSecondaryDark,
                            modifier = Modifier.padding(start = 32.dp, top = 6.dp, end = 32.dp)
                        )
                    }

                    // Interest Tags
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(top = 10.dp)
                    ) {
                        peer.interests.take(3).forEach { interest ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = DarkSurface.copy(alpha = 0.6f)
                            ) {
                                Text(
                                    text = interest,
                                    fontSize = 10.sp,
                                    color = NeonCyan,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Top Bar Overlay (Remote Profile Info + Call Timer)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 40.dp, end = 16.dp, bottom = 0.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = DarkSurface.copy(alpha = 0.8f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AvatarCircle(
                            name = peer.displayName,
                            gradientStart = peer.avatarGradientStart,
                            gradientEnd = peer.avatarGradientEnd,
                            size = 32.dp,
                            borderWidth = 1.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = peer.displayName,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                if (peer.isVerified) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.Verified,
                                        contentDescription = "Verified",
                                        tint = NeonCyan,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                            Text(
                                text = "${getFlagEmoji(peer.country)} ${peer.country} • ${peer.networkQuality}",
                                fontSize = 10.sp,
                                color = TextSecondaryDark
                            )
                        }
                    }
                }

                // Call Duration Timer
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = DarkSurface.copy(alpha = 0.8f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonPink)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(NeonPink)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = formattedTimer,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            // Local PIP Camera Viewport (Bottom Right)
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 100.dp, end = 16.dp)
                    .size(width = 100.dp, height = 150.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(DarkSurfaceVariant)
                    .border(1.5.dp, NeonCyan, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (isCameraOff) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.VideocamOff, null, tint = NeonPink, modifier = Modifier.size(24.dp))
                        Text("Cam Off", fontSize = 10.sp, color = TextSecondaryDark)
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(listOf(DarkSurfaceVariant, DarkBackground))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isFrontCam) "Front Cam" else "Rear Cam",
                            fontSize = 10.sp,
                            color = NeonCyan
                        )
                    }
                }

                // Camera Toggle Controls in PIP
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = { isFrontCam = !isFrontCam },
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(DarkSurface.copy(alpha = 0.8f))
                    ) {
                        Icon(Icons.Default.FlipCameraAndroid, null, tint = Color.White, modifier = Modifier.size(12.dp))
                    }

                    IconButton(
                        onClick = { isFlashlightOn = !isFlashlightOn },
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(if (isFlashlightOn) GoldAccent else DarkSurface.copy(alpha = 0.8f))
                    ) {
                        Icon(Icons.Default.FlashOn, null, tint = if (isFlashlightOn) Color.Black else Color.White, modifier = Modifier.size(12.dp))
                    }
                }
            }

            // Active Filters Overlay Badges (Beauty, Blur, AI Scanner)
            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 100.dp, start = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (isBeautyFilterOn) {
                    Surface(shape = RoundedCornerShape(10.dp), color = DarkSurface.copy(alpha = 0.7f)) {
                        Text("✨ Beauty On", fontSize = 10.sp, color = NeonPink, modifier = Modifier.padding(4.dp))
                    }
                }
                if (isBackgroundBlurOn) {
                    Surface(shape = RoundedCornerShape(10.dp), color = DarkSurface.copy(alpha = 0.7f)) {
                        Text("🌫️ Blur On", fontSize = 10.sp, color = NeonCyan, modifier = Modifier.padding(4.dp))
                    }
                }
            }

            // Screenshot Alert Toast
            AnimatedVisibility(
                visible = showScreenshotToast,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = NeonPink,
                    shadowElevation = 8.dp
                ) {
                    Text(
                        text = "📸 Screenshot Alert: Notified both participants!",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }

            // In-Call Text Chat Drawer Overlay
            AnimatedVisibility(
                visible = isChatOpen,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 90.dp, start = 12.dp, end = 12.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .border(1.dp, DarkCardBorder, RoundedCornerShape(20.dp)),
                    color = DarkSurface.copy(alpha = 0.95f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("In-Call Text Chat", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Auto-Translate", fontSize = 10.sp, color = TextSecondaryDark)
                                Switch(
                                    checked = autoTranslateChat,
                                    onCheckedChange = { autoTranslateChat = it },
                                    modifier = Modifier.scale(0.7f)
                                )
                                IconButton(onClick = { isChatOpen = false }) {
                                    Icon(Icons.Default.Close, null, tint = TextSecondaryDark, modifier = Modifier.size(18.dp))
                                }
                            }
                        }

                        // Chat Messages List
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            reverseLayout = true
                        ) {
                            items(chatMessages.reversed()) { msg ->
                                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                    Text(
                                        text = "${msg.senderName}: ${msg.text}",
                                        fontSize = 12.sp,
                                        fontWeight = if (msg.isSystem) FontWeight.Bold else FontWeight.Normal,
                                        color = if (msg.isSystem) GoldAccent else TextPrimaryDark
                                    )
                                    if (autoTranslateChat && msg.translatedText != null) {
                                        Text(
                                            text = msg.translatedText,
                                            fontSize = 10.sp,
                                            color = NeonCyan
                                        )
                                    }
                                }
                            }
                        }

                        // Canned Quick Messages Bar
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(vertical = 6.dp)
                        ) {
                            items(cannedMessages) { canned ->
                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable {
                                            chatMessages.add(
                                                InCallChatMessage(
                                                    senderUid = "local_user_1",
                                                    senderName = "You",
                                                    text = canned
                                                )
                                            )
                                        },
                                    color = DarkSurfaceVariant
                                ) {
                                    Text(canned, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                }
                            }
                        }

                        // Text Input Row
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = chatMessageInput,
                                onValueChange = { chatMessageInput = it },
                                placeholder = { Text("Type message...", fontSize = 12.sp) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("chat_input_field"),
                                shape = RoundedCornerShape(20.dp)
                            )

                            Spacer(modifier = Modifier.width(6.dp))

                            IconButton(
                                onClick = {
                                    if (chatMessageInput.isNotBlank()) {
                                        chatMessages.add(
                                            InCallChatMessage(
                                                senderUid = "local_user_1",
                                                senderName = "You",
                                                text = chatMessageInput.trim()
                                            )
                                        )
                                        chatMessageInput = ""
                                    }
                                },
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(NeonPink)
                                    .testTag("send_chat_button")
                            ) {
                                Icon(Icons.Default.Send, null, tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }

            // In-Call Action Control Bar (Bottom)
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(12.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .border(1.dp, DarkCardBorder, RoundedCornerShape(28.dp)),
                color = DarkSurface.copy(alpha = 0.9f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Mute Mic
                    IconButton(
                        onClick = { isMicMuted = !isMicMuted },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(if (isMicMuted) NeonPink else DarkSurfaceVariant)
                            .testTag("mute_mic_button")
                    ) {
                        Icon(
                            imageVector = if (isMicMuted) Icons.Default.MicOff else Icons.Default.Mic,
                            contentDescription = "Mute",
                            tint = Color.White
                        )
                    }

                    // Toggle Camera
                    IconButton(
                        onClick = { isCameraOff = !isCameraOff },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(if (isCameraOff) NeonPink else DarkSurfaceVariant)
                            .testTag("camera_off_button")
                    ) {
                        Icon(
                            imageVector = if (isCameraOff) Icons.Default.VideocamOff else Icons.Default.Videocam,
                            contentDescription = "Cam",
                            tint = Color.White
                        )
                    }

                    // Open Chat
                    IconButton(
                        onClick = { isChatOpen = !isChatOpen },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(if (isChatOpen) NeonCyan else DarkSurfaceVariant)
                            .testTag("toggle_chat_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Chat,
                            contentDescription = "Chat",
                            tint = if (isChatOpen) Color.Black else Color.White
                        )
                    }

                    // Add Friend
                    IconButton(
                        onClick = {
                            if (!friendAdded) {
                                onAddFriend(peer)
                                friendAdded = true
                            }
                        },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(if (friendAdded) GoldAccent else DarkSurfaceVariant)
                            .testTag("add_friend_button")
                    ) {
                        Icon(
                            imageVector = if (friendAdded) Icons.Default.Check else Icons.Default.PersonAdd,
                            contentDescription = "Add Friend",
                            tint = if (friendAdded) Color.Black else Color.White
                        )
                    }

                    // Report User
                    IconButton(
                        onClick = { showReportDialog = true },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(DarkSurfaceVariant)
                            .testTag("report_user_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Flag,
                            contentDescription = "Report",
                            tint = GoldAccent
                        )
                    }

                    // Skip / Next User (Explicit re-match)
                    Button(
                        onClick = onSkipCall,
                        modifier = Modifier
                            .height(44.dp)
                            .testTag("skip_call_button"),
                        shape = RoundedCornerShape(22.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonPink)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Skip", fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.SkipNext, null, modifier = Modifier.size(18.dp))
                        }
                    }

                    // End Call
                    IconButton(
                        onClick = onEndCall,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color.Red)
                            .testTag("end_call_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CallEnd,
                            contentDescription = "End Call",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }

    // Report Reason Sheet Dialog (with Child Safety Priority Escalation as required by PRD 4.13)
    if (showReportDialog) {
        AlertDialog(
            onDismissRequest = { showReportDialog = false },
            title = { Text("Report User (${peer.displayName})") },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text(
                        text = "Choose a reason for reporting. All reports are confidential.",
                        fontSize = 12.sp,
                        color = TextSecondaryDark,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    val reasons = listOf(
                        "Nudity / Inappropriate Content",
                        "Violence / Graphic Content",
                        "Harassment / Bullying",
                        "Child Abuse / Exploitation (Priority Escalated)",
                        "Fake Account / Spam",
                        "Other"
                    )

                    reasons.forEach { r ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedReportReason = r
                                    isChildSafetyPriority = r.contains("Child Abuse")
                                }
                                .padding(vertical = 8.dp)
                        ) {
                            RadioButton(
                                selected = selectedReportReason == r,
                                onClick = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = r,
                                fontSize = 13.sp,
                                color = if (r.contains("Child Abuse")) Color.Red else TextPrimaryDark,
                                fontWeight = if (r.contains("Child Abuse")) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }

                    OutlinedTextField(
                        value = reportDetailsInput,
                        onValueChange = { reportDetailsInput = it },
                        label = { Text("Additional Details (Optional)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        shape = RoundedCornerShape(12.dp)
                    )

                    if (isChildSafetyPriority) {
                        Surface(
                            modifier = Modifier.padding(top = 10.dp),
                            shape = RoundedCornerShape(8.dp),
                            color = Color.Red.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.Red)
                        ) {
                            Text(
                                text = "⚠️ Child safety reports trigger immediate priority escalation to human moderators.",
                                fontSize = 11.sp,
                                color = Color.Red,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onReportUser(selectedReportReason, reportDetailsInput, isChildSafetyPriority)
                        showReportDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonPink)
                ) {
                    Text("Submit Report")
                }
            },
            dismissButton = {
                TextButton(onClick = { showReportDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
