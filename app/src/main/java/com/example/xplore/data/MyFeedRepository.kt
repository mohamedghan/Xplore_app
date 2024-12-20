package com.example.xplore.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

interface MyFeedRepository {
    val myFeeds: Flow<JourneysResponse>
    suspend fun like(post: JourneyResponse)
    suspend fun dislike(post: JourneyResponse)
    suspend fun addJourney(post: JourneyRequest)
}

class DefaultMyFeedRepository @Inject constructor(
    private val journeysApi: JourneysApiService
) : MyFeedRepository {

    override val myFeeds: Flow<JourneysResponse> = flow {
        while (true) {
            try {
                val response = journeysApi.getJourneys()
                if (response.isSuccessful) {
                    response.body()?.let { journeysResponse ->
                        emit(journeysResponse)
                    } ?: throw Exception("Empty response body")
                } else {
                    throw Exception("API error: ${response.code()}")
                }
            } catch (e: Exception) {
                throw Exception("Error fetching journeys: ${e.message}")
            }

            delay(5000)
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun like(post: JourneyResponse) {
      journeysApi.likeJourney(post.id)
    }

    override suspend fun dislike(post: JourneyResponse) {
        journeysApi.dislikeJourney(post.id)
    }

    override suspend fun addJourney(post: JourneyRequest) {
       journeysApi.createJourney(post)
    }
}

