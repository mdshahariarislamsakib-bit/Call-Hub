package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.AppSettingEntity
import com.example.data.db.BlockedUserEntity
import com.example.data.db.UserAccountEntity
import com.example.ui.components.AvatarCircle
import com.example.ui.components.getFlagEmoji
import com.example.ui.theme.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileScreen(
    user: UserAccountEntity,
    settings: AppSettingEntity,
    blockedUsers: List<BlockedUserEntity>,
    onUpdateProfile: (
        displayName: String,
        username: String,
        gender: String,
        age: Int,
        country: String,
        language: String,
        bio: String,
        interests: List<String>
    ) -> Unit,
    onUpdateSettings: (AppSettingEntity) -> Unit,
    onUnblockUser: (String) -> Unit,
    onOpenAdminPanel: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isEditing by remember { mutableStateOf(false) }

    var displayNameInput by remember { mutableStateOf(user.displayName) }
    var usernameInput by remember { mutableStateOf(user.username) }
    var genderInput by remember { mutableStateOf(user.gender) }
    var ageInput by remember { mutableStateOf(user.age) }
    var countryInput by remember { mutableStateOf(user.country) }
    var languageInput by remember { mutableStateOf(user.preferredLanguage) }
    var bioInput by remember { mutableStateOf(user.bio) }

    var showPrivacyPolicyDialog by remember { mutableStateOf(false) }

    val countries = listOf("USA", "Bangladesh", "India", "Pakistan", "UK", "Japan", "Korea", "Canada", "Germany")
    val languages = listOf("English", "Bengali", "Hindi", "Arabic", "Spanish", "French")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Profile & Settings",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark
            )

            IconButton(
                onClick = { isEditing = !isEditing },
                modifier = Modifier.testTag("edit_profile_toggle_button")
            ) {
                Icon(
                    imageVector = if (isEditing) Icons.Default.Check else Icons.Default.Edit,
                    contentDescription = "Edit Profile",
                    tint = NeonPink
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Profile Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(20.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AvatarCircle(name = user.displayName, size = 80.dp)

                Spacer(modifier = Modifier.height(12.dp))

                if (isEditing) {
                    OutlinedTextField(
                        value = displayNameInput,
                        onValueChange = { displayNameInput = it },
                        label = { Text("Display Name") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .testTag("profile_edit_name"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = usernameInput,
                        onValueChange = { usernameInput = it },
                        label = { Text("Username") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .testTag("profile_edit_username"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = bioInput,
                        onValueChange = { bioInput = it },
                        label = { Text("Bio") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .testTag("profile_edit_bio"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Button(
                        onClick = {
                            onUpdateProfile(
                                displayNameInput,
                                usernameInput,
                                genderInput,
                                ageInput,
                                countryInput,
                                languageInput,
                                bioInput,
                                user.interests.split(", ")
                            )
                            isEditing = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                        modifier = Modifier.fillMaxWidth().testTag("save_profile_button")
                    ) {
                        Text("Save Profile Changes")
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(user.displayName, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                        if (user.isPremium) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("VIP", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    Text("@${user.username} • Account Type: ${user.loginType}", fontSize = 12.sp, color = TextSecondaryDark)

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "${user.gender} • ${user.age} yrs • ${getFlagEmoji(user.country)} ${user.country} • ${user.preferredLanguage}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = NeonCyan
                    )

                    Text(
                        text = "\"${user.bio}\"",
                        fontSize = 12.sp,
                        color = TextSecondaryDark,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Privacy & Security Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(18.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Security, null, tint = NeonGreen)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Privacy & Security Rules", fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "🔒 Your email, phone number, DOB, and IP address are NEVER shared with other users.",
                    fontSize = 12.sp,
                    color = TextSecondaryDark
                )

                TextButton(
                    onClick = { showPrivacyPolicyDialog = true },
                    modifier = Modifier.testTag("privacy_policy_button")
                ) {
                    Text("View Full Privacy Policy", fontSize = 12.sp, color = NeonCyan)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // App Settings Section
        Text("⚙️ App Preferences", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)

        Spacer(modifier = Modifier.height(10.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(18.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Auto-Match After Skip", color = TextPrimaryDark)
                    Switch(
                        checked = settings.autoMatch,
                        onCheckedChange = { onUpdateSettings(settings.copy(autoMatch = it)) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Auto-Translate Chat Messages", color = TextPrimaryDark)
                    Switch(
                        checked = settings.autoTranslateChat,
                        onCheckedChange = { onUpdateSettings(settings.copy(autoTranslateChat = it)) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Face Required Verification", color = TextPrimaryDark)
                    Switch(
                        checked = settings.faceDetectionRequired,
                        onCheckedChange = { onUpdateSettings(settings.copy(faceDetectionRequired = it)) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Data Saver Mode (Lower Bitrate)", color = TextPrimaryDark)
                    Switch(
                        checked = settings.dataSaverMode,
                        onCheckedChange = { onUpdateSettings(settings.copy(dataSaverMode = it)) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Blocked Users Section
        if (blockedUsers.isNotEmpty()) {
            Text("🚫 Blocked Users (${blockedUsers.size})", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                blockedUsers.forEach { blocked ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(blocked.name, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                            Button(
                                onClick = { onUnblockUser(blocked.blockedUid) },
                                colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant)
                            ) {
                                Text("Unblock", fontSize = 11.sp, color = NeonPink)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Admin Moderation Panel Button
        Button(
            onClick = onOpenAdminPanel,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("admin_panel_button"),
            colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
            shape = RoundedCornerShape(24.dp)
        ) {
            Icon(Icons.Default.AdminPanelSettings, null, tint = GoldAccent)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Open Moderation & Admin Panel", color = GoldAccent, fontWeight = FontWeight.Bold)
        }
    }

    if (showPrivacyPolicyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyPolicyDialog = false },
            title = { Text("Call Hub Privacy Policy") },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text(
                        text = "1. Personal Information Privacy:\nYour email address, phone number, date of birth, IP address, and device ID are stored securely in isolated private server-side collections and are NEVER exposed to other clients or included in public profile documents.\n\n" +
                                "2. Visible Information:\nOnly your display name, username, age (derived from DOB), gender, country, preferred language, bio, and interests tags are visible to other users.\n\n" +
                                "3. AI Content Moderation:\nVideo frames are periodically sampled using AI vision models solely to detect nudity and graphic violence. Confirmed violations result in account suspension.\n\n" +
                                "4. Safety & Age Gating:\nCall Hub is strictly for adults (18+). Reports involving child safety undergo immediate priority escalation to human moderators.",
                        fontSize = 12.sp,
                        color = TextSecondaryDark
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showPrivacyPolicyDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}
