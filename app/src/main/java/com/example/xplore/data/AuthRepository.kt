package com.example.xplore.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface AuthRepository {
    suspend fun login(username: String, password: String): Result<LoginResponse>
    suspend fun register(username: String, email: String, password: String): Result<LoginResponse>
    suspend fun refreshToken(): Result<TokenRefreshResponse>
    suspend fun logout()
}

class ApiAuthRepository @Inject constructor(
    private val authApiService: AuthApiService,
    private val tokenManager: TokenManager
): AuthRepository {

    override suspend fun login(username: String, password: String): Result<LoginResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val loginRequest = LoginRequest(username, password)
                val response = authApiService.login(loginRequest)

                if (response.isSuccessful) {
                    response.body()?.let { loginResponse ->
                        // Save tokens
                        tokenManager.saveAccessToken(loginResponse.tokens.access.token)
                        Result.success(loginResponse)
                    } ?: Result.failure(Exception("Empty response body"))
                } else {
                    // Parse error body if available
                    val errorBody = response.errorBody()?.string()
                    Result.failure(Exception(errorBody ?: "Login failed"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun register(username: String, email: String, password: String): Result<LoginResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val registerRequest = RegisterRequest(username, email, password)
                val response = authApiService.register(registerRequest)

                if (response.isSuccessful) {
                    response.body()?.let { loginResponse ->
                        // Save tokens
                        tokenManager.saveAccessToken(loginResponse.tokens.access.token)

                        Result.success(loginResponse)
                    } ?: Result.failure(Exception("Empty response body"))
                } else {
                    val errorBody = response.errorBody()?.string()
                    Result.failure(Exception(errorBody ?: "Registration failed"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun refreshToken(): Result<TokenRefreshResponse> {
        /*return withContext(Dispatchers.IO) {
            try {
                // Retrieve the stored refresh token
                val storedRefreshToken = tokenManager.getAccessToken()
                    ?: return@withContext Result.failure(Exception("No refresh token found"))

                val refreshRequest = RefreshTokenRequest(storedRefreshToken)
                val response = authApiService.refreshToken(refreshRequest)

                if (response.isSuccessful) {
                    response.body()?.let { tokenRefreshResponse ->
                        // Update stored tokens
                        tokenManager.saveAccessToken(tokenRefreshResponse.accessToken)
                        tokenManager.saveSecureToken("refresh_token", tokenRefreshResponse.refreshToken)

                        Result.success(tokenRefreshResponse)
                    } ?: Result.failure(Exception("Empty token refresh response"))
                } else {
                    // If refresh fails, likely need to re-login
                    Result.failure(Exception("Token refresh failed"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }*/
        TODO()

    }

    override suspend fun logout() {
        tokenManager.clearAccessToken()
    }
}




class FakeAuthRepository @Inject constructor(
    private val authApiService: AuthApiService,
    private val tokenManager: TokenManager
): AuthRepository {
    override suspend fun login(username: String, password: String): Result<LoginResponse> {
        val username = "fake@example.com"
        val password = "password1"
        return withContext(Dispatchers.IO) {
            try {
                val loginRequest = LoginRequest(username, password)
                val response = authApiService.login(loginRequest)

                if (response.isSuccessful) {
                    response.body()?.let { loginResponse ->
                        // Save tokens
                        tokenManager.saveAccessToken(loginResponse.tokens.access.token)
                        Result.success(loginResponse)
                    } ?: Result.failure(Exception("Empty response body"))
                } else {
                    // Parse error body if available
                    val errorBody = response.errorBody()?.string()
                    Result.failure(Exception(errorBody ?: "Login failed"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun register(username: String, email: String, password: String): Result<LoginResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val registerRequest = RegisterRequest(username, email, password)
                val response = authApiService.register(registerRequest)

                if (response.isSuccessful) {
                    response.body()?.let { loginResponse ->
                        // Save tokens
                        tokenManager.saveAccessToken(loginResponse.tokens.access.token)

                        Result.success(loginResponse)
                    } ?: Result.failure(Exception("Empty response body"))
                } else {
                    val errorBody = response.errorBody()?.string()
                    Result.failure(Exception(errorBody ?: "Registration failed"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun refreshToken(): Result<TokenRefreshResponse> {
        /*return withContext(Dispatchers.IO) {
            try {
                // Retrieve the stored refresh token
                val storedRefreshToken = tokenManager.getAccessToken()
                    ?: return@withContext Result.failure(Exception("No refresh token found"))

                val refreshRequest = RefreshTokenRequest(storedRefreshToken)
                val response = authApiService.refreshToken(refreshRequest)

                if (response.isSuccessful) {
                    response.body()?.let { tokenRefreshResponse ->
                        // Update stored tokens
                        tokenManager.saveAccessToken(tokenRefreshResponse.accessToken)
                        tokenManager.saveSecureToken("refresh_token", tokenRefreshResponse.refreshToken)

                        Result.success(tokenRefreshResponse)
                    } ?: Result.failure(Exception("Empty token refresh response"))
                } else {
                    // If refresh fails, likely need to re-login
                    Result.failure(Exception("Token refresh failed"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }*/
        TODO()

    }

    override suspend fun logout() {
        tokenManager.clearAccessToken()
    }
}


