package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MatchFilter
import com.example.data.model.StrangerProfile
import com.example.ui.components.getFlagEmoji
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun MatchmakingScreen(
    filter: MatchFilter,
    matchedPeer: StrangerProfile?,
    onMatchFound: () -> Unit,
    onCancelMatching: () -> Unit,
    modifier: Modifier = Modifier
) {
    var secondsSearching by remember { mutableStateOf(0) }
    var isConnecting by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (secondsSearching < 2) {
            delay(1000)
            secondsSearching++
        }
        isConnecting = true
        delay(1200)
        onMatchFound()
    }

    // Infinite radar pulse animation
    val infiniteTransition = rememberInfiniteTransition(label = "radar")
    val pulse1 by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse1"
    )
    val pulse2 by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, delayMillis = 600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse2"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // Radar Animation Center
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(220.dp)
            ) {
                // Pulse waves
                Box(
                    modifier = Modifier
                        .scale(pulse1)
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(NeonPink.copy(alpha = 0.15f))
                        .border(1.dp, NeonPink.copy(alpha = 0.3f), CircleShape)
                )

                Box(
                    modifier = Modifier
                        .scale(pulse2)
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(NeonCyan.copy(alpha = 0.15f))
                        .border(1.dp, NeonCyan.copy(alpha = 0.3f), CircleShape)
                )

                // Center Icon / Avatar
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(listOf(DarkSurface, DarkSurfaceVariant))
                        )
                        .border(2.dp, NeonCyan, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isConnecting) Icons.Default.Search else Icons.Default.Radar,
                        contentDescription = "Radar",
                        tint = if (isConnecting) NeonPink else NeonCyan,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = if (isConnecting) "MATCH FOUND! CONNECTING..." else "FINDING STRANGER...",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (isConnecting) NeonPink else TextPrimaryDark,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Active Filters Summary Chip
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = DarkSurfaceVariant,
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Filters: ${filter.genderPreference} • ${getFlagEmoji(filter.countryPreference)} ${filter.countryPreference} • ${filter.languagePreference}",
                        fontSize = 12.sp,
                        color = TextSecondaryDark,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (matchedPeer != null && isConnecting) {
                // Matched Preview Tag
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = NeonPink.copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonPink)
                ) {
                    Text(
                        text = "Pairing with ${matchedPeer.displayName} (${getFlagEmoji(matchedPeer.country)} ${matchedPeer.country})",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            } else {
                Text(
                    text = "Searching queue... ($secondsSearching s)",
                    fontSize = 13.sp,
                    color = TextSecondaryDark
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Cancel Button
            OutlinedButton(
                onClick = onCancelMatching,
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .height(48.dp)
                    .testTag("cancel_matching_button"),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimaryDark)
            ) {
                Icon(Icons.Default.Close, null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Cancel Search")
            }
        }
    }
}
