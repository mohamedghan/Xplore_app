package com.example.xplore.data

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface AuthApiService {
    @POST("v1/auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @POST("v1/auth/register")
    suspend fun register(@Body registerRequest: RegisterRequest): Response<LoginResponse>

    @POST("v1/auth/refresh-tokens")
    suspend fun refreshToken(@Body refreshTokenRequest: RefreshTokenRequest): Response<TokenRefreshResponse>

}

interface JourneysApiService {
    @GET("v1/journeys")
    suspend fun getJourneys(): Response<JourneysResponse>

    @POST("v1/journeys")
    suspend fun createJourney(@Body journey: JourneyRequest): Response<JourneyResponse>

    @POST("v1/journeys/{id}/like")
    suspend fun likeJourney(@Path("id") journey: String): Response<JourneyResponse>

    @DELETE("v1/journeys/{id}/like")
    suspend fun dislikeJourney(@Path("id") journey: String): Response<JourneyResponse>
}

data class JourneysResponse(
    val results: List<JourneyResponse>,
    val page: Number,
    val limit: Number,
    val totalPages: Number,
    val totalResults: Number,
    val myuid: String

)

data class JourneyRequest(
    val from: Point,
    val to: Point,
    val caption: String,
    val figure: String,
    val locations: List<Location>,
)

data class JourneyResponse(
    val from: Point,
    val to: Point,
    val caption: String,
    val figure: String,
    val user: User,
    val locations: List<Location>,
    val likedBy: List<String>,
    val id: String
)

data class Point(
    val type: String="Point",
    val coordinates: List<Double>
)

data class Location(
    val coordinates: Point? = null,
    val href: String? = ""
)

// Request Data Classes
data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)

data class RefreshTokenRequest(
    val refreshToken: String
)

// Response Data Classes
data class TokenRefreshResponse(
    val accessToken: String,
    val refreshToken: String
)
//TODO: fix refresh token response
/*
{
    "access": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI1ZWJhYzUzNDk1NGI1NDEzOTgwNmMxMTIiLCJpYXQiOjE1ODkyOTg0ODQsImV4cCI6MTU4OTMwMDI4NH0.m1U63blB0MLej_WfB7yC2FTMnCziif9X8yzwDEfJXAg",
    "expires": "2020-05-12T16:18:04.793Z"
},
    "refresh": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI1ZWJhYzUzNDk1NGI1NDEzOTgwNmMxMTIiLCJpYXQiOjE1ODkyOTg0ODQsImV4cCI6MTU4OTMwMDI4NH0.m1U63blB0MLej_WfB7yC2FTMnCziif9X8yzwDEfJXAg",
    "expires": "2020-05-12T16:18:04.793Z"
}
}*/

data class User(
    val role: String,
    val isEmailVerified: Boolean,
    val name: String,
    val email: String,
    val id: String,
    val avatar: String,
)

data class Token(
    val token: String,
    val expires: String,
)

data class Tokens(
    val access: Token,
    val refresh: Token,
)

data class LoginResponse(
    val user: User,
    val tokens: Tokens
)