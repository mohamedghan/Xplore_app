package com.example.xplore.ui.myauth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.xplore.data.AuthRepository
import com.example.xplore.data.LoginResponse
import com.example.xplore.data.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MyAuthViewModel @Inject constructor(
    private val myAuthRepository: AuthRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _loginState = MutableStateFlow<AuthState>(AuthState.Idle)
    val loginState: StateFlow<AuthState> = _loginState.asStateFlow()

    private val _signupState = MutableStateFlow<AuthState>(AuthState.Idle)
    val signupState: StateFlow<AuthState> = _signupState.asStateFlow()

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _loginState.value = AuthState.Loading
            try {
                val result = myAuthRepository.login(username, password)
                result.fold(
                    onSuccess = {
                        _loginState.value = AuthState.Success(it)
                    },
                    onFailure = {
                        _loginState.value = AuthState.Error(
                            "login failed"
                        )
                    }
                )
            } catch (e: Exception) {
                _loginState.value = AuthState.Error(
                    e.message ?: "An unexpected error occurred"
                )
            }
        }
    }

    fun register(username: String, email: String, password: String) {
        viewModelScope.launch {
            _signupState.value = AuthState.Loading
            try {
                val result = myAuthRepository.register(username, email, password)
                result.fold(
                    onSuccess = {
                        _signupState.value = AuthState.Success(it)
                    },
                    onFailure = {
                        _signupState.value = AuthState.Error(
                            "login failed"
                        )
                    }
                )
            } catch (e: Exception) {
                _signupState.value = AuthState.Error(
                    e.message ?: "An unexpected error occurred"
                )
            }
        }
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val data: LoginResponse) : AuthState()
    data class Error(val message: String) : AuthState()
}