package com.example.data.repository

import com.example.data.db.CallHubDao
import com.example.data.db.FriendEntity
import com.example.data.db.FriendRequestEntity
import com.example.data.model.StrangerProfile
import kotlinx.coroutines.flow.Flow

class FriendRepository(private val dao: CallHubDao) {

    val friendsFlow: Flow<List<FriendEntity>> = dao.getAllFriends()
    val friendRequestsFlow: Flow<List<FriendRequestEntity>> = dao.getAllFriendRequests()

    suspend fun addFriendFromProfile(stranger: StrangerProfile) {
        val friend = FriendEntity(
            friendUid = stranger.uid,
            username = stranger.displayName.lowercase().replace(" ", "_"),
            displayName = stranger.displayName,
            photoUrl = "",
            gender = stranger.gender,
            age = stranger.age,
            country = stranger.country,
            preferredLanguage = stranger.preferredLanguage,
            bio = stranger.bio,
            interests = stranger.interests.joinToString(", "),
            isOnline = true
        )
        dao.insertFriend(friend)
    }

    suspend fun sendFriendRequest(stranger: StrangerProfile) {
        val request = FriendRequestEntity(
            fromUid = stranger.uid,
            fromName = stranger.displayName,
            photoUrl = "",
            gender = stranger.gender,
            country = stranger.country,
            status = "PENDING"
        )
        dao.insertFriendRequest(request)
    }

    suspend fun acceptFriendRequest(request: FriendRequestEntity) {
        dao.updateFriendRequestStatus(request.id, "ACCEPTED")
        val friend = FriendEntity(
            friendUid = request.fromUid,
            username = request.fromName.lowercase().replace(" ", "_"),
            displayName = request.fromName,
            photoUrl = request.photoUrl,
            gender = request.gender,
            age = 22,
            country = request.country,
            preferredLanguage = "English",
            bio = "Friend added from Call Hub matching",
            interests = "Gaming, Music",
            isOnline = true
        )
        dao.insertFriend(friend)
    }

    suspend fun declineFriendRequest(request: FriendRequestEntity) {
        dao.updateFriendRequestStatus(request.id, "DECLINED")
    }

    suspend fun removeFriend(friendUid: String) {
        dao.deleteFriend(friendUid)
    }

    suspend fun ensureSampleFriendData() {
        val sampleRequests = listOf(
            FriendRequestEntity(
                fromUid = "req_201",
                fromName = "Elena Rostova",
                photoUrl = "",
                gender = "Female",
                country = "USA",
                status = "PENDING"
            ),
            FriendRequestEntity(
                fromUid = "req_202",
                fromName = "Mateo Rossi",
                photoUrl = "",
                gender = "Male",
                country = "Italy",
                status = "PENDING"
            )
        )
        sampleRequests.forEach { dao.insertFriendRequest(it) }

        val sampleFriends = listOf(
            FriendEntity(
                friendUid = "stranger_101",
                username = "sophia_c",
                displayName = "Sophia Chen",
                photoUrl = "",
                gender = "Female",
                age = 23,
                country = "USA",
                preferredLanguage = "English",
                bio = "UX designer from Seattle! Love indie music",
                interests = "Music, Design, Gaming",
                isOnline = true
            ),
            FriendEntity(
                friendUid = "stranger_103",
                username = "tariq_r",
                displayName = "Tariq Rahman",
                photoUrl = "",
                gender = "Male",
                age = 22,
                country = "Bangladesh",
                preferredLanguage = "Bengali",
                bio = "Photographer & travel lover",
                interests = "Travel, Movies, Gaming",
                isOnline = true
            )
        )
        sampleFriends.forEach { dao.insertFriend(it) }
    }
}
