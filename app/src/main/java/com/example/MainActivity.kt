package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.data.db.AppDatabase
import com.example.data.db.AppSettingEntity
import com.example.data.db.UserAccountEntity
import com.example.data.model.MatchFilter
import com.example.data.model.StrangerProfile
import com.example.data.repository.*
import com.example.ui.screens.*
import com.example.ui.theme.CallHubTheme
import kotlinx.coroutines.launch

enum class AppNavScreen {
    SPLASH,
    AUTH,
    ONBOARDING,
    MAIN_TABS,
    MATCHMAKING,
    CALL,
    ADMIN
}

class MainActivity : ComponentActivity() {

    private lateinit var database: AppDatabase
    private lateinit var userRepository: UserRepository
    private lateinit var friendRepository: FriendRepository
    private lateinit var walletRepository: WalletRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        database = AppDatabase.getInstance(applicationContext)
        userRepository = UserRepository(database.callHubDao())
        friendRepository = FriendRepository(database.callHubDao())
        walletRepository = WalletRepository(database.callHubDao())

        // Initialize default user and sample friend data
        lifecycleScope.launch {
            userRepository.ensureDefaultUser()
            friendRepository.ensureSampleFriendData()
        }

        setContent {
            CallHubApp(
                database = database,
                userRepository = userRepository,
                friendRepository = friendRepository,
                walletRepository = walletRepository
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CallHubApp(
    database: AppDatabase,
    userRepository: UserRepository,
    friendRepository: FriendRepository,
    walletRepository: WalletRepository
) {
    val coroutineScope = rememberCoroutineScope()

    val userAccount by userRepository.userAccountFlow.collectAsStateWithLifecycle(initialValue = null)
    val appSettings by userRepository.appSettingsFlow.collectAsStateWithLifecycle(initialValue = null)
    val friends by friendRepository.friendsFlow.collectAsStateWithLifecycle(initialValue = emptyList())
    val friendRequests by friendRepository.friendRequestsFlow.collectAsStateWithLifecycle(initialValue = emptyList())
    val callHistory by userRepository.callHistoryFlow.collectAsStateWithLifecycle(initialValue = emptyList())
    val blockedUsers by userRepository.blockedUsersFlow.collectAsStateWithLifecycle(initialValue = emptyList())
    val reportLogs by userRepository.reportLogsFlow.collectAsStateWithLifecycle(initialValue = emptyList())
    val transactions by walletRepository.coinTransactionsFlow.collectAsStateWithLifecycle(initialValue = emptyList())

    var currentNavScreen by remember { mutableStateOf(AppNavScreen.SPLASH) }
    var selectedBottomTab by remember { mutableIntStateOf(0) }

    var matchFilter by remember { mutableStateOf(MatchFilter()) }
    var activeCallPeer by remember { mutableStateOf<StrangerProfile?>(null) }
    var activeCallMatchId by remember { mutableStateOf("") }

    val user = userAccount ?: UserAccountEntity()
    val settings = appSettings ?: AppSettingEntity()

    CallHubTheme(darkTheme = settings.isDarkMode) {
        Scaffold(
            bottomBar = {
                if (currentNavScreen == AppNavScreen.MAIN_TABS) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.testTag("bottom_navigation_bar")
                    ) {
                        NavigationBarItem(
                            selected = selectedBottomTab == 0,
                            onClick = { selectedBottomTab = 0 },
                            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                            label = { Text("Home") },
                            modifier = Modifier.testTag("tab_home")
                        )
                        NavigationBarItem(
                            selected = selectedBottomTab == 1,
                            onClick = { selectedBottomTab = 1 },
                            icon = { Icon(Icons.Default.People, contentDescription = "Friends") },
                            label = { Text("Friends") },
                            modifier = Modifier.testTag("tab_friends")
                        )
                        NavigationBarItem(
                            selected = selectedBottomTab == 2,
                            onClick = { selectedBottomTab = 2 },
                            icon = { Icon(Icons.Default.History, contentDescription = "History") },
                            label = { Text("History") },
                            modifier = Modifier.testTag("tab_history")
                        )
                        NavigationBarItem(
                            selected = selectedBottomTab == 3,
                            onClick = { selectedBottomTab = 3 },
                            icon = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = "Wallet") },
                            label = { Text("Wallet") },
                            modifier = Modifier.testTag("tab_wallet")
                        )
                        NavigationBarItem(
                            selected = selectedBottomTab == 4,
                            onClick = { selectedBottomTab = 4 },
                            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                            label = { Text("Profile") },
                            modifier = Modifier.testTag("tab_profile")
                        )
                    }
                }
            }
        ) { innerPadding ->
            Crossfade(
                targetState = currentNavScreen,
                modifier = Modifier.padding(innerPadding),
                label = "screen_transition"
            ) { screen ->
                when (screen) {
                    AppNavScreen.SPLASH -> {
                        SplashScreen(
                            onSplashFinished = {
                                currentNavScreen = AppNavScreen.AUTH
                            }
                        )
                    }
                    AppNavScreen.AUTH -> {
                        AuthScreen(
                            onLoginSuccess = { loginType, email ->
                                coroutineScope.launch {
                                    userRepository.updateLoginAccount(loginType, email)
                                }
                                currentNavScreen = AppNavScreen.ONBOARDING
                            },
                            onGuestMode = {
                                coroutineScope.launch {
                                    userRepository.updateLoginAccount("GUEST", "guest@callhub.io")
                                }
                                currentNavScreen = AppNavScreen.MAIN_TABS
                            }
                        )
                    }
                    AppNavScreen.ONBOARDING -> {
                        OnboardingScreen(
                            onCompleteProfile = { displayName, username, gender, country, language, bio, interests ->
                                coroutineScope.launch {
                                    userRepository.updateProfile(
                                        displayName = displayName,
                                        username = username,
                                        gender = gender,
                                        age = user.age,
                                        country = country,
                                        preferredLanguage = language,
                                        bio = bio,
                                        interests = interests
                                    )
                                }
                                currentNavScreen = AppNavScreen.MAIN_TABS
                            }
                        )
                    }
                    AppNavScreen.MAIN_TABS -> {
                        when (selectedBottomTab) {
                            0 -> HomeScreen(
                                user = user,
                                filter = matchFilter,
                                onFilterChanged = { matchFilter = it },
                                onStartMatching = {
                                    currentNavScreen = AppNavScreen.MATCHMAKING
                                },
                                onOpenWallet = { selectedBottomTab = 3 },
                                onOpenProfile = { selectedBottomTab = 4 },
                                onUnlockGenderFilter = {
                                    coroutineScope.launch {
                                        userRepository.spendCoins(20, "Gender Filter Match Unlock")
                                    }
                                }
                            )
                            1 -> FriendsScreen(
                                friends = friends,
                                friendRequests = friendRequests,
                                onAcceptRequest = { req ->
                                    coroutineScope.launch { friendRepository.acceptFriendRequest(req) }
                                },
                                onDeclineRequest = { req ->
                                    coroutineScope.launch { friendRepository.declineFriendRequest(req) }
                                },
                                onStartPrivateCall = { friend ->
                                    activeCallPeer = StrangerProfile(
                                        uid = friend.friendUid,
                                        displayName = friend.displayName,
                                        age = friend.age,
                                        gender = friend.gender,
                                        country = friend.country,
                                        countryCode = "US",
                                        flagEmoji = "🌐",
                                        preferredLanguage = friend.preferredLanguage,
                                        bio = friend.bio,
                                        interests = friend.interests.split(", "),
                                        avatarGradientStart = 0xFFFF2A85,
                                        avatarGradientEnd = 0xFF00E5FF
                                    )
                                    activeCallMatchId = "private_${System.currentTimeMillis()}"
                                    currentNavScreen = AppNavScreen.CALL
                                },
                                onRemoveFriend = { uid ->
                                    coroutineScope.launch { friendRepository.removeFriend(uid) }
                                }
                            )
                            2 -> HistoryScreen(
                                callHistory = callHistory,
                                onRematchPeer = { historyItem ->
                                    activeCallPeer = StrangerProfile(
                                        uid = historyItem.peerUid,
                                        displayName = historyItem.peerName,
                                        age = historyItem.peerAge,
                                        gender = historyItem.peerGender,
                                        country = historyItem.peerCountry,
                                        countryCode = "US",
                                        flagEmoji = "🌐",
                                        preferredLanguage = "English",
                                        bio = "Previous match on Call Hub",
                                        interests = listOf("Gaming", "Music"),
                                        avatarGradientStart = 0xFF8A2BE2,
                                        avatarGradientEnd = 0xFFFF2A85
                                    )
                                    activeCallMatchId = "rematch_${System.currentTimeMillis()}"
                                    currentNavScreen = AppNavScreen.CALL
                                }
                            )
                            3 -> WalletScreen(
                                currentCoins = user.coins,
                                transactions = transactions,
                                coinPackages = walletRepository.availablePackages,
                                onClaimDailyReward = {
                                    coroutineScope.launch { walletRepository.claimDailyReward() }
                                },
                                onRedeemReferral = { code ->
                                    var result = false
                                    coroutineScope.launch {
                                        result = walletRepository.redeemReferralCode(code)
                                    }
                                    result
                                },
                                onPurchasePackage = { pkg ->
                                    coroutineScope.launch { walletRepository.purchasePackage(pkg) }
                                }
                            )
                            4 -> ProfileScreen(
                                user = user,
                                settings = settings,
                                blockedUsers = blockedUsers,
                                onUpdateProfile = { displayName, username, gender, age, country, language, bio, interests ->
                                    coroutineScope.launch {
                                        userRepository.updateProfile(
                                            displayName, username, gender, age, country, language, bio, interests
                                        )
                                    }
                                },
                                onUpdateSettings = { newSettings ->
                                    coroutineScope.launch {
                                        userRepository.updateSettings(newSettings)
                                    }
                                },
                                onUnblockUser = { uid ->
                                    coroutineScope.launch { userRepository.unblockUser(uid) }
                                },
                                onOpenAdminPanel = {
                                    currentNavScreen = AppNavScreen.ADMIN
                                }
                            )
                        }
                    }
                    AppNavScreen.MATCHMAKING -> {
                        val peer = remember(matchFilter) {
                            MatchmakingEngine.findBestMatch(matchFilter, blockedUsers.map { it.blockedUid })
                        }
                        MatchmakingScreen(
                            filter = matchFilter,
                            matchedPeer = peer,
                            onMatchFound = {
                                activeCallPeer = peer
                                activeCallMatchId = "match_${System.currentTimeMillis()}"
                                currentNavScreen = AppNavScreen.CALL
                            },
                            onCancelMatching = {
                                currentNavScreen = AppNavScreen.MAIN_TABS
                            }
                        )
                    }
                    AppNavScreen.CALL -> {
                        activeCallPeer?.let { peer ->
                            CallScreen(
                                peer = peer,
                                onSkipCall = {
                                    coroutineScope.launch {
                                        userRepository.saveCallHistory(
                                            matchId = activeCallMatchId,
                                            peerUid = peer.uid,
                                            peerName = peer.displayName,
                                            peerPhotoUrl = "",
                                            peerCountry = peer.country,
                                            peerGender = peer.gender,
                                            peerAge = peer.age,
                                            durationSeconds = 15
                                        )
                                    }
                                    // Immediately re-queue / re-match
                                    currentNavScreen = AppNavScreen.MATCHMAKING
                                },
                                onEndCall = {
                                    coroutineScope.launch {
                                        userRepository.saveCallHistory(
                                            matchId = activeCallMatchId,
                                            peerUid = peer.uid,
                                            peerName = peer.displayName,
                                            peerPhotoUrl = "",
                                            peerCountry = peer.country,
                                            peerGender = peer.gender,
                                            peerAge = peer.age,
                                            durationSeconds = 30
                                        )
                                    }
                                    currentNavScreen = AppNavScreen.MAIN_TABS
                                },
                                onAddFriend = { targetPeer ->
                                    coroutineScope.launch {
                                        friendRepository.addFriendFromProfile(targetPeer)
                                    }
                                },
                                onReportUser = { reason, details, isChildSafety ->
                                    coroutineScope.launch {
                                        userRepository.submitReport(
                                            reportedUid = peer.uid,
                                            reportedName = peer.displayName,
                                            reason = reason,
                                            details = details,
                                            callId = activeCallMatchId,
                                            isChildSafetyEscalated = isChildSafety
                                        )
                                    }
                                    currentNavScreen = AppNavScreen.MAIN_TABS
                                },
                                onBlockUser = { targetPeer ->
                                    coroutineScope.launch {
                                        userRepository.blockUser(targetPeer.uid, targetPeer.displayName, "")
                                    }
                                    currentNavScreen = AppNavScreen.MAIN_TABS
                                }
                            )
                        }
                    }
                    AppNavScreen.ADMIN -> {
                        AdminScreen(
                            reportLogs = reportLogs,
                            onUpdateReportStatus = { id, status ->
                                coroutineScope.launch {
                                    database.callHubDao().updateReportStatus(id, status)
                                }
                            },
                            onBack = {
                                currentNavScreen = AppNavScreen.MAIN_TABS
                            }
                        )
                    }
                }
            }
        }
    }
}
