package pl.tinks.budgetbuddy.security

interface SecurePreferences {
    fun getString(key: String): String?
    fun putString(key: String, value: String)
}
