// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.7.3" apply false
    id("org.jetbrains.kotlin.android") version "2.1.10" apply false
    id("com.google.devtools.ksp") version "2.1.10-1.0.31" apply false
    id("com.google.dagger.hilt.android") version "2.55" apply false
    id("androidx.room") version "2.6.1" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.10"
}

buildscript {
    repositories {
        google()
    }
    dependencies {
        val navVersion = "2.8.9"
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$navVersion")
    }
}
