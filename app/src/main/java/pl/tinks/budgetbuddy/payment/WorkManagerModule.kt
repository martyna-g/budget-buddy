package pl.tinks.budgetbuddy.payment

import android.content.Context
import androidx.work.WorkManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class WorkManagerModule {

    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context) = WorkManager.getInstance(context)
}
