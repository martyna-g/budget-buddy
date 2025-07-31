package pl.tinks.budgetbuddy.payment

import android.Manifest
import android.os.Build
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PaymentNotificationSwitch(
    checked: Boolean, onNotificationChange: (Boolean) -> Unit
) {
    val notificationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)
    } else null

    LaunchedEffect(notificationPermission?.status) {
        if (notificationPermission?.status?.isGranted == true) {
            if (!checked) onNotificationChange(true)
        } else if (notificationPermission != null && checked) {
            onNotificationChange(false)
        }
    }
    Switch(checked = checked, onCheckedChange = { isChecked ->
        if (isChecked) {
            if (notificationPermission == null) {
                onNotificationChange(true)
            } else if (notificationPermission.status.isGranted) {
                onNotificationChange(true)
            } else {
                notificationPermission.launchPermissionRequest()
            }
        } else {
            onNotificationChange(false)
        }
    })
}
