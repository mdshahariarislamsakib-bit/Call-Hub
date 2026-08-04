package com.example.data.model

data class MatchFilter(
    val genderPreference: String = "Anyone", // Anyone, Male, Female
    val countryPreference: String = "All Countries", // All Countries, Bangladesh, India, Pakistan, USA, UK, Japan, Korea
    val languagePreference: String = "English",
    val selectedInterests: Set<String> = setOf("Gaming", "Music", "Travel")
)

data class StrangerProfile(
    val uid: String,
    val displayName: String,
    val age: Int,
    val gender: String,
    val country: String,
    val countryCode: String,
    val flagEmoji: String,
    val preferredLanguage: String,
    val bio: String,
    val interests: List<String>,
    val avatarGradientStart: Long,
    val avatarGradientEnd: Long,
    val networkQuality: String = "Good (14ms)",
    val isVerified: Boolean = false,
    val bioPrompt: String = ""
)

data class InCallChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val senderUid: String,
    val senderName: String,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val translatedText: String? = null,
    val isSystem: Boolean = false
)

sealed interface MatchState {
    object Idle : MatchState
    data class Searching(
        val seconds: Int,
        val filterSummary: String,
        val foundCandidatesCount: Int
    ) : MatchState
    data class Connecting(
        val matchId: String,
        val peer: StrangerProfile
    ) : MatchState
    data class Connected(
        val matchId: String,
        val channelName: String,
        val peer: StrangerProfile,
        val connectedAt: Long = System.currentTimeMillis()
    ) : MatchState
}
