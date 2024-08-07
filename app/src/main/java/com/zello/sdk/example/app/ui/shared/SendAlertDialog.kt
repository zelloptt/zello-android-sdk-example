package com.zello.sdk.example.app.ui.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.zello.sdk.ZelloAlertMessage
import com.zello.sdk.example.app.R

@Composable
fun SendAlertDialog(
	canSelectLevel: Boolean,
	onDismiss: () -> Unit,
	onSend: (String, ZelloAlertMessage.ChannelLevel?) -> Unit
) {
	var text by remember { mutableStateOf("") }
	var selectedLevel by remember {
		mutableStateOf(if (canSelectLevel) ZelloAlertMessage.ChannelLevel.ALL else null)
	}
	Dialog(onDismissRequest = {
		onDismiss()
	}) {
		Column(modifier =
		Modifier
			.fillMaxWidth(0.75f)
			.background(color = Color.White)
		) {
			BasicTextField(
				value = text,
				onValueChange = {
					text = it
				},
				modifier = Modifier
					.fillMaxWidth(0.9f)
					.padding(vertical = 16.dp)
					.background(color = Color.LightGray)
					.padding(8.dp)
					.align(Alignment.CenterHorizontally)
			)
			if (canSelectLevel) {
				Row {
					Switch(
						checked = selectedLevel == ZelloAlertMessage.ChannelLevel.CONNECTED,
						onCheckedChange = {
							if (it) {
								selectedLevel = ZelloAlertMessage.ChannelLevel.CONNECTED
							} else {
								selectedLevel = ZelloAlertMessage.ChannelLevel.ALL
							}
						},
						modifier = Modifier.padding(16.dp)
					)
					Text(
						text = stringResource(
							id =
							if (selectedLevel == ZelloAlertMessage.ChannelLevel.CONNECTED)
								R.string.connected_users
							else
								R.string.all_users
						),
						modifier = Modifier
							.padding(16.dp)
							.align(Alignment.CenterVertically)
					)
				}

			}
			Button(
				modifier = Modifier.align(Alignment.CenterHorizontally),
				onClick = {
					onSend(text, selectedLevel)
					onDismiss()
				}
			) {
				Text(text = stringResource(id = R.string.send))
			}
		}
	}
}
