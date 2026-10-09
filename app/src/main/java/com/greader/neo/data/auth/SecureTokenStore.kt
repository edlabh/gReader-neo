package com.greader.neo.data.auth

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Armazenamento seguro de tokens e credenciais.
 *
 * Usa o Android Keystore (AES-256/GCM) diretamente — evita a biblioteca
 * `androidx.security:security-crypto`, que foi descontinuada.
 */
class SecureTokenStore(context: Context) {

    private val prefs = context.getSharedPreferences("secure_auth", Context.MODE_PRIVATE)

    var accessToken: String?
        get() = decryptPref(KEY_ACCESS)
        set(value) = writePref(KEY_ACCESS, value)

    var refreshToken: String?
        get() = decryptPref(KEY_REFRESH)
        set(value) = writePref(KEY_REFRESH, value)

    var userId: String?
        get() = decryptPref(KEY_USER_ID)
        set(value) = writePref(KEY_USER_ID, value)

    var userEmail: String?
        get() = decryptPref(KEY_EMAIL)
        set(value) = writePref(KEY_EMAIL, value)

    var expiresAt: Long
        get() = prefs.getLong(KEY_EXPIRES, 0L)
        set(value) = prefs.edit().putLong(KEY_EXPIRES, value).apply()

    /** Estado do OAuth pendente (persistido para sobreviver à recriação do app). */
    var pendingState: String?
        get() = decryptPref(KEY_PENDING_STATE)
        set(value) = writePref(KEY_PENDING_STATE, value)

    /** Credenciais OAuth configuradas pelo usuário (opcionais; sobrepõem o padrão). */
    var clientId: String?
        get() = decryptPref(KEY_CLIENT_ID)
        set(value) = writePref(KEY_CLIENT_ID, value)

    var clientSecret: String?
        get() = decryptPref(KEY_CLIENT_SECRET)
        set(value) = writePref(KEY_CLIENT_SECRET, value)

    fun hasSession(): Boolean = !refreshToken.isNullOrBlank() || !accessToken.isNullOrBlank()

    /** Limpa a sessão (tokens), preservando as credenciais OAuth configuradas. */
    fun clearSession() {
        listOf(KEY_ACCESS, KEY_REFRESH, KEY_USER_ID, KEY_EMAIL, KEY_EXPIRES, KEY_PENDING_STATE)
            .forEach { prefs.edit().remove(it).apply() }
    }

    /** Remove as credenciais personalizadas (volta ao padrão). */
    fun clearCredentials() {
        listOf(KEY_CLIENT_ID, KEY_CLIENT_SECRET)
            .forEach { prefs.edit().remove(it).apply() }
    }

    fun clear() = prefs.edit().clear().apply()

    // ---------- criptografia ----------

    private fun writePref(key: String, value: String?) {
        if (value == null) {
            prefs.edit().remove(key).apply()
        } else {
            prefs.edit().putString(key, encrypt(value)).apply()
        }
    }

    private fun decryptPref(key: String): String? {
        val data = prefs.getString(key, null) ?: return null
        return runCatching { decrypt(data) }.getOrNull()
    }

    private fun secretKey(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
        (keyStore.getEntry(KEY_ALIAS, null) as? KeyStore.SecretKeyEntry)?.let { return it.secretKey }

        val generator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
        generator.init(
            KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .build(),
        )
        return generator.generateKey()
    }

    private fun encrypt(plain: String): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey())
        val encrypted = cipher.doFinal(plain.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(cipher.iv, Base64.NO_WRAP) +
            ":" + Base64.encodeToString(encrypted, Base64.NO_WRAP)
    }

    private fun decrypt(data: String): String {
        val parts = data.split(":", limit = 2)
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(
            Cipher.DECRYPT_MODE,
            secretKey(),
            GCMParameterSpec(128, Base64.decode(parts[0], Base64.NO_WRAP)),
        )
        return String(cipher.doFinal(Base64.decode(parts[1], Base64.NO_WRAP)), Charsets.UTF_8)
    }

    private companion object {
        const val ANDROID_KEYSTORE = "AndroidKeyStore"
        const val KEY_ALIAS = "greader_neo_auth_key"
        const val TRANSFORMATION = "AES/GCM/NoPadding"
        const val KEY_ACCESS = "access_token"
        const val KEY_REFRESH = "refresh_token"
        const val KEY_USER_ID = "user_id"
        const val KEY_EMAIL = "user_email"
        const val KEY_EXPIRES = "expires_at"
        const val KEY_PENDING_STATE = "pending_oauth_state"
        const val KEY_CLIENT_ID = "oauth_client_id"
        const val KEY_CLIENT_SECRET = "oauth_client_secret"
    }
}
