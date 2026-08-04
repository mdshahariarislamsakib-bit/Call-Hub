package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.FriendEntity
import com.example.data.db.FriendRequestEntity
import com.example.ui.components.AvatarCircle
import com.example.ui.components.getFlagEmoji
import com.example.ui.theme.*

@Composable
fun FriendsScreen(
    friends: List<FriendEntity>,
    friendRequests: List<FriendRequestEntity>,
    onAcceptRequest: (FriendRequestEntity) -> Unit,
    onDeclineRequest: (FriendRequestEntity) -> Unit,
    onStartPrivateCall: (FriendEntity) -> Unit,
    onRemoveFriend: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) } // 0 = Friends, 1 = Requests

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp)
    ) {
        Text(
            text = "Friends & Connections",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimaryDark
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Tab Selector
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = DarkSurface,
            contentColor = NeonPink
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("My Friends (${friends.size})") }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    val pendingCount = friendRequests.count { it.status == "PENDING" }
                    Text("Requests (${pendingCount})")
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedTab == 0) {
            if (friends.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.PeopleOutline, null, tint = TextSecondaryDark, modifier = Modifier.size(56.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No friends added yet.", color = TextSecondaryDark)
                        Text("Add friends during video calls to stay connected!", fontSize = 12.sp, color = TextSecondaryDark)
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(friends) { friend ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("friend_card_${friend.friendUid}"),
                            colors = CardDefaults.cardColors(containerColor = DarkSurface),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box {
                                    AvatarCircle(name = friend.displayName, size = 48.dp)
                                    if (friend.isOnline) {
                                        Box(
                                            modifier = Modifier
                                                .size(12.dp)
                                                .clip(CircleShape)
                                                .background(NeonGreen)
                                                .align(Alignment.BottomEnd)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = friend.displayName,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimaryDark
                                    )
                                    Text(
                                        text = "${getFlagEmoji(friend.country)} ${friend.country} • ${friend.gender}",
                                        fontSize = 12.sp,
                                        color = TextSecondaryDark
                                    )
                                }

                                // 1:1 Private Call Button
                                Button(
                                    onClick = { onStartPrivateCall(friend) },
                                    shape = RoundedCornerShape(20.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    modifier = Modifier.testTag("private_call_button_${friend.friendUid}")
                                ) {
                                    Icon(Icons.Default.VideoCall, null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Call", fontSize = 12.sp)
                                }

                                IconButton(onClick = { onRemoveFriend(friend.friendUid) }) {
                                    Icon(Icons.Default.Delete, null, tint = TextSecondaryDark, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }
        } else {
            val pending = friendRequests.filter { it.status == "PENDING" }
            if (pending.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No pending friend requests.", color = TextSecondaryDark)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(pending) { req ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("friend_request_card_${req.id}"),
                            colors = CardDefaults.cardColors(containerColor = DarkSurface),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AvatarCircle(name = req.fromName, size = 44.dp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(req.fromName, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                                    Text("${getFlagEmoji(req.country)} ${req.country}", fontSize = 11.sp, color = TextSecondaryDark)
                                }

                                IconButton(
                                    onClick = { onAcceptRequest(req) },
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(NeonGreen)
                                ) {
                                    Icon(Icons.Default.Check, null, tint = Color.Black)
                                }

                                Spacer(modifier = Modifier.width(6.dp))

                                IconButton(
                                    onClick = { onDeclineRequest(req) },
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(DarkSurfaceVariant)
                                ) {
                                    Icon(Icons.Default.Close, null, tint = TextSecondaryDark)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
