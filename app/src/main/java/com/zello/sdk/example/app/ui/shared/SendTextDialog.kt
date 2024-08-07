package com.zello.sdk.example.app.ui.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
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
import com.zello.sdk.example.app.R

@Composable
fun SendTextDialog(
	onDismiss: () -> Unit,
	onSend: (String) -> Unit
) {
	var text by remember { mutableStateOf("") }
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
					.align(Alignment.CenterHorizontally),
			)
			Button(
				modifier = Modifier.align(Alignment.CenterHorizontally),
				onClick = {
					onSend(text)
					onDismiss()
				}
			) {
				Text(text = stringResource(id = R.string.send))
			}
		}
	}
}
