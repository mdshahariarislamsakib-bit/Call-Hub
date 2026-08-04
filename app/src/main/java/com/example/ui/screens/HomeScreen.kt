package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.UserAccountEntity
import com.example.data.model.MatchFilter
import com.example.ui.components.AppHeaderBar
import com.example.ui.components.PulseGlowingButton
import com.example.ui.components.getFlagEmoji
import com.example.ui.theme.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    user: UserAccountEntity,
    filter: MatchFilter,
    onFilterChanged: (MatchFilter) -> Unit,
    onStartMatching: () -> Unit,
    onOpenWallet: () -> Unit,
    onOpenProfile: () -> Unit,
    onUnlockGenderFilter: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showGenderFilterDialog by remember { mutableStateOf(false) }
    var showCountryFilterDialog by remember { mutableStateOf(false) }
    var cameraFront by remember { mutableStateOf(true) }

    val countries = listOf("All Countries", "Bangladesh", "India", "Pakistan", "USA", "UK", "Japan", "Korea", "Canada", "Germany")
    val languages = listOf("English", "Bengali", "Hindi", "Arabic", "Spanish", "French")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Top Bar Header
        AppHeaderBar(
            coins = user.coins,
            isPremium = user.isPremium,
            onOpenWallet = onOpenWallet,
            onOpenProfile = onOpenProfile
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Filters Bar Surface Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, DarkCardBorder, RoundedCornerShape(20.dp)),
                color = DarkSurface
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "🎯 Matching Filters",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Gender Filter Chip
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    if (!user.isPremium && filter.genderPreference != "Anyone") {
                                        showGenderFilterDialog = true
                                    } else {
                                        showGenderFilterDialog = true
                                    }
                                }
                                .testTag("gender_filter_chip"),
                            color = DarkSurfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(1.dp, NeonPink.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Gender", fontSize = 10.sp, color = TextSecondaryDark)
                                    Text(filter.genderPreference, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                                }
                                if (!user.isPremium && filter.genderPreference != "Anyone") {
                                    Icon(Icons.Default.Lock, null, tint = GoldAccent, modifier = Modifier.size(16.dp))
                                } else {
                                    Icon(Icons.Default.ArrowDropDown, null, tint = NeonPink)
                                }
                            }
                        }

                        // Country Filter Chip
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { showCountryFilterDialog = true }
                                .testTag("country_filter_chip"),
                            color = DarkSurfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Country", fontSize = 10.sp, color = TextSecondaryDark)
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(getFlagEmoji(filter.countryPreference), fontSize = 12.sp)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            filter.countryPreference,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimaryDark,
                                            maxLines = 1
                                        )
                                    }
                                }
                                Icon(Icons.Default.ArrowDropDown, null, tint = NeonCyan)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Language Filter Row
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Language Priority:", fontSize = 11.sp, color = TextSecondaryDark)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(languages) { lang ->
                                FilterChip(
                                    selected = filter.languagePreference == lang,
                                    onClick = { onFilterChanged(filter.copy(languagePreference = lang)) },
                                    label = { Text(lang, fontSize = 11.sp) }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Camera Viewport Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(DarkSurfaceVariant)
                    .border(1.dp, DarkCardBorder, RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                // Camera View Placeholder / Stream graphic
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                colors = listOf(NeonPurple.copy(alpha = 0.25f), DarkBackground),
                                radius = 400f
                            )
                        )
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                            .background(DarkSurface)
                            .border(1.dp, NeonCyan, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (cameraFront) Icons.Default.CameraFront else Icons.Default.CameraRear,
                            contentDescription = "Camera",
                            tint = NeonCyan,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Camera Active & Face Detection Ready",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimaryDark
                    )

                    Text(
                        text = "Your camera preview is ready for matching",
                        fontSize = 11.sp,
                        color = TextSecondaryDark
                    )
                }

                // Camera Switch Overlay Button
                IconButton(
                    onClick = { cameraFront = !cameraFront },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .clip(CircleShape)
                        .background(DarkSurface.copy(alpha = 0.7f))
                ) {
                    Icon(
                        imageVector = Icons.Default.FlipCameraAndroid,
                        contentDescription = "Switch Cam",
                        tint = Color.White
                    )
                }

                // Live Face Required Badge
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = DarkSurface.copy(alpha = 0.8f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonGreen)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(NeonGreen)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Face Verified",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonGreen
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Main Pulse Start Call Button
            PulseGlowingButton(
                text = "START CALL MATCHING",
                icon = Icons.Default.VideoCall,
                onClick = onStartMatching,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Community Safety Note
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = "Safety",
                    tint = NeonCyan,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "AI Moderation & 24/7 Safety Active. Be respectful!",
                    fontSize = 11.sp,
                    color = TextSecondaryDark,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Gender Filter Selection Dialog
    if (showGenderFilterDialog) {
        AlertDialog(
            onDismissRequest = { showGenderFilterDialog = false },
            title = { Text("Select Gender Preference") },
            text = {
                Column {
                    Text(
                        text = "Targeting specific gender requires 20 Coins per match or VIP Membership.",
                        fontSize = 12.sp,
                        color = TextSecondaryDark,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    listOf("Anyone", "Male", "Female").forEach { g ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (g != "Anyone" && !user.isPremium && user.coins < 20) {
                                        onOpenWallet()
                                    } else {
                                        if (g != "Anyone" && !user.isPremium) {
                                            onUnlockGenderFilter()
                                        }
                                        onFilterChanged(filter.copy(genderPreference = g))
                                        showGenderFilterDialog = false
                                    }
                                }
                                .padding(vertical = 10.dp)
                        ) {
                            RadioButton(
                                selected = filter.genderPreference == g,
                                onClick = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(g, fontWeight = FontWeight.Bold)
                            if (g != "Anyone" && !user.isPremium) {
                                Spacer(modifier = Modifier.weight(1f))
                                Text("20 Coins", fontSize = 11.sp, color = GoldAccent, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showGenderFilterDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Country Filter Selection Dialog
    if (showCountryFilterDialog) {
        AlertDialog(
            onDismissRequest = { showCountryFilterDialog = false },
            title = { Text("Select Country Filter") },
            text = {
                Column(modifier = Modifier.height(260.dp).verticalScroll(rememberScrollState())) {
                    countries.forEach { country ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onFilterChanged(filter.copy(countryPreference = country))
                                    showCountryFilterDialog = false
                                }
                                .padding(vertical = 10.dp)
                        ) {
                            Text(getFlagEmoji(country), fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(country, fontWeight = FontWeight.SemiBold)
                            if (filter.countryPreference == country) {
                                Spacer(modifier = Modifier.weight(1f))
                                Icon(Icons.Default.Check, null, tint = NeonCyan)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCountryFilterDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}
