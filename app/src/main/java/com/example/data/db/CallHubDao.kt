package com.example.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CallHubDao {
    // User Account
    @Query("SELECT * FROM user_account WHERE uid = :uid LIMIT 1")
    fun getUserAccountFlow(uid: String = "local_user_1"): Flow<UserAccountEntity?>

    @Query("SELECT * FROM user_account WHERE uid = :uid LIMIT 1")
    suspend fun getUserAccount(uid: String = "local_user_1"): UserAccountEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserAccount(user: UserAccountEntity)

    @Query("UPDATE user_account SET coins = coins + :delta WHERE uid = :uid")
    suspend fun updateCoins(delta: Int, uid: String = "local_user_1")

    @Query("UPDATE user_account SET isPremium = :isPremium WHERE uid = :uid")
    suspend fun updatePremiumStatus(isPremium: Boolean, uid: String = "local_user_1")

    // Friends
    @Query("SELECT * FROM friends ORDER BY addedAt DESC")
    fun getAllFriends(): Flow<List<FriendEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFriend(friend: FriendEntity)

    @Query("DELETE FROM friends WHERE friendUid = :friendUid")
    suspend fun deleteFriend(friendUid: String)

    // Friend Requests
    @Query("SELECT * FROM friend_requests ORDER BY timestamp DESC")
    fun getAllFriendRequests(): Flow<List<FriendRequestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFriendRequest(request: FriendRequestEntity)

    @Query("UPDATE friend_requests SET status = :status WHERE id = :requestId")
    suspend fun updateFriendRequestStatus(requestId: Int, status: String)

    // Call History
    @Query("SELECT * FROM call_history ORDER BY timestamp DESC")
    fun getCallHistory(): Flow<List<CallHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCallHistory(history: CallHistoryEntity)

    // Blocked Users
    @Query("SELECT * FROM blocked_users")
    fun getBlockedUsersFlow(): Flow<List<BlockedUserEntity>>

    @Query("SELECT blockedUid FROM blocked_users")
    suspend fun getBlockedUids(): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun blockUser(blockedUser: BlockedUserEntity)

    @Query("DELETE FROM blocked_users WHERE blockedUid = :blockedUid")
    suspend fun unblockUser(blockedUid: String)

    // Report Logs
    @Query("SELECT * FROM report_logs ORDER BY timestamp DESC")
    fun getAllReportLogs(): Flow<List<ReportLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReportLog(report: ReportLogEntity)

    @Query("UPDATE report_logs SET status = :status WHERE id = :id")
    suspend fun updateReportStatus(id: Int, status: String)

    // Coin Transactions
    @Query("SELECT * FROM coin_transactions ORDER BY timestamp DESC")
    fun getCoinTransactions(): Flow<List<CoinTransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoinTransaction(transaction: CoinTransactionEntity)

    // App Settings
    @Query("SELECT * FROM app_settings WHERE id = 1 LIMIT 1")
    fun getAppSettingsFlow(): Flow<AppSettingEntity?>

    @Query("SELECT * FROM app_settings WHERE id = 1 LIMIT 1")
    suspend fun getAppSettings(): AppSettingEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppSettings(settings: AppSettingEntity)
}
