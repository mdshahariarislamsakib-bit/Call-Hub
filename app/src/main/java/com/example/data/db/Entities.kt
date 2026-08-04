package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_account")
data class UserAccountEntity(
    @PrimaryKey val uid: String = "local_user_1",
    val username: String = "AlexRider",
    val displayName: String = "Alex Rider",
    val photoUrl: String = "",
    val gender: String = "Male", // Male, Female, Other
    val age: Int = 22,
    val dobYear: Int = 2004,
    val dobMonth: Int = 5,
    val dobDay: Int = 15,
    val country: String = "USA",
    val countryCode: String = "US",
    val preferredLanguage: String = "English",
    val bio: String = "Love meeting new friends around the world! 🌍✨",
    val interests: String = "Gaming, Music, Travel, Programming", // comma separated
    val coins: Int = 150,
    val isPremium: Boolean = false,
    val loginType: String = "GUEST", // GUEST, EMAIL, GOOGLE, PHONE
    val email: String = "alex@callhub.io",
    val isOnline: Boolean = true
)

@Entity(tableName = "friends")
data class FriendEntity(
    @PrimaryKey val friendUid: String,
    val username: String,
    val displayName: String,
    val photoUrl: String,
    val gender: String,
    val age: Int,
    val country: String,
    val preferredLanguage: String,
    val bio: String,
    val interests: String,
    val isOnline: Boolean = true,
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "friend_requests")
data class FriendRequestEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val fromUid: String,
    val fromName: String,
    val photoUrl: String,
    val gender: String,
    val country: String,
    val status: String = "PENDING", // PENDING, ACCEPTED, DECLINED
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "call_history")
data class CallHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val matchId: String,
    val peerUid: String,
    val peerName: String,
    val peerPhotoUrl: String,
    val peerCountry: String,
    val peerGender: String,
    val peerAge: Int,
    val durationSeconds: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val callType: String = "RANDOM" // RANDOM, PRIVATE
)

@Entity(tableName = "blocked_users")
data class BlockedUserEntity(
    @PrimaryKey val blockedUid: String,
    val name: String,
    val photoUrl: String,
    val reason: String = "User blocked from call",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "report_logs")
data class ReportLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val reporterUid: String,
    val reportedUid: String,
    val reportedName: String,
    val reason: String,
    val details: String,
    val callId: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isChildSafetyEscalated: Boolean = false,
    val status: String = "SUBMITTED" // SUBMITTED, UNDER_REVIEW, RESOLVED_BANNED, RESOLVED_DISMISSED
)

@Entity(tableName = "coin_transactions")
data class CoinTransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val amount: Int, // Positive for gain, negative for spend
    val type: String, // DAILY_REWARD, REFERRAL, PURCHASE, GENDER_FILTER_UNLOCK
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "app_settings")
data class AppSettingEntity(
    @PrimaryKey val id: Int = 1,
    val isDarkMode: Boolean = true,
    val autoMatch: Boolean = false,
    val autoSkip: Boolean = false,
    val defaultCameraFront: Boolean = true,
    val defaultMicEnabled: Boolean = true,
    val dataSaverMode: Boolean = false,
    val autoTranslateChat: Boolean = true,
    val faceDetectionRequired: Boolean = true,
    val appLanguage: String = "English"
)
