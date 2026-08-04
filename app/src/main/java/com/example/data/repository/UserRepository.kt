package com.example.data.repository

import com.example.data.db.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull

class UserRepository(private val dao: CallHubDao) {

    val userAccountFlow: Flow<UserAccountEntity?> = dao.getUserAccountFlow()
    val appSettingsFlow: Flow<AppSettingEntity?> = dao.getAppSettingsFlow()
    val blockedUsersFlow: Flow<List<BlockedUserEntity>> = dao.getBlockedUsersFlow()
    val reportLogsFlow: Flow<List<ReportLogEntity>> = dao.getAllReportLogs()
    val callHistoryFlow: Flow<List<CallHistoryEntity>> = dao.getCallHistory()

    suspend fun ensureDefaultUser() {
        val current = dao.getUserAccount()
        if (current == null) {
            val defaultUser = UserAccountEntity(
                uid = "local_user_1",
                username = "AlexRider",
                displayName = "Alex Rider",
                gender = "Male",
                age = 22,
                country = "USA",
                countryCode = "US",
                preferredLanguage = "English",
                bio = "Love meeting new friends around the world! 🌍✨",
                interests = "Gaming, Music, Travel, Programming",
                coins = 200,
                isPremium = false,
                loginType = "GUEST"
            )
            dao.insertUserAccount(defaultUser)
        }

        val settings = dao.getAppSettings()
        if (settings == null) {
            dao.insertAppSettings(AppSettingEntity())
        }
    }

    suspend fun updateProfile(
        displayName: String,
        username: String,
        gender: String,
        age: Int,
        country: String,
        preferredLanguage: String,
        bio: String,
        interests: List<String>
    ) {
        val current = dao.getUserAccount() ?: return
        val updated = current.copy(
            displayName = displayName,
            username = username,
            gender = gender,
            age = age,
            country = country,
            preferredLanguage = preferredLanguage,
            bio = bio,
            interests = interests.joinToString(", ")
        )
        dao.insertUserAccount(updated)
    }

    suspend fun updateLoginAccount(
        loginType: String,
        email: String = "",
        displayName: String = ""
    ) {
        val current = dao.getUserAccount() ?: return
        val updated = current.copy(
            loginType = loginType,
            email = if (email.isNotEmpty()) email else current.email,
            displayName = if (displayName.isNotEmpty()) displayName else current.displayName
        )
        dao.insertUserAccount(updated)
    }

    suspend fun spendCoins(amount: Int, reason: String): Boolean {
        val current = dao.getUserAccount() ?: return false
        if (current.coins < amount) return false

        dao.updateCoins(-amount)
        dao.insertCoinTransaction(
            CoinTransactionEntity(
                title = reason,
                amount = -amount,
                type = "SPEND"
            )
        )
        return true
    }

    suspend fun addCoins(amount: Int, reason: String, type: String = "REWARD") {
        dao.updateCoins(amount)
        dao.insertCoinTransaction(
            CoinTransactionEntity(
                title = reason,
                amount = amount,
                type = type
            )
        )
    }

    suspend fun setPremium(isPremium: Boolean) {
        dao.updatePremiumStatus(isPremium)
    }

    suspend fun blockUser(uid: String, name: String, photoUrl: String, reason: String = "In-call report") {
        dao.blockUser(BlockedUserEntity(blockedUid = uid, name = name, photoUrl = photoUrl, reason = reason))
    }

    suspend fun unblockUser(uid: String) {
        dao.unblockUser(uid)
    }

    suspend fun submitReport(
        reportedUid: String,
        reportedName: String,
        reason: String,
        details: String,
        callId: String,
        isChildSafetyEscalated: Boolean = false
    ) {
        val current = dao.getUserAccount()
        val report = ReportLogEntity(
            reporterUid = current?.uid ?: "local_user_1",
            reportedUid = reportedUid,
            reportedName = reportedName,
            reason = reason,
            details = details,
            callId = callId,
            isChildSafetyEscalated = isChildSafetyEscalated,
            status = if (isChildSafetyEscalated) "PRIORITY_ESCALATED" else "SUBMITTED"
        )
        dao.insertReportLog(report)

        // Block user automatically on report
        blockUser(reportedUid, reportedName, "", reason)
    }

    suspend fun saveCallHistory(
        matchId: String,
        peerUid: String,
        peerName: String,
        peerPhotoUrl: String,
        peerCountry: String,
        peerGender: String,
        peerAge: Int,
        durationSeconds: Int
    ) {
        dao.insertCallHistory(
            CallHistoryEntity(
                matchId = matchId,
                peerUid = peerUid,
                peerName = peerName,
                peerPhotoUrl = peerPhotoUrl,
                peerCountry = peerCountry,
                peerGender = peerGender,
                peerAge = peerAge,
                durationSeconds = durationSeconds
            )
        )
    }

    suspend fun updateSettings(settings: AppSettingEntity) {
        dao.insertAppSettings(settings)
    }
}
