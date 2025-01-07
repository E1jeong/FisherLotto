package com.queentech.presentation.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Dialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.queentech.presentation.component.dialog.BaseDialog
import com.queentech.presentation.component.dialog.model.DialogButton
import com.queentech.presentation.component.dialog.model.DialogContent
import com.queentech.presentation.component.dialog.model.DialogText
import com.queentech.presentation.component.dialog.model.DialogTitle

enum class PermissionState {
    Granted, Denied, NeedsSpecialPermission
}

@RequiresApi(Build.VERSION_CODES.R)
object PermissionHelper {

    fun checkPermissions(context: Context, permissions: List<String>): PermissionState {
        val specialPermission =
            permissions.find { it == android.Manifest.permission.MANAGE_EXTERNAL_STORAGE }
        if (specialPermission != null) {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) PermissionState.Granted else PermissionState.NeedsSpecialPermission
            } else {
                PermissionState.Granted
            }
        }

        return if (permissions.all { permission ->
                ContextCompat.checkSelfPermission(
                    context,
                    permission
                ) == PackageManager.PERMISSION_GRANTED
            }) {
            PermissionState.Granted
        } else {
            PermissionState.Denied
        }
    }

    fun shouldShowRationale(activity: Activity, permissions: List<String>): Boolean {
        return permissions.any { permission ->
            ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
        }
    }

    fun openAppSettings(context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
        }
        context.startActivity(intent)
    }

    fun openManageAllFilesSettings(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
            context.startActivity(intent)
        } else {
            openAppSettings(context)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun permissionRequest(
    permissions: List<String>,
    rationaleTitle: String,
    rationaleText: String
): PermissionState {
    val context = LocalContext.current
    var showRationale by remember { mutableStateOf(false) }
    var permissionState by remember { mutableStateOf(PermissionState.Denied) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissionsResult ->
            permissionState = if (permissionsResult.all { it.value }) {
                PermissionState.Granted
            } else {
                PermissionState.Denied
            }
        }
    )

    LaunchedEffect(permissions) {
        permissionState = PermissionHelper.checkPermissions(context, permissions)
        when (permissionState) {
            PermissionState.Granted -> {} // Do nothing for Granted

            PermissionState.Denied -> {
                if (PermissionHelper.shouldShowRationale(context as Activity, permissions)) {
                    showRationale = true
                } else {
                    permissionLauncher.launch(permissions.toTypedArray())
                }
            }

            PermissionState.NeedsSpecialPermission -> {
                showRationale = true
            }

        }
    }

    if (showRationale) {
        RationaleDialog(
            title = rationaleTitle,
            text = rationaleText,
            onConfirm = {
                showRationale = false
                if (permissionState == PermissionState.NeedsSpecialPermission) {
                    PermissionHelper.openManageAllFilesSettings(context)
                } else {
                    PermissionHelper.openAppSettings(context)
                }
            },
            onDismiss = {
                showRationale = false
                permissionState = PermissionState.Denied
            }
        )
    }

    return permissionState
}

@Composable
private fun RationaleDialog(
    title: String,
    text: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        BaseDialog(
            dialogTitle = DialogTitle.Default(text = title),
            dialogContent = DialogContent.Default(DialogText.Default(text = text)),
            buttons = listOf(
                DialogButton.UnderlinedText(
                    title = "설정으로 이동",
                    action = onConfirm
                ),
                DialogButton.Primary(
                    title = "취소",
                    action = onDismiss
                )
            )
        )
    }
}