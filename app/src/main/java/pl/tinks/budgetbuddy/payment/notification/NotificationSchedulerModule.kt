package pl.tinks.budgetbuddy.payment.notification

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class NotificationSchedulerModule {

    @Singleton
    @Binds
    abstract fun bindNotificationScheduler(notificationScheduler: NotificationSchedulerImpl): NotificationScheduler
}
