package com.example.xplore.ui.myfeed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.xplore.data.AuthRepository
import com.example.xplore.data.JourneyResponse
import com.example.xplore.data.JourneysResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.example.xplore.data.MyFeedRepository
import com.example.xplore.ui.myfeed.MyFeedUiState.Error
import com.example.xplore.ui.myfeed.MyFeedUiState.Loading
import com.example.xplore.ui.myfeed.MyFeedUiState.Success
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class MyFeedViewModel @Inject constructor(
    private val myFeedRepository: MyFeedRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val refreshTrigger = MutableStateFlow(0)

    val uiState: StateFlow<MyFeedUiState> = myFeedRepository.myFeeds
        .map<JourneysResponse, MyFeedUiState>({ e ->
            Success(e.results, e.myuid)
        })
        .catch {
            emit(Error(it))
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Loading)

    fun likeMyFeed(post: JourneyResponse) {
        viewModelScope.launch {
            myFeedRepository.like(post)
        }
    }

    fun dislikeMyFeed(post: JourneyResponse) {
        viewModelScope.launch {
            myFeedRepository.dislike(post)
        }
    }

    fun reload() {
        refreshTrigger.value += 1;
    }

    fun logout() {
        runBlocking {
            authRepository.logout()
        }
    }
}


sealed interface MyFeedUiState {
    data object Loading : MyFeedUiState
    data class Error(val throwable: Throwable) : MyFeedUiState
    data class Success(val data: List<JourneyResponse>, val myid: String) : MyFeedUiState
}
