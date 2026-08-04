package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    onLoginSuccess: (loginType: String, email: String) -> Unit,
    onGuestMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    var authMode by remember { mutableStateOf("EMAIL") } // EMAIL, PHONE, GOOGLE
    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var phoneInput by remember { mutableStateOf("") }
    var otpInput by remember { mutableStateOf("") }
    var otpSent by remember { mutableStateOf(false) }

    // Age Gating verification
    var selectedBirthYear by remember { mutableStateOf(2004) }
    var showAgeError by remember { mutableStateOf(false) }

    val computedAge = 2026 - selectedBirthYear

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(DarkBackground, DarkSurface)
                )
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Welcome to Call Hub",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark
            )

            Text(
                text = "Sign in to connect with strangers 1:1 worldwide",
                fontSize = 13.sp,
                color = TextSecondaryDark,
                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
            )

            // Auth Type Selector
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
            ) {
                SegmentedButton(
                    selected = authMode == "EMAIL",
                    onClick = { authMode = "EMAIL" },
                    shape = SegmentedButtonDefaults.itemShape(0, 3)
                ) {
                    Text("Email")
                }
                SegmentedButton(
                    selected = authMode == "GOOGLE",
                    onClick = { authMode = "GOOGLE" },
                    shape = SegmentedButtonDefaults.itemShape(1, 3)
                ) {
                    Text("Google")
                }
                SegmentedButton(
                    selected = authMode == "PHONE",
                    onClick = { authMode = "PHONE" },
                    shape = SegmentedButtonDefaults.itemShape(2, 3)
                ) {
                    Text("Phone")
                }
            }

            when (authMode) {
                "EMAIL" -> {
                    OutlinedTextField(
                        value = emailInput,
                        onValueChange = { emailInput = it },
                        label = { Text("Email address") },
                        leadingIcon = { Icon(Icons.Default.Email, null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .testTag("email_input"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = passwordInput,
                        onValueChange = { passwordInput = it },
                        label = { Text("Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, null) },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                            .testTag("password_input"),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
                "GOOGLE" -> {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                            .clickable {
                                onLoginSuccess("GOOGLE", "google.user@callhub.io")
                            }
                            .testTag("google_login_card"),
                        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.AccountCircle, null, tint = NeonCyan)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Continue with Google Account", fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                        }
                    }
                }
                "PHONE" -> {
                    OutlinedTextField(
                        value = phoneInput,
                        onValueChange = { phoneInput = it },
                        label = { Text("Phone Number (+1...)") },
                        leadingIcon = { Icon(Icons.Default.Phone, null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .testTag("phone_input"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    if (otpSent) {
                        OutlinedTextField(
                            value = otpInput,
                            onValueChange = { otpInput = it },
                            label = { Text("Enter 6-digit OTP code") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                                .testTag("otp_input"),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }

            // Age-Gating Verification DOB Selector
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "🔞 Age Verification (Must be 18+)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldAccent
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Birth Year: $selectedBirthYear (Age: $computedAge)")
                        Slider(
                            value = selectedBirthYear.toFloat(),
                            onValueChange = { selectedBirthYear = it.toInt() },
                            valueRange = 1960f..2010f,
                            steps = 50,
                            modifier = Modifier.width(140.dp)
                        )
                    }
                }
            }

            if (showAgeError) {
                Text(
                    text = "⚠️ You must be at least 18 years old to join Call Hub.",
                    color = NeonPink,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Login / Continue Button
            Button(
                onClick = {
                    if (computedAge < 18) {
                        showAgeError = true
                        return@Button
                    }
                    showAgeError = false

                    if (authMode == "PHONE" && !otpSent) {
                        otpSent = true
                        return@Button
                    }

                    onLoginSuccess(authMode, emailInput.ifEmpty { "user@callhub.io" })
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("auth_submit_button"),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NeonPink)
            ) {
                Text(
                    text = if (authMode == "PHONE" && !otpSent) "Send OTP Code" else "Continue",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Guest Mode Option
            OutlinedButton(
                onClick = {
                    if (computedAge < 18) {
                        showAgeError = true
                        return@OutlinedButton
                    }
                    onGuestMode()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("guest_mode_button"),
                shape = RoundedCornerShape(24.dp)
            ) {
                Icon(Icons.Default.PersonOutline, null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Continue as Guest", color = TextPrimaryDark)
            }
        }
    }
}
