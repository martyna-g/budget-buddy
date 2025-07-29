package pl.tinks.budgetbuddy

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        var keepSplash = true
        val delayTime = 800L

        installSplashScreen().setKeepOnScreenCondition { keepSplash }
        Handler(Looper.getMainLooper()).postDelayed({ keepSplash = false }, delayTime)

        super.onCreate(savedInstanceState)

        setContent {
            MainScreen()
        }
    }
}
