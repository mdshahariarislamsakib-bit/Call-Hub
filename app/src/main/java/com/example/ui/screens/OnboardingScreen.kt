package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
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
import com.example.ui.theme.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OnboardingScreen(
    onCompleteProfile: (
        displayName: String,
        username: String,
        gender: String,
        country: String,
        language: String,
        bio: String,
        interests: List<String>
    ) -> Unit,
    modifier: Modifier = Modifier
) {
    var displayName by remember { mutableStateOf("Alex Rider") }
    var username by remember { mutableStateOf("alex_rider") }
    var selectedGender by remember { mutableStateOf("Male") }
    var selectedCountry by remember { mutableStateOf("USA") }
    var selectedLanguage by remember { mutableStateOf("English") }
    var bio by remember { mutableStateOf("Love meeting new friends around the world! 🌍✨") }

    val availableInterests = listOf("Gaming", "Music", "Movies", "Anime", "Sports", "Study", "Programming", "Travel")
    var selectedInterests by remember { mutableStateOf(setOf("Gaming", "Music", "Travel")) }

    val countries = listOf("USA", "Bangladesh", "India", "Pakistan", "UK", "Japan", "Korea", "Canada", "Germany")
    val languages = listOf("English", "Bengali", "Hindi", "Arabic", "Spanish", "French")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(DarkBackground, DarkSurface)
                )
            )
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Complete Your Profile",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimaryDark
        )

        Text(
            text = "This is how other users will see you during random video calls.",
            fontSize = 13.sp,
            color = TextSecondaryDark,
            modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
        )

        // Profile Avatar Placeholder
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(90.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(NeonPink, NeonPurple))),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Avatar",
                tint = Color.White,
                modifier = Modifier.size(50.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Name Fields
        OutlinedTextField(
            value = displayName,
            onValueChange = { displayName = it },
            label = { Text("Display Name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .testTag("onboarding_name_input"),
            shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
            value = username,
            onValueChange = { username = it.lowercase().trim() },
            label = { Text("Unique Username") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .testTag("onboarding_username_input"),
            shape = RoundedCornerShape(12.dp)
        )

        // Gender Selector
        Text(
            text = "Gender",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondaryDark,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            listOf("Male", "Female", "Other").forEach { gender ->
                FilterChip(
                    selected = selectedGender == gender,
                    onClick = { selectedGender = gender },
                    label = { Text(gender) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Country & Language
        Text(
            text = "Country",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondaryDark,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            items(countries) { country ->
                FilterChip(
                    selected = selectedCountry == country,
                    onClick = { selectedCountry = country },
                    label = { Text(country) }
                )
            }
        }

        Text(
            text = "Preferred Language",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondaryDark,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            items(languages) { lang ->
                FilterChip(
                    selected = selectedLanguage == lang,
                    onClick = { selectedLanguage = lang },
                    label = { Text(lang) }
                )
            }
        }

        // Bio
        OutlinedTextField(
            value = bio,
            onValueChange = { bio = it },
            label = { Text("Short Bio") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .testTag("onboarding_bio_input"),
            shape = RoundedCornerShape(12.dp)
        )

        // Interests Multi-Select
        Text(
            text = "Interests (Used for smart matching)",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondaryDark,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        ) {
            availableInterests.forEach { interest ->
                val isSelected = interest in selectedInterests
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        selectedInterests = if (isSelected) {
                            selectedInterests - interest
                        } else {
                            selectedInterests + interest
                        }
                    },
                    label = { Text(interest) },
                    leadingIcon = if (isSelected) {
                        { Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp)) }
                    } else null
                )
            }
        }

        Button(
            onClick = {
                onCompleteProfile(
                    displayName,
                    username,
                    selectedGender,
                    selectedCountry,
                    selectedLanguage,
                    bio,
                    selectedInterests.toList()
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("complete_profile_button"),
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.buttonColors(containerColor = NeonPink)
        ) {
            Text(
                text = "Save & Start Matching",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
