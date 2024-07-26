package pl.tinks.budgetbuddy.payment

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import pl.tinks.budgetbuddy.R
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaymentNotificationPresenter @Inject constructor(val context: Context) {

    companion object {
        const val CHANNEL_ID = "payment_notifications"
    }

    fun sendPaymentReminderNotification(title: String, id: UUID) {

        val notificationId = id.hashCode()
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notifications)
            .setContentTitle(context.getString(R.string.payment_reminder_title))
            .setContentText(context.getString(R.string.payment_reminder_content, title.uppercase()))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            notify(notificationId, builder.build())
        }
    }

    fun createNotificationChannel() {
        val name = context.getString(R.string.payment_notification_channel_name)
        val descriptionText = context.getString(R.string.payment_notification_channel_description)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

}
