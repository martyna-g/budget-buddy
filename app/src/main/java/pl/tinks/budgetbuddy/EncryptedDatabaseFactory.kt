package pl.tinks.budgetbuddy

import android.content.Context
import androidx.room.Room
import net.sqlcipher.database.SupportFactory
import pl.tinks.budgetbuddy.security.DatabasePassphraseProvider

class EncryptedDatabaseFactory(
    private val passphraseProvider: DatabasePassphraseProvider
) {
    fun create(context: Context): BudgetBuddyDatabase {
        val passphrase = passphraseProvider.getPassphrase()
        try {
            val factory = SupportFactory(passphrase)
            return Room.databaseBuilder(
                context, BudgetBuddyDatabase::class.java, "budget_buddy_database"
            ).openHelperFactory(factory).build()

        } finally {
            passphrase.fill(0)
        }
    }
}
