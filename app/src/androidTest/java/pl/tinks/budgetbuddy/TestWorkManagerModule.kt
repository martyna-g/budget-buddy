package pl.tinks.budgetbuddy

import android.content.Context
import androidx.work.Configuration
import androidx.work.WorkManager
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import pl.tinks.budgetbuddy.payment.WorkManagerModule
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class], replaces = [WorkManagerModule::class]
)
object TestWorkManagerModule {

    private var initialized = false

    @Provides
    @Singleton
    fun provideTestWorkManager(@ApplicationContext context: Context): WorkManager {
        if (!initialized) {
            WorkManager.initialize(
                context,
                Configuration.Builder()
                    .setMinimumLoggingLevel(android.util.Log.INFO)
                    .build()
            )
            initialized = true
        }
        return WorkManager.getInstance(context)
    }
}
