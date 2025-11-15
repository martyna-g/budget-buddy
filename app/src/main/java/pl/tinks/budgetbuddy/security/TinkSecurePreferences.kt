package pl.tinks.budgetbuddy.security

import android.content.Context
import android.util.Base64
import com.google.crypto.tink.Aead
import com.google.crypto.tink.KeysetHandle
import com.google.crypto.tink.RegistryConfiguration
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.aead.AesGcmKeyManager
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class TinkSecurePreferences @Inject constructor(
    @ApplicationContext context: Context
) : SecurePreferences {

    private val prefsName = "secure_prefs"
    private val keysetName = "tink_keyset_aead"
    private val masterKeyUri = "android-keystore://budgetbuddy_master_key"

    private val aead: Aead
    private val plainPrefs = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)

    init {
        AeadConfig.register()
        val handle: KeysetHandle =
            AndroidKeysetManager.Builder().withSharedPref(context, keysetName, prefsName)
                .withKeyTemplate(AesGcmKeyManager.aes256GcmTemplate())
                .withMasterKeyUri(masterKeyUri).build().keysetHandle
        aead = handle.getPrimitive(RegistryConfiguration.get(), Aead::class.java)
    }

    override fun getString(key: String): String? {
        val b64 = plainPrefs.getString(key, null) ?: return null
        val blob = Base64.decode(b64, Base64.NO_WRAP)
        val plain = aead.decrypt(blob, key.toByteArray())
        return plain.decodeToString()
    }

    override fun putString(key: String, value: String) {
        val blob = aead.encrypt(value.encodeToByteArray(), key.toByteArray())
        val b64 = Base64.encodeToString(blob, Base64.NO_WRAP)
        plainPrefs.edit().putString(key, b64).apply()
    }
}
