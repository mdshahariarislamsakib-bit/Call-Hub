package com.example.data.repository

import com.example.data.model.MatchFilter
import com.example.data.model.StrangerProfile
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

object MatchmakingEngine {

    val SAMPLE_STRANGERS = listOf(
        StrangerProfile(
            uid = "stranger_101",
            displayName = "Sophia Chen",
            age = 23,
            gender = "Female",
            country = "USA",
            countryCode = "US",
            flagEmoji = "🇺🇸",
            preferredLanguage = "English",
            bio = "UX designer from Seattle! Love indie music & coffee ☕",
            interests = listOf("Music", "Design", "Gaming", "Travel"),
            avatarGradientStart = 0xFFFF2A85,
            avatarGradientEnd = 0xFF8A2BE2,
            networkQuality = "Excellent (8ms)",
            isVerified = true
        ),
        StrangerProfile(
            uid = "stranger_102",
            displayName = "Aarav Sharma",
            age = 24,
            gender = "Male",
            country = "India",
            countryCode = "IN",
            flagEmoji = "🇮🇳",
            preferredLanguage = "Hindi",
            bio = "Software dev & guitar enthusiast 🎸 Let's chat tech!",
            interests = listOf("Programming", "Music", "Anime", "Study"),
            avatarGradientStart = 0xFF00E5FF,
            avatarGradientEnd = 0xFF120C1F,
            networkQuality = "Good (16ms)",
            isVerified = true
        ),
        StrangerProfile(
            uid = "stranger_103",
            displayName = "Tariq Rahman",
            age = 22,
            gender = "Male",
            country = "Bangladesh",
            countryCode = "BD",
            flagEmoji = "🇧🇩",
            preferredLanguage = "Bengali",
            bio = "Photographer & travel lover 📸 Looking for creative minds!",
            interests = listOf("Travel", "Movies", "Music", "Gaming"),
            avatarGradientStart = 0xFF00FF88,
            avatarGradientEnd = 0xFF00E5FF,
            networkQuality = "Excellent (12ms)",
            isVerified = true
        ),
        StrangerProfile(
            uid = "stranger_104",
            displayName = "Emma Watson",
            age = 21,
            gender = "Female",
            country = "UK",
            countryCode = "GB",
            flagEmoji = "🇬🇧",
            preferredLanguage = "English",
            bio = "Literature student in London 📖 Love movies & tea!",
            interests = listOf("Movies", "Study", "Travel", "Music"),
            avatarGradientStart = 0xFFFFD700,
            avatarGradientEnd = 0xFFFF2A85,
            networkQuality = "Good (20ms)",
            isVerified = false
        ),
        StrangerProfile(
            uid = "stranger_105",
            displayName = "Yuki Tanaka",
            age = 25,
            gender = "Female",
            country = "Japan",
            countryCode = "JP",
            flagEmoji = "🇯🇵",
            preferredLanguage = "English",
            bio = "Game developer in Tokyo 👾 Let's talk about anime & games!",
            interests = listOf("Gaming", "Anime", "Programming", "Music"),
            avatarGradientStart = 0xFF8A2BE2,
            avatarGradientEnd = 0xFF00E5FF,
            networkQuality = "Excellent (9ms)",
            isVerified = true
        ),
        StrangerProfile(
            uid = "stranger_106",
            displayName = "Min-jun Kim",
            age = 22,
            gender = "Male",
            country = "Korea",
            countryCode = "KR",
            flagEmoji = "🇰🇷",
            preferredLanguage = "English",
            bio = "K-pop producer & street dancer 🕺 Let's vibe!",
            interests = listOf("Music", "Gaming", "Movies", "Travel"),
            avatarGradientStart = 0xFFFF2A85,
            avatarGradientEnd = 0xFF00FF88,
            networkQuality = "Excellent (10ms)",
            isVerified = true
        ),
        StrangerProfile(
            uid = "stranger_107",
            displayName = "Zainab Malik",
            age = 20,
            gender = "Female",
            country = "Pakistan",
            countryCode = "PK",
            flagEmoji = "🇵🇰",
            preferredLanguage = "Urdu",
            bio = "Medical student & foodie 🍕 Friendly chats only!",
            interests = listOf("Study", "Movies", "Travel", "Music"),
            avatarGradientStart = 0xFF00FF88,
            avatarGradientEnd = 0xFF8A2BE2,
            networkQuality = "Good (18ms)",
            isVerified = false
        ),
        StrangerProfile(
            uid = "stranger_108",
            displayName = "Lucas Silva",
            age = 26,
            gender = "Male",
            country = "USA",
            countryCode = "US",
            flagEmoji = "🇺🇸",
            preferredLanguage = "English",
            bio = "Fitness trainer & adventure seeker 🏋️‍♂️ Always positive vibes!",
            interests = listOf("Sports", "Travel", "Gaming", "Music"),
            avatarGradientStart = 0xFF00E5FF,
            avatarGradientEnd = 0xFFFF2A85,
            networkQuality = "Excellent (11ms)",
            isVerified = true
        )
    )

    fun findBestMatch(
        filter: MatchFilter,
        blockedUids: List<String>
    ): StrangerProfile {
        // Filter out blocked users
        val available = SAMPLE_STRANGERS.filter { it.uid !in blockedUids }

        // Score candidates based on filter requirements & shared interests
        val scored = available.map { candidate ->
            var score = 0
            if (filter.genderPreference != "Anyone" && candidate.gender.equals(filter.genderPreference, ignoreCase = true)) {
                score += 50
            } else if (filter.genderPreference == "Anyone") {
                score += 20
            }

            if (filter.countryPreference != "All Countries" && candidate.country.equals(filter.countryPreference, ignoreCase = true)) {
                score += 40
            } else if (filter.countryPreference == "All Countries") {
                score += 15
            }

            if (candidate.preferredLanguage.equals(filter.languagePreference, ignoreCase = true)) {
                score += 25
            }

            // Shared interests bonus
            val shared = candidate.interests.intersect(filter.selectedInterests)
            score += shared.size * 10

            candidate to score
        }.sortedByDescending { it.second }

        return scored.firstOrNull()?.first ?: available.random()
    }
}
