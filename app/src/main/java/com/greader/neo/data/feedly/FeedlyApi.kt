package com.greader.neo.data.feedly

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Cliente Feedly v3 (endpoints mapeados do app original — Relatório 02).
 *
 * Autenticação: `Authorization: OAuth <access_token>`.
 * O fluxo OAuth2 completo (authorize + troca de código) será implementado
 * na próxima fase com app registrado próprio (nunca reutilizar credenciais do original).
 */
interface FeedlyApi {

    @GET("v3/profile")
    suspend fun profile(
        @Header("Authorization") authorization: String,
    ): FeedlyProfile

    @GET("v3/subscriptions")
    suspend fun subscriptions(
        @Header("Authorization") authorization: String,
    ): List<FeedlySubscription>

    @GET("v3/tags")
    suspend fun tags(
        @Header("Authorization") authorization: String,
    ): List<FeedlyTag>

    @GET("v3/streams/contents")
    suspend fun streamContents(
        @Header("Authorization") authorization: String,
        @Query("streamId") streamId: String,
        @Query("count") count: Int = 1000,
        @Query("newerThan") newerThan: Long? = null,
        @Query("continuation") continuation: String? = null,
        @Query("unreadOnly") unreadOnly: Boolean? = null,
    ): FeedlyStreamResponse

    @GET("v3/streams/ids")
    suspend fun streamIds(
        @Header("Authorization") authorization: String,
        @Query("streamId") streamId: String,
        @Query("count") count: Int = 10000,
        @Query("newerThan") newerThan: Long? = null,
        @Query("unreadOnly") unreadOnly: Boolean? = null,
    ): FeedlyIdsResponse

    @POST("v3/markers")
    suspend fun markers(
        @Header("Authorization") authorization: String,
        @Body body: FeedlyMarkerRequest,
    ): Response<ResponseBody>

    @GET("v3/search/feeds")
    suspend fun searchFeeds(
        @Header("Authorization") authorization: String,
        @Query("query") query: String,
        @Query("count") count: Int = 20,
    ): FeedlySearchResponse

    @POST("v3/subscriptions")
    suspend fun addSubscription(
        @Header("Authorization") authorization: String,
        @Body body: FeedlySubscriptionRequest,
    ): Response<ResponseBody>

    @POST("v3/subscriptions")
    suspend fun updateSubscription(
        @Header("Authorization") authorization: String,
        @Body body: FeedlySubscriptionUpdate,
    ): Response<ResponseBody>

    @DELETE("v3/subscriptions/{feedId}")
    suspend fun deleteSubscription(
        @Header("Authorization") authorization: String,
        @Path("feedId") feedId: String,
    ): Response<ResponseBody>

    @FormUrlEncoded
    @POST("v3/auth/token")
    suspend fun exchangeCode(
        @Field("code") code: String,
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("redirect_uri") redirectUri: String,
        @Field("grant_type") grantType: String = "authorization_code",
        @Field("code_verifier") codeVerifier: String? = null,
    ): FeedlyTokenResponse

    @FormUrlEncoded
    @POST("v3/auth/token")
    suspend fun refreshToken(
        @Field("refresh_token") refreshToken: String,
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("grant_type") grantType: String = "refresh_token",
    ): FeedlyTokenResponse
}

fun createFeedlyApi(): FeedlyApi {
    val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }
    return Retrofit.Builder()
        .baseUrl("https://feedly.com/")
        .client(com.greader.neo.data.sharedHttpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()
        .create(FeedlyApi::class.java)
}
