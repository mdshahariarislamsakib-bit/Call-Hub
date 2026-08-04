package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import com.example.data.db.CoinTransactionEntity
import com.example.data.repository.CoinPackage
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun WalletScreen(
    currentCoins: Int,
    transactions: List<CoinTransactionEntity>,
    coinPackages: List<CoinPackage>,
    onClaimDailyReward: () -> Unit,
    onRedeemReferral: (String) -> Boolean,
    onPurchasePackage: (CoinPackage) -> Unit,
    modifier: Modifier = Modifier
) {
    var referralInput by remember { mutableStateOf("") }
    var referralMessage by remember { mutableStateOf<String?>(null) }
    var dailyClaimed by remember { mutableStateOf(false) }

    val sdf = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Coin Wallet & Store",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimaryDark
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Big Balance Header Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, GoldAccent, RoundedCornerShape(24.dp)),
            colors = CardDefaults.cardColors(containerColor = DarkSurface)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(listOf(DarkSurface, DarkSurfaceVariant))
                    )
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Current Balance", fontSize = 12.sp, color = TextSecondaryDark)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.MonetizationOn, null, tint = GoldAccent, modifier = Modifier.size(32.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("$currentCoins", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimaryDark)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Coins", fontSize = 14.sp, color = GoldAccent, fontWeight = FontWeight.Bold)
                        }
                    }

                    Button(
                        onClick = {
                            if (!dailyClaimed) {
                                onClaimDailyReward()
                                dailyClaimed = true
                            }
                        },
                        enabled = !dailyClaimed,
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = Color.Black),
                        modifier = Modifier.testTag("claim_daily_reward_button")
                    ) {
                        Text(if (dailyClaimed) "Claimed 🎁" else "Daily Reward +50", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Referral Code Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(18.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("🚀 Redeem Referral / Invite Code (+100 Coins)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = referralInput,
                        onValueChange = { referralInput = it },
                        placeholder = { Text("e.g. CALLHUB2026") },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("referral_code_input"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            val success = onRedeemReferral(referralInput.trim())
                            referralMessage = if (success) "Code redeemed successfully! +100 Coins" else "Invalid referral code."
                            referralInput = ""
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                        modifier = Modifier.testTag("redeem_referral_button")
                    ) {
                        Text("Redeem")
                    }
                }

                referralMessage?.let { msg ->
                    Text(msg, fontSize = 11.sp, color = NeonCyan, modifier = Modifier.padding(top = 6.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Coin Shop Packages
        Text("💰 Buy Coin Packages (Google Play Billing)", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)

        Spacer(modifier = Modifier.height(10.dp))

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            coinPackages.forEach { pkg ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("coin_package_${pkg.id}"),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(16.dp),
                    border = if (pkg.isPopular) androidx.compose.foundation.BorderStroke(1.5.dp, GoldAccent) else null
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.MonetizationOn, null, tint = GoldAccent, modifier = Modifier.size(28.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("${pkg.coinAmount} Coins", fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                                    if (pkg.bonusCoins > 0) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(shape = RoundedCornerShape(8.dp), color = NeonPink) {
                                            Text("+${pkg.bonusCoins} Bonus", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                        }
                                    }
                                }
                                if (pkg.isPopular) {
                                    Text("⭐ Best Value Pack", fontSize = 10.sp, color = GoldAccent)
                                }
                            }
                        }

                        Button(
                            onClick = { onPurchasePackage(pkg) },
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black),
                            modifier = Modifier.testTag("buy_pack_button_${pkg.id}")
                        ) {
                            Text(pkg.priceUsd, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Transaction History Log
        Text("📜 Coin Transaction History", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)

        Spacer(modifier = Modifier.height(10.dp))

        if (transactions.isEmpty()) {
            Text("No transactions yet.", fontSize = 12.sp, color = TextSecondaryDark)
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                transactions.take(5).forEach { tx ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(tx.title, fontSize = 13.sp, color = TextPrimaryDark)
                            Text(sdf.format(Date(tx.timestamp)), fontSize = 10.sp, color = TextSecondaryDark)
                        }
                        Text(
                            text = if (tx.amount > 0) "+${tx.amount}" else "${tx.amount}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (tx.amount > 0) NeonGreen else NeonPink
                        )
                    }
                }
            }
        }
    }
}
