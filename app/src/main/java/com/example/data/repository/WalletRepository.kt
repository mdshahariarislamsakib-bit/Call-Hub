package com.example.data.repository

import com.example.data.db.CallHubDao
import com.example.data.db.CoinTransactionEntity
import kotlinx.coroutines.flow.Flow

data class CoinPackage(
    val id: String,
    val coinAmount: Int,
    val bonusCoins: Int,
    val priceUsd: String,
    val isPopular: Boolean = false
)

class WalletRepository(private val dao: CallHubDao) {

    val coinTransactionsFlow: Flow<List<CoinTransactionEntity>> = dao.getCoinTransactions()

    val availablePackages = listOf(
        CoinPackage("pkg_100", 100, 0, "$0.99"),
        CoinPackage("pkg_500", 500, 50, "$3.99", isPopular = true),
        CoinPackage("pkg_1200", 1200, 200, "$8.99"),
        CoinPackage("pkg_3000", 3000, 600, "$19.99")
    )

    suspend fun claimDailyReward(): Int {
        val rewardCoins = 50
        dao.updateCoins(rewardCoins)
        dao.insertCoinTransaction(
            CoinTransactionEntity(
                title = "Daily Login Reward 🎁",
                amount = rewardCoins,
                type = "DAILY_REWARD"
            )
        )
        return rewardCoins
    }

    suspend fun redeemReferralCode(code: String): Boolean {
        if (code.isBlank() || code.length < 4) return false
        val rewardCoins = 100
        dao.updateCoins(rewardCoins)
        dao.insertCoinTransaction(
            CoinTransactionEntity(
                title = "Referral Code Bonus ($code) 🚀",
                amount = rewardCoins,
                type = "REFERRAL"
            )
        )
        return true
    }

    suspend fun purchasePackage(pkg: CoinPackage) {
        val totalCoins = pkg.coinAmount + pkg.bonusCoins
        dao.updateCoins(totalCoins)
        dao.insertCoinTransaction(
            CoinTransactionEntity(
                title = "Purchased ${pkg.coinAmount} Coins Pack (${pkg.priceUsd})",
                amount = totalCoins,
                type = "PURCHASE"
            )
        )
    }
}
