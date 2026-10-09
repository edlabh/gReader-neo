package com.greader.neo.data.auth

import android.content.Context
import android.net.Uri
import android.util.Base64
import android.util.Log
import com.greader.neo.BuildConfig
import com.greader.neo.R
import com.greader.neo.data.feedly.FeedlyApi
import com.greader.neo.data.feedly.FeedlyTokenResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.security.SecureRandom

sealed interface AuthState {
    data object Disconnected : AuthState
    data object Connecting : AuthState
    data class Error(val message: String) : AuthState
    data class Connected(val email: String?) : AuthState
}

/**
 * Fluxo OAuth2 do Feedly (Authorization Code).
 *
 * - App OAuth próprio, configurado via `local.properties`
 *   (`feedly.clientId` / `feedly.clientSecret` → BuildConfig).
 * - Redirect: `greader://feedly` (deep link na MainActivity).
 * - PKCE opcional (constante [PKCE_ENABLED]); o Feedly ainda não o documenta.
 * - Tokens armazenados criptografados no Keystore ([SecureTokenStore]).
 */
class AuthManager(
    private val api: FeedlyApi,
    private val tokenStore: SecureTokenStore,
    private val scope: CoroutineScope,
    private val context: Context,
) {

    private val _state = MutableStateFlow<AuthState>(
        if (tokenStore.hasSession()) AuthState.Connected(tokenStore.userEmail) else AuthState.Disconnected,
    )
    val state: StateFlow<AuthState> = _state

    private var pendingVerifier: String? = null

    /** Credenciais efetivas: personalizadas (no app) ou o padrão embutido. */
    private val clientId: String
        get() = tokenStore.clientId?.takeIf { it.isNotBlank() } ?: BuildConfig.FEEDLY_CLIENT_ID

    private val clientSecret: String
        get() = tokenStore.clientSecret?.takeIf { it.isNotBlank() } ?: BuildConfig.FEEDLY_CLIENT_SECRET

    val isConfigured: Boolean
        get() = clientId.isNotBlank() && clientSecret.isNotBlank()

    val hasCustomCredentials: Boolean
        get() = !tokenStore.clientId.isNullOrBlank() || !tokenStore.clientSecret.isNullOrBlank()

    val customClientId: String?
        get() = tokenStore.clientId

    val customClientSecret: String?
        get() = tokenStore.clientSecret

    /** Salva credenciais personalizadas e encerra a sessão (novo login necessário). */
    fun saveCredentials(newClientId: String, newClientSecret: String) {
        tokenStore.clientId = newClientId.trim()
        tokenStore.clientSecret = newClientSecret.trim()
        logout()
    }

    /** Volta a usar as credenciais padrão embutidas. */
    fun useDefaultCredentials() {
        tokenStore.clearCredentials()
        logout()
    }

    val userId: String?
        get() = tokenStore.userId

    fun setUserId(id: String) {
        tokenStore.userId = id
    }

    fun buildAuthorizeUrl(): String? {
        if (!isConfigured) return null
        val state = randomToken(24)
        tokenStore.pendingState = state
        // Parâmetros idênticos ao app original (o scope é obrigatório):
        // https://feedly.com/v3/auth/auth?scope=…&redirect_uri=greader://feedly&response_type=code&client_id=…
        val scope = Uri.encode(SCOPE)
        val base = "https://feedly.com/v3/auth/auth" +
            "?scope=$scope" +
            "&redirect_uri=$REDIRECT_URI" +
            "&response_type=code" +
            "&client_id=$clientId" +
            "&state=$state"
        return if (PKCE_ENABLED) {
            val verifier = randomToken(64)
            pendingVerifier = verifier
            "$base&code_challenge=${sha256Base64Url(verifier)}&code_challenge_method=S256"
        } else {
            base
        }
    }

    fun handleRedirect(uri: Uri) {
        if (uri.scheme != "greader" || uri.host != "feedly") return

        val code = uri.getQueryParameter("code")
        val returnedState = uri.getQueryParameter("state")
        val error = uri.getQueryParameter("error")

        // Valida o state apenas quando o Feedly o devolve (o fluxo original não o envia).
        val stateOk = returnedState == null || returnedState == tokenStore.pendingState

        when {
            error != null -> _state.value =
                AuthState.Error(context.getString(R.string.login_error_denied, error))

            code.isNullOrBlank() -> Unit
            !stateOk -> _state.value =
                AuthState.Error(context.getString(R.string.login_error_state))

            else -> {
                _state.value = AuthState.Connecting
                scope.launch {
                    runCatching { exchangeCode(code) }
                        .onSuccess { _state.value = AuthState.Connected(tokenStore.userEmail) }
                        .onFailure {
                            _state.value = AuthState.Error(
                                it.message ?: context.getString(R.string.login_error_generic),
                            )
                        }
                }
            }
        }
    }

    private suspend fun exchangeCode(code: String) {
        val response = try {
            api.exchangeCode(
                code = code,
                clientId = clientId,
                clientSecret = clientSecret,
                redirectUri = REDIRECT_URI,
                codeVerifier = pendingVerifier,
            )
        } catch (e: retrofit2.HttpException) {
            val detail = runCatching {
                e.response()?.errorBody()?.string()?.take(300)
            }.getOrNull().orEmpty()
            throw IllegalStateException("HTTP ${e.code()} ao trocar o código$detail".trim())
        }
        persist(response)
        tokenStore.pendingState = null
        pendingVerifier = null

        val profile = api.profile("OAuth ${response.accessToken}")
        tokenStore.userId = profile.id
        tokenStore.userEmail = profile.email
    }

    /** Retorna um access token válido, renovando se necessário. */
    suspend fun validAccessToken(): String? {
        val token = tokenStore.accessToken
        if (!token.isNullOrBlank() && System.currentTimeMillis() < tokenStore.expiresAt) return token

        val refresh = tokenStore.refreshToken ?: return null
        return runCatching {
            val response = api.refreshToken(
                refreshToken = refresh,
                clientId = clientId,
                clientSecret = clientSecret,
            )
            persist(response)
            response.accessToken
        }
            .onFailure { Log.w(TAG, "Falha ao renovar token: ${it.message}") }
            .getOrNull()
    }

    fun invalidateAccessToken() {
        tokenStore.accessToken = null
        tokenStore.expiresAt = 0
    }

    fun logout() {
        tokenStore.clearSession()
        _state.value = AuthState.Disconnected
    }

    private fun persist(response: FeedlyTokenResponse) {
        tokenStore.accessToken = response.accessToken
        response.refreshToken?.let { tokenStore.refreshToken = it }
        tokenStore.expiresAt = System.currentTimeMillis() +
            (response.expiresIn ?: 3600L) * 1000L - 60_000L
    }

    private fun randomToken(bytes: Int): String {
        val buffer = ByteArray(bytes)
        SecureRandom().nextBytes(buffer)
        return Base64.encodeToString(
            buffer,
            Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING,
        )
    }

    private fun sha256Base64Url(value: String): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(value.toByteArray(Charsets.US_ASCII))
        return Base64.encodeToString(
            digest,
            Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING,
        )
    }

    companion object {
        const val REDIRECT_URI = "greader://feedly"
        private const val SCOPE = "https://cloud.feedly.com/subscriptions"
        private const val PKCE_ENABLED = false // habilite quando o Feedly suportar PKCE
        private const val TAG = "AuthManager"
    }
}
