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
import com.zello.sdk.example.app.ui.shared.types.IncomingLocationViewState

@Composable
fun LocationDialog(
	state: IncomingLocationViewState?,
	onDismiss: () -> Unit
) {
	var showImagePopup by remember { mutableStateOf(false) }
	LaunchedEffect(key1 = state) {
		showImagePopup = state != null // When a new non-null image arrives, show the popup
	}
	if (showImagePopup) {
		state?.locationText?.let {
			Dialog(onDismissRequest = {
				showImagePopup = false
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
						showImagePopup = false
						onDismiss()
					}) {
						Text(text = stringResource(id = R.string.close))
					}
				}
			}
		}
	}
}
