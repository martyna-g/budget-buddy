package pl.tinks.budgetbuddy.security

interface DatabasePassphraseProvider {
    fun getPassphrase(): ByteArray
}
