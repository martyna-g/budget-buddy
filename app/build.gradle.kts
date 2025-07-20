plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("androidx.navigation.safeargs")
    id("com.google.devtools.ksp")
    id("dagger.hilt.android.plugin")
    id("androidx.room")
    id("org.jetbrains.kotlin.plugin.compose")
    kotlin("kapt")
}

android {
    namespace = "pl.tinks.budgetbuddy"
    compileSdk = 35

    defaultConfig {
        applicationId = "pl.tinks.budgetbuddy"
        minSdk = 27
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "pl.tinks.budgetbuddy.CustomTestRunner"

        javaCompileOptions {
            annotationProcessorOptions {
                arguments += mapOf(
                    "room.schemaLocation" to "$projectDir/schemas",
                    "room.incremental" to "true"
                )
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        viewBinding = true
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildToolsVersion = "35.0.0"

    room {
        schemaDirectory("$projectDir/schemas")
    }
}

composeCompiler {
    reportsDestination = layout.buildDirectory.dir("compose_compiler")
    stabilityConfigurationFile = rootProject.layout.projectDirectory.file("stability_config.conf")
}

dependencies {
    val roomVersion = "2.6.1"
    val navVersion = "2.8.9"
    val hiltVersion = "2.55"
    val workVersion = "2.10.0"
    val composeBom = platform("androidx.compose:compose-bom:2025.06.01")

    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation("androidx.recyclerview:recyclerview:1.4.0")
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("androidx.viewpager2:viewpager2:1.1.0")

    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")

    implementation("androidx.navigation:navigation-fragment-ktx:$navVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navVersion")
    implementation("androidx.navigation:navigation-dynamic-features-fragment:$navVersion")
    implementation("androidx.navigation:navigation-compose:2.9.2")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    implementation("org.joda:joda-money:2.0.1")
    implementation("org.joda:joda-convert:3.0.1")

    implementation("com.google.dagger:hilt-android:$hiltVersion")
    implementation("androidx.work:work-runtime-ktx:$workVersion")
    implementation("androidx.work:work-gcm:$workVersion")
    implementation("androidx.hilt:hilt-work:1.2.0")
    ksp("androidx.hilt:hilt-compiler:1.2.0")
    ksp("com.google.dagger:hilt-android-compiler:$hiltVersion")

    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    implementation(composeBom)
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")
    implementation("androidx.activity:activity-compose:1.10.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.1")
    implementation("androidx.compose.material:material-icons-extended:1.7.8")

    testImplementation("junit:junit:4.13.2")
    testImplementation("androidx.room:room-testing:$roomVersion")
    testImplementation("org.mockito:mockito-core:5.16.1")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.4.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.1")

    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.6.1")
    androidTestImplementation("androidx.navigation:navigation-testing:$navVersion")
    androidTestImplementation("androidx.work:work-testing:$workVersion")
    androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.1")
    androidTestImplementation("com.google.dagger:hilt-android-testing:2.55")
    androidTestImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
    androidTestImplementation("com.squareup.okhttp3:okhttp:4.12.0")
    androidTestImplementation("androidx.test.espresso:espresso-idling-resource:3.6.1")
    kspAndroidTest("com.google.dagger:hilt-android-compiler:2.55")
    androidTestImplementation(composeBom)
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}