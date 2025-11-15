package pl.tinks.budgetbuddy.security

import android.util.Base64
import java.security.SecureRandom
import javax.inject.Inject

class DefaultDatabasePassphraseProvider @Inject constructor(
    private val securePrefs: SecurePreferences
) : DatabasePassphraseProvider {

    companion object { private const val PREF_KEY = "db_passphrase_b64" }

    private val cached: ByteArray by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        val existing = securePrefs.getString(PREF_KEY)
        existing?.let { Base64.decode(it, Base64.NO_WRAP) } ?: run {
            val fresh = ByteArray(32).also { SecureRandom().nextBytes(it) }
            val encoded = Base64.encodeToString(fresh, Base64.NO_WRAP)
            securePrefs.putString(PREF_KEY, encoded)
            fresh
        }
    }

    override fun getPassphrase(): ByteArray = cached.copyOf()
}
