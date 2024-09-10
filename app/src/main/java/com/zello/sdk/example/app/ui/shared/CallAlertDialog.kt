package com.zello.sdk.example.app.ui.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.zello.sdk.example.app.R
import com.zello.sdk.example.app.ui.shared.types.IncomingAlertViewState

@Composable
fun CallAlertDialog(
	state: IncomingAlertViewState?,
	onDismiss: () -> Unit
) {
	var showPopup by remember { mutableStateOf(false) }
	LaunchedEffect(key1 = state) {
		showPopup = state != null // When a new non-null alert arrives, show the popup
	}
	if (showPopup) {
		state?.text?.let {
			Dialog(onDismissRequest = {
				showPopup = false
				onDismiss()
			}) {
				Column {
					Text(
						text = it,
						modifier = Modifier
							.background(
								color = Color.White,
							)
							.padding(16.dp)
					)
					Button(onClick = {
						showPopup = false
						onDismiss()
					}) {
						Text(text = stringResource(id = R.string.close))
					}
				}
			}
		}
	}
}
