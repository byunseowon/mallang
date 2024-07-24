package com.chill.mallang.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import com.chill.mallang.R

@Composable
fun BackConfirmHandler(
    isBackPressed: Boolean = true,
    onConfirm: () -> Unit,
    onDismiss: (Boolean) -> Unit,
    content: String = stringResource(R.string.confirm_dialog_default_message),
) {
    val showDialog = remember {
        mutableStateOf(false)
    }
    LaunchedEffect(isBackPressed) {
        showDialog.value = isBackPressed
    }

    if (showDialog.value) {
        BackConfirmDialog(
            onConfirm = {
                onConfirm()
            },
            onDismiss = {
                onDismiss(false)
            },
            content = content
        )
    }
}